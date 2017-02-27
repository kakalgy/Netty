package kakalgy.netty.common.util.internal;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Locale;

import kakalgy.netty.common.util.internal.logging.InternalLogger;
import kakalgy.netty.common.util.internal.logging.InternalLoggerFactory;

/**
 * Helper class to load JNI resources.
 * 
 * </br>
 * </br>
 *
 * Load JNI资源的帮助类
 */
public final class NativeLibraryLoader {

	private static final InternalLogger logger = InternalLoggerFactory.getInstance(NativeLibraryLoader.class);

	/**
	 * 本地的资源路径
	 */
	private static final String NATIVE_RESOURCE_HOME = "META-INF/native/";
	/**
	 * 操作系统类型
	 */
	private static final String OSNAME;
	/**
	 * 工作路径
	 */
	private static final File WORKDIR;
	/**
	 * 在载入Lib之后是否删除本地Lib
	 */
	private static final boolean DELETE_NATIVE_LIB_AFTER_LOADING;

	static {
		OSNAME = SystemPropertyUtil.getJavaSystemPropertyString("os.name", "").toLowerCase(Locale.US).replaceAll("[^a-z0-9]+", "");

		String workdir = SystemPropertyUtil.getJavaSystemPropertyString("io.netty.native.workdir");
		if (workdir != null) {
			File f = new File(workdir);
			f.mkdirs();
			try {
				f = f.getAbsoluteFile();
			} catch (Exception e) {
				// TODO: handle exception
				// Good to have an absolute path, but it's OK.
			}
			WORKDIR = f;
			logger.debug("-Dio.netty.native.workdir: " + WORKDIR);
		} else {
			WORKDIR = tmpdir();
			logger.debug("-Dio.netty.native.workdir: " + WORKDIR + " (io.netty.tmpdir)");
		}

		DELETE_NATIVE_LIB_AFTER_LOADING = SystemPropertyUtil.getJavaSystemPropertyBoolean("io.netty.native.deleteLibAfterLoading", true);
	}

	/**
	 * 构造函数
	 */
	private NativeLibraryLoader() {

	}

