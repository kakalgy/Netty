package kakalgy.netty.common.util.internal;

public final class StringUtil {

	private static final char PACKAGE_SEPARATOR_CHAR = '.';

	/**
	 * Generates a simplified name from a {@link Class}. Similar to
	 * {@link Class#getSimpleName()}, but it works fine with anonymous
	 * classes.</br>
	 * </br>
	 * 得到类名的最后一段, 例如：输入kakalgy.netty.common.util.internal.StringUtil
	 * 得到StringUtil
	 */
	public static String simpleClassName(Class<?> clazz) {
		String className = ObjectUtil.checkNotNull(clazz, "clazz").getName();
		final int lastDotIdx = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);
		if (lastDotIdx > -1) {
			return className.substring(lastDotIdx + 1);
		}
		return className;
	}

	/**
	 * The shortcut to {@link #simpleClassName(Class)
	 * simpleClassName(o.getClass())}.
	 */
	public static String simpleClassName(Object o) {
		if (o == null) {
			return "null_object";
		} else {
			return simpleClassName(o.getClass());
		}
	}
}
