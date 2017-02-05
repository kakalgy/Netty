package kakalgy.netty.common.util.internal;

/**
 * A grab-bag of useful utility methods.
 */
public final class ObjectUtil {

	private ObjectUtil() {

	}

	/**
	 * Checks that the given argument is not null. If it is, throws
	 * {@link NullPointerException}. Otherwise, returns the argument.
	 */
	public static <T> T checkNotNull(T arg, String text) {
		if (arg == null) {
			throw new NullPointerException(text);
		}
		return arg;
	}
}