	/**
	 * 建立tmp文件夹(Windows和Linux都可以)
	 * 
	 * @return
	 */
	private static File tmpdir() {
		File f;
		try {
			f = toDirectory(SystemPropertyUtil.getJavaSystemPropertyString("io.netty.tmpdir"));
			if (f != null) {
				logger.debug("-Dio.netty.tmpdir: " + f);
				return f;
			}

			f = toDirectory(SystemPropertyUtil.getJavaSystemPropertyString("java.io.tmpdir"));
			if (f != null) {
				logger.debug("-Dio.netty.tmpdir: " + f + " (java.io.tmpdir)");
				return f;
			}

			// This shouldn't happen, but just in case ..
			if (isWindows()) {
				f = toDirectory(System.getenv("TEMP"));
				if (f != null) {
					logger.debug("-Dio.netty.tmpdir: " + f + " (%TEMP%)");
					return f;
				}

				String userprofile = System.getenv("USERPROFILE");
				if (userprofile != null) {
					f = toDirectory(userprofile + "\\AppData\\Local\\Temp");
					if (f != null) {
						logger.debug("-Dio.netty.tmpdir: " + f + " (%USERPROFILE%\\AppData\\Local\\Temp)");
						return f;
					}

					f = toDirectory(userprofile + "\\Local Settings\\Temp");
					if (f != null) {
						logger.debug("-Dio.netty.tmpdir: " + f + " (%USERPROFILE%\\Local Settings\\Temp)");
						return f;
					}
				}
			} else {
				f = toDirectory(System.getenv("TMPDIR"));
				if (f != null) {
					logger.debug("-Dio.netty.tmpdir: " + f + " ($TMPDIR)");
					return f;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			// Environment variable inaccessible
		}

		// Last resort
		if (isWindows()) {
			f = new File("C:\\Windows\\Temp");
		} else {
			f = new File("/tmp");
		}

		logger.warn("Failed to get the temporary directory; falling back to: " + f);
		return f;
	}

	/**
	 * 将路径转换为文件返回
	 * 
	 * @param path
	 * @return
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	private static File toDirectory(String path) {
		if (path == null) {
			return null;
		}

		File f = new File(path);
		f.mkdirs();

		if (!f.isDirectory()) {
			return null;
		}

		try {
			return f.getAbsoluteFile();
		} catch (Exception e) {
			// TODO: handle exception
			return f;
		}
	}

	/**
	 * 判断OSNAME是否以windows开始
	 * 
	 * @return
	 */
	private static boolean isWindows() {
		return OSNAME.startsWith("windows");
	}

	/**
	 * 判断OSNAME是否为Mac系统
	 * 
	 * @return
	 */
	private static boolean isOSX() {
		return OSNAME.startsWith("macosx") || OSNAME.startsWith("osx");
	}

	/**
	 * Loads the first available library in the collection with the specified
	 * {@link ClassLoader}. </br>
	 * </br>
	 * 在所有传入参数的库中load第一个可用的库
	 *
	 * @throws IllegalArgumentException
	 *             if none of the given libraries load successfully.
	 */
	public static void loadFirstAvailable(ClassLoader loader, String... names) {
		for (String name : names) {
			try {
				load(name, loader);
				logger.debug("Successfully loaded the library: {}", name);
				return;
			} catch (Throwable t) {
				// TODO: handle exception
				logger.debug("Unable to load the library '{}', trying next name...", name, t);
			}
		}
		throw new IllegalArgumentException("Failed to load any of the given libraries: " + Arrays.toString(names));
	}

	/**
	 * Load the given library with the specified {@link ClassLoader}
	 */
	public static void load(String name, ClassLoader loader) {
		String libname = System.mapLibraryName(name);
		String path = NATIVE_RESOURCE_HOME + libname;

		URL url = loader.getResource(path);
		if (url == null && isOSX()) {
			if (path.endsWith(".jnilib")) {
				url = loader.getResource(NATIVE_RESOURCE_HOME + "lib" + name + ".dynlib");
			} else {
				url = loader.getResource(NATIVE_RESOURCE_HOME + "lib" + name + ".jnilib");
			}
		}

		if (url == null) {
			// Fall back to normal loading of JNI stuff
			loadLibrary(loader, name, false);
			return;
		}

		int index = libname.lastIndexOf('.');
		String prefix = libname.substring(0, index);
		String suffix = libname.substring(index, libname.length());
		InputStream in = null;
		OutputStream out = null;
		File tmpFile = null;

		try {
			tmpFile = File.createTempFile(prefix, suffix, WORKDIR);
			in = url.openStream();
			out = new FileOutputStream(tmpFile);

			byte[] buffer = new byte[8192];
			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			out.flush();

			// Close the output stream before loading the unpacked library,
			// because otherwise Windows will refuse to load it when it's in use
			// by other process.
			closeQuietly(out);
			out = null;

			loadLibrary(loader, tmpFile.getPath(), true);
		} catch (Exception e) {
			// TODO: handle exception
			throw (UnsatisfiedLinkError) new UnsatisfiedLinkError("could not load a native library: " + name).initCause(e);
		} finally {
			closeQuietly(in);
			closeQuietly(out);
			// After we load the library it is safe to delete the file.
			// We delete the file immediately to free up resources as soon as
			// possible,
			// and if this fails fallback to deleting on JVM exit.
			if (tmpFile != null && (!DELETE_NATIVE_LIB_AFTER_LOADING || !tmpFile.delete())) {
				tmpFile.deleteOnExit();
			}
		}
	}

	/**
	 * Loading the native library into the specified {@link ClassLoader}.
	 * 
	 * @param loader
	 *            - The {@link ClassLoader} where the native library will be
	 *            loaded into
	 * @param name
	 *            - The native library path or name
	 * @param absolute
	 *            - Whether the native library will be loaded by path or by name
	 */
	private static void loadLibrary(final ClassLoader loader, final String name, final boolean absolute) {
		try {
			// Make sure the helper is belong to the target ClassLoader.
			final Class<?> newHelper = tryToLoadClass(loader, NativeLibraryUtil.class);
			loadLibraryByHelper(newHelper, name, absolute);
			return;
		} catch (UnsatisfiedLinkError e) {
			// TODO: handle exception
			logger.debug("Unable to load the library '{}', trying other loading mechanism.", name, e);
		} catch (Exception e) {
			// TODO: handle exception
			logger.debug("Unable to load the library '{}', trying other loading mechanism.", name, e);
		}
		NativeLibraryUtil.loadLibrary(name, absolute);
		// Fallback to local helper class.
	}

	/**
	 * 
	 * @param helper
	 * @param name
	 * @param absolute
	 * @throws UnsatisfiedLinkError
	 */
	private static void loadLibraryByHelper(final Class<?> helper, final String name, final boolean absolute) throws UnsatisfiedLinkError {
		Object ret = AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Object run() {
				// TODO Auto-generated method stub
				try {
					// Invoke the helper to load the native library, if succeed,
					// then the native
					// library belong to the specified ClassLoader.
					Method method = helper.getMethod("loadLibrary", new Class<?>[] { String.class, boolean.class });
					method.setAccessible(true);
					return method.invoke(null, name, absolute);
				} catch (Exception e) {
					// TODO: handle exception
					return e;
				}
			}
		});

		if (ret instanceof Throwable) {
			Throwable error = (Throwable) ret;
			Throwable cause = error.getCause();
			if (cause != null) {
				if (cause instanceof UnsatisfiedLinkError) {
					throw (UnsatisfiedLinkError) cause;
				} else {
					throw new UnsatisfiedLinkError(cause.getMessage());
				}
			}
			throw new UnsatisfiedLinkError(error.getMessage());
		}
	}

