package kakalgy.netty.common.util.internal;

/**
 * A Utility to Call the {@link System#load(String)} or
 * {@link System#loadLibrary(String)}. Because the {@link System#load(String)}
 * and {@link System#loadLibrary(String)} are both CallerSensitive, it will load
 * the native library into its caller's {@link ClassLoader}. In OSGi
 * environment, we need this helper to delegate(委派，授权) the calling to
 * {@link System#load(String)} and it should be as simple as possible. It will
 * be injected into the native library's ClassLoader when it is undefined. And
 * therefore, when the defined new helper is invoked, the native library would
 * be loaded into the native library's ClassLoader, not the caller's
 * ClassLoader.
 */
public final class NativeLibraryUtil {

	private NativeLibraryUtil() {

	}

	/**
	 * Delegate the calling to {@link System#load(String)} or
	 * {@link System#loadLibrary(String)}.</br>
	 * </br>
	 * 若absolute为TRUE，则libName为路径；若absolute为FALSE，则libName为包名
	 * 
	 * @param libName
	 *            - The native library path or name
	 * @param absolute
	 *            - Whether the native library will be loaded by path or by name
	 */
	public static void loadLibrary(String libName, boolean absolute) {
		if (absolute) {
			System.load(libName);
		} else {
			System.loadLibrary(libName);
		}
	}
}
