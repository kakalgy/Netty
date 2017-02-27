package kakalgy.netty.handler.ssl;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.tomcat.jni.Library;
import org.apache.tomcat.jni.SSL;

import handler.src.main.java.io.netty.handler.ssl.OpenSslEngine;
import kakalgy.netty.common.util.internal.NativeLibraryLoader;
import kakalgy.netty.common.util.internal.SystemPropertyUtil;
import kakalgy.netty.common.util.internal.logging.InternalLogger;
import kakalgy.netty.common.util.internal.logging.InternalLoggerFactory;

/**
 * Tells if <a href="http://netty.io/wiki/forked-tomcat-native.html">
 * {@code netty-tcnative}</a> and its OpenSSL support are available.
 * 
 * @author Administrator
 *
 */
public final class OpenSsl {

	private static final InternalLogger logger = InternalLoggerFactory.getInstance(OpenSsl.class);

	private static final String LINUX = "linux";
	private static final String UNKNOWN = "unknown";
	private static final Throwable UNAVAILABILITY_CAUSE;

	/**
	 * 密码套件
	 */
	static final Set<String> AVAILABLE_CIPHER_SUITES;
	private static final Set<String> AVAILABLE_OPENSSL_CIPHER_SUITES;
	private static final Set<String> AVAILABLE_JAVA_CIPHER_SUITES;

	/**
	 * 实现指定密钥管理算法的 KeyManagerFactory 对象
	 */
	private static final boolean SUPPORTS_KEYMANAGER_FACTORY;
	private static final boolean USE_KEYMANAGER_FACTORY;

	/**
	 * 协议Protocols
	 */
	static final String PROTOCOL_SSL_V2_HELLO = "SSLv2Hello";
	static final String PROTOCOL_SSL_V2 = "SSLv2";
	static final String PROTOCOL_SSL_V3 = "SSLv3";
	static final String PROTOCOL_TLS_V1 = "TLSv1";
	static final String PROTOCOL_TLS_V1_1 = "TLSv1.1";
	static final String PROTOCOL_TLS_V1_2 = "TLSv1.2";

	private static final String[] SUPPORTED_PROTOCOLS = { PROTOCOL_SSL_V2_HELLO, PROTOCOL_SSL_V2, PROTOCOL_SSL_V3, PROTOCOL_TLS_V1, PROTOCOL_TLS_V1_1, PROTOCOL_TLS_V1_2 };

