package kakalgy.netty.common.util.internal;

import java.security.AccessController;
import java.security.PrivilegedAction;

import kakalgy.netty.common.util.internal.logging.InternalLogger;
import kakalgy.netty.common.util.internal.logging.InternalLoggerFactory;

/**
 * A collection of utility methods to retrieve(恢复) and parse(分析) the values of
 * the Java system properties.
 */
public final class SystemPropertyUtil {

	private static final InternalLogger logger = InternalLoggerFactory.getInstance(SystemPropertyUtil.class);

	/**
	 * Returns {@code true} if and only if the system property with the
	 * specified {@code key} exists.
	 * 
	 * @param key
	 * @return
	 */
	public static boolean containsJavaSystemProperty(String key) {
		String value = getJavaSystemPropertyString(key);
		boolean ifContains = (value != null);
		return ifContains;
	}

	/**
	 * Returns the value of the Java system property with the specified
	 * {@code key}, while falling back to the specified default value if the
	 * property access fails.
	 *
	 * @return the property value. {@code def} if there's no such property or if
	 *         an access to the specified property is not allowed.
	 */
	public static String getJavaSystemPropertyString(final String key, String def) {
		if (key == null) {
			throw new NullPointerException("key");
		}
		if (key.isEmpty()) {
			throw new IllegalArgumentException("key must not be empty");
		}

		String value = null;
		try {
			if (System.getSecurityManager() == null) {
				value = System.getProperty(key);
			} else {
				value = AccessController.doPrivileged(new PrivilegedAction<String>() {
					public String run() {
						// TODO Auto-generated method stub
						return System.getProperty(key);
					}
				});
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn("Unable to retrieve a system property '{}'; default values will be used.", key, e);
		}

		if (value == null) {// 若java得不到对应key的值，则返回默认值
			return def;
		}

		return value;
	}

	/**
	 * Returns the value of the Java system property with the specified
	 * {@code key}, while falling back to {@code null} if the property access
	 * fails.
	 *
	 * @return the property value or {@code null}
	 */
	public static String getJavaSystemPropertyString(String key) {
		return getJavaSystemPropertyString(key, null);
	}

	/**
	 * Returns the value of the Java system property with the specified
	 * {@code key}, while falling back to the specified default value if the
	 * property access fails.
	 *
	 * @return the property value. {@code def} if there's no such property or if
	 *         an access to the specified property is not allowed.
	 */
	public static boolean getJavaSystemPropertyBoolean(String key, boolean def) {
		String value = getJavaSystemPropertyString(key);

		if (value == null) {
			return def;
		}

		value = value.trim().toLowerCase();

		if (value.isEmpty()) {
			return true;
		}

		if ("true".equals(value) || "yes".equals(value) || "1".equals(value)) {
			return true;
		}

		if ("false".equals(value) || "no".equals(value) || "0".equals(value)) {
			return false;
		}

		logger.warn("Unable to parse the boolean system property '{}':{} - using the default value: {}", key, value, def);
		return def;
	}
}
