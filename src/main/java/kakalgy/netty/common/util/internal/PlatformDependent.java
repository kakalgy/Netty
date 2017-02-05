package kakalgy.netty.common.util.internal;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import java.util.regex.Pattern;

import common.src.main.java.io.netty.util.internal.PlatformDependent0;
import kakalgy.netty.common.util.internal.logging.InternalLogger;
import kakalgy.netty.common.util.internal.logging.InternalLoggerFactory;

/**
 * 检测系统工具PlatformDependent</br>
 * </br>
 * 
 * Utility that detects various properties specific to the current runtime
 * environment, such as Java version and the availability of the
 * {@code sun.misc.Unsafe} object.
 * <p>
 * You can disable the use of {@code sun.misc.Unsafe} if you specify the system
 * property <strong>io.netty.noUnsafe</strong>.
 * 
 * 用来检测运行时系统的属性的工具类，可以通过设置io.netty.noUnsafe属性禁止使用sun.misc.Unsafe对象
 * System.setProperty("io.netty.allocator.type","pooled");
 * System.setPropperty("io.netty.noUnsafe",false) -XX:MaxDirectMemorySize
 *
 * 该类实现实现：class加载的时候获取系统的属性保存到statuc变量中，然后访问的时候直接返回这个变量
 * 
 * @author Administrator
 *
 */
public final class PlatformDependent {

	private static final InternalLogger logger = InternalLoggerFactory.getInstance(PlatformDependent.class);

	/**
	 * 最大直接内存大小参数 表达式
	 */
	private static final Pattern MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN = Pattern.compile("\\s*-XX:MaxDirectMemorySize\\s*=\\s*([0-9]+)\\s*([kKmMgG]?)\\s*$");
	/*
	 * this must be initialized before any code below triggers initialization of
	 * PlatformDependent0
	 */
	private static final boolean IS_EXPLICIT_NO_UNSAFE = explicitNoUnsafe0();

	private static final boolean IS_ANDROID = isAndroid0();
	private static final boolean IS_WINDOWS = isWindows0();
	private static volatile Boolean IS_ROOT;

	private static final int JAVA_VERSION = javaVersion0();

	private static final boolean CAN_ENABLE_TCP_NODELAY_BY_DEFAULT = !isAndroid();

	private static final boolean HAS_UNSAFE = hasUnsafe0();

	/**
	 * 显示的NoUnsafe，非隐示
	 * 
	 * @return
	 */
	private static boolean explicitNoUnsafe0() {
		// 若没有io.netty.noUnsafe的值，则返回FALSE
		final boolean noUnsafe = SystemPropertyUtil.getJavaSystemPropertyBoolean("io.netty.noUnsafe", false);
		logger.debug("-Dio.netty.noUnsafe: {}", noUnsafe);

		if (noUnsafe) {
			logger.debug("sun.misc.Unsafe: unavailable (io.netty.noUnsafe)");
			return true;
		}

		// Legacy properties
		boolean tryUnsafe;
		if (SystemPropertyUtil.containsJavaSystemProperty("io.netty.tryUnsafe")) {
			tryUnsafe = SystemPropertyUtil.getJavaSystemPropertyBoolean("io.netty.tryUnsafe", true);
		} else {
			tryUnsafe = SystemPropertyUtil.getJavaSystemPropertyBoolean("org.jboss.netty.tryUnsafe", true);
		}

		if (!tryUnsafe) {
			logger.debug("sun.misc.Unsafe: unavailable (io.netty.tryUnsafe/org.jboss.netty.tryUnsafe)");
			return true;
		}

		return false;
	}

	private static boolean isAndroid0() {
		boolean isAndroid;
		try {
			Class.forName("android.app.Application", false, getSystemClassLoader());
			isAndroid = true;
		} catch (Throwable e) {
			// TODO: handle exception
			// Failed to load the class uniquely available in Android.
			isAndroid = false;
		}

		if (isAndroid) {
			logger.debug("Platform: Android");
		}
		return isAndroid;
	}

	/**
	 * Return the system {@link ClassLoader}.
	 */
	public static ClassLoader getSystemClassLoader() {
		return PlatformDependent0.getSystemClassLoader();
	}

	private static boolean isWindows0() {
		boolean isWindows = SystemPropertyUtil.getJavaSystemPropertyString("os.name", "").toLowerCase(Locale.US).contains("win");
		if (isWindows) {
			logger.debug("Platform: Windows");
		}
		return isWindows;
	}

	private static int javaVersion0() {
		final int majorVersion;

		if (getIS_ANDROID()) {
			majorVersion = 6;
		} else {
			majorVersion = majorVersionFromJavaSpecificationVersion();
		}

		logger.debug("Java version: {}", majorVersion);

		return majorVersion;
	}

	static int majorVersionFromJavaSpecificationVersion() {
		try {
			final String javaSpecVersion = AccessController.doPrivileged(new PrivilegedAction<String>() {
				public String run() {
					// TODO Auto-generated method stub
					return System.getProperty("java.specification.version");
				}
			});
			return majorVersion(javaSpecVersion);
		} catch (SecurityException e) {
			// TODO: handle exception
			logger.debug("security exception while reading java.specification.version", e);
			return 6;
		}
	}

	static int majorVersion(final String javaSpecVersion) {
		final String[] components = javaSpecVersion.split("\\.");
		final int[] version = new int[components.length];
		for (int i = 0; i < components.length; i++) {
			version[i] = Integer.parseInt(components[i]);
		}

		if (version[0] == 1) {
			assert version[1] >= 6;
			return version[1];
		} else {
			return version[0];
		}
	}

	private static boolean hasUnsafe0() {
		if (getIS_ANDROID()) {
			logger.debug("sun.misc.Unsafe: unavailable (Android)");
			return false;
		}

		if (IS_EXPLICIT_NO_UNSAFE) {
			return false;
		}

		try {
			boolean hasUnsafe = PlatformDependent0.hasUnsafe();
		} catch (Throwable ignored) {
			// Probably failed to initialize PlatformDependent0.
			return false;
		}
	}

	/********** get/set函数 *****************/
	/**
	 * Returns {@code true} if and only if the current platform is Android
	 */
	public static boolean getIS_ANDROID() {
		return IS_ANDROID;
	}

	/**
	 * Return {@code true} if the JVM is running on Windows
	 */
	public static boolean getIS_WINDOWS() {
		return IS_WINDOWS;
	}

	static boolean isExplicitNoUnsafe() {
		return IS_EXPLICIT_NO_UNSAFE;
	}
}