	static final Set<String> SUPPORTED_PROTOCOLS_SET = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(SUPPORTED_PROTOCOLS)));

	static {
		Throwable cause = null;

		// Test if netty-tcnative is in the classpath first.
		// 测试netty-tcnative是否在classpath中
		try {
			Class.forName("org.apache.tomcat.jni.SSL", false, OpenSsl.class.getClassLoader());
		} catch (ClassNotFoundException e) {
			// TODO: handle exception
			cause = e;
			logger.debug("netty-tcnative not in the classpath; " + OpenSslEngine.class.getSimpleName() + " will be unavailable.");
		}

		// If in the classpath, try to load the native library and initialize
		// netty-tcnative.
		if (cause == null) {
			try {
				// The JNI library was not already loaded. Load it now.
				loadTcNative();
			} catch (Throwable t) {
				// TODO: handle exception
				cause = t;
				logger.debug("Failed to load netty-tcnative; " + OpenSslEngine.class.getSimpleName() + " will be unavailable, unless the "
						+ "application has already loaded the symbols by some other means. " + "See http://netty.io/wiki/forked-tomcat-native.html for more information.", t);

			}

			try {
				initializeTcNative();
				// The library was initialized successfully. If loading the
				// library failed above,
				// reset the cause now since it appears that the library was
				// loaded by some other means
				cause = null;
			} catch (Throwable t) {
				// TODO: handle exception
				if (cause == null) {
					cause = t;
				}
				logger.debug("Failed to initialize netty-tcnative; " + OpenSslEngine.class.getSimpleName() + " will be unavailable. "
						+ "See http://netty.io/wiki/forked-tomcat-native.html for more information.", t);
			}

		}

		if (cause == null && !isNettyTcnative()) {

		}

	}

	/**
	 * 构造函数
	 */
	private OpenSsl() {

	}

	/**
	 * Load netty-tcnative 的库
	 * 
	 * @throws Exception
	 */
	private static void loadTcNative() throws Exception {
		String os = normalizeOs(SystemPropertyUtil.getJavaSystemPropertyString("os.name", ""));
		String arch = normalizeArch(SystemPropertyUtil.getJavaSystemPropertyString("os.arch", ""));

		Set<String> libNames = new LinkedHashSet<String>(3);
		// First, try loading the platform-specific library. Platform-specific
		// libraries will be available if using a tcnative uber jar.
		libNames.add("netty-tcnative-" + os + '-' + arch);
		if (LINUX.equalsIgnoreCase(os)) {
			// Fedora SSL lib so naming (libssl.so.10 vs libssl.so.1.0.0)..
			libNames.add("netty-tcnative-" + os + '-' + arch + "-fedora");
		}
		// finally the default library.
		libNames.add("netty-tcnative");

		NativeLibraryLoader.loadFirstAvailable(SSL.class.getClassLoader(), libNames.toArray(new String[libNames.size()]));

	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	private static String normalizeOs(String value) {
		value = normalize(value);
		if (value.startsWith("aix")) {
			return "aix";
		}
		if (value.startsWith("hpux")) {
			return "hpux";
		}
		if (value.startsWith("os400")) {
			// Avoid the names such as os4000
			if (value.length() <= 5 || !Character.isDigit(value.charAt(5))) {
				return "os400";
			}
		}
		if (value.startsWith(LINUX)) {
			return LINUX;
		}
		if (value.startsWith("macosx") || value.startsWith("osx")) {
			return "osx";
		}
		if (value.startsWith("freebsd")) {
			return "freebsd";
		}
		if (value.startsWith("openbsd")) {
			return "openbsd";
		}
		if (value.startsWith("netbsd")) {
			return "netbsd";
		}
		if (value.startsWith("solaris") || value.startsWith("sunos")) {
			return "sunos";
		}
		if (value.startsWith("windows")) {
			return "windows";
		}

		return UNKNOWN;
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	private static String normalizeArch(String value) {
		value = normalize(value);
		if (value.matches("^(x8664|amd64|ia32e|em64t|x64)$")) {
			return "x86_64";
		}
		if (value.matches("^(x8632|x86|i[3-6]86|ia32|x32)$")) {
			return "x86_32";
		}
		if (value.matches("^(ia64|itanium64)$")) {
			return "itanium_64";
		}
		if (value.matches("^(sparc|sparc32)$")) {
			return "sparc_32";
		}
		if (value.matches("^(sparcv9|sparc64)$")) {
			return "sparc_64";
		}
		if (value.matches("^(arm|arm32)$")) {
			return "arm_32";
		}
		if ("aarch64".equals(value)) {
			return "aarch_64";
		}
		if (value.matches("^(ppc|ppc32)$")) {
			return "ppc_32";
		}
		if ("ppc64".equals(value)) {
			return "ppc_64";
		}
		if ("ppc64le".equals(value)) {
			return "ppcle_64";
		}
		if ("s390".equals(value)) {
			return "s390_32";
		}
		if ("s390x".equals(value)) {
			return "s390_64";
		}

		return UNKNOWN;
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	private static String normalize(String value) {
		return value.toLowerCase(Locale.US).replaceAll("[^a-z0-9]+", "");
	}

	/**
	 * 初始化TcNative的库
	 * 
	 * @throws Exception
	 */
	private static void initializeTcNative() throws Exception {
		Library.initialize("provided");
		SSL.initialize(null);
	}

	/**
	 * 
	 * @return
	 */
	private static boolean isNettyTcnative() {
		return AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
			public Boolean run() {
				// TODO Auto-generated method stub
				InputStream is = null;
				try {
					is = Apr
				} catch (Throwable ignore) {
					// TODO: handle exception
					return false;
				}finally {
					
				}
				
			}
		});
	}
}