	/**
	 * 关闭c
	 * 
	 * @param c
	 */
	private static void closeQuietly(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				// TODO: handle exception
			}
		}
	}

	/**
	 * 
	 * Try to load the helper {@link Class} into specified {@link ClassLoader}.
	 * 
	 * @param loader
	 *            - The {@link ClassLoader} where to load the helper
	 *            {@link Class}
	 * @param helper
	 *            - The helper {@link Class}
	 * @return A new helper Class defined in the specified ClassLoader.
	 * @throws ClassNotFoundException
	 *             Helper class not found or loading failed
	 */
	private static Class<?> tryToLoadClass(final ClassLoader loader, final Class<?> helper) throws ClassNotFoundException {
		try {
			return loader.loadClass(helper.getName());
		} catch (ClassNotFoundException e) {
			// TODO: handle exception
			// The helper class is NOT found in target ClassLoader, we have to
			// define the helper class.
			final byte[] classBinary = classToByteArray(helper);
			return AccessController.doPrivileged(new PrivilegedAction<Class<?>>() {
				public Class<?> run() {
					// TODO Auto-generated method stub
					try {
						// Define the helper class in the target ClassLoader,
						// then we can call the helper to load the native
						// library.
						Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
						defineClass.setAccessible(true);
						return (Class<?>) defineClass.invoke(loader, helper.getName(), classBinary, 0, classBinary.length);
					} catch (Exception e2) {
						// TODO: handle exception
						throw new IllegalStateException("Define class failed", e2);
					}
				}
			});
		}
	}

	/**
	 * Load the helper {@link Class} as a byte array, to be redefined in
	 * specified {@link ClassLoader}.
	 * 
	 * @param clazz
	 *            - The helper {@link Class} provided by this bundle
	 * @return The binary content of helper {@link Class}.
	 * @throws ClassNotFoundException
	 *             Helper class not found or loading failed
	 */
	private static byte[] classToByteArray(Class<?> clazz) throws ClassNotFoundException {
		String fileName = clazz.getName();
		int lastDot = fileName.lastIndexOf('.');
		if (lastDot > 0) {
			fileName = fileName.substring(lastDot + 1);
		}

		URL classUrl = clazz.getResource(fileName + ".class");
		if (classUrl == null) {
			throw new ClassNotFoundException(clazz.getName());
		}

		byte[] buf = new byte[1024];
		ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
		InputStream in = null;

		try {
			in = classUrl.openStream();
			for (int r; (r = in.read(buf)) != -1;) {
				out.write(buf, 0, r);
			}
			return out.toByteArray();
		} catch (IOException e) {
			throw new ClassNotFoundException(clazz.getName(), e);
		} finally {
			// TODO: handle finally clause
			closeQuietly(in);
			closeQuietly(out);
		}
	}
}
