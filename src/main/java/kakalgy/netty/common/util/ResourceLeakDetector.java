package kakalgy.netty.common.util;

import java.util.EnumSet;

import kakalgy.netty.common.util.internal.SystemPropertyUtil;
import kakalgy.netty.common.util.internal.logging.InternalLogger;
import kakalgy.netty.common.util.internal.logging.InternalLoggerFactory;

/**
 * 资源泄漏检测类
 * 
 * @author Administrator
 *
 * @param <T>
 */
public class ResourceLeakDetector<T> {

	private static final InternalLogger logger = InternalLoggerFactory.getInstance(ResourceLeakDetector.class);

	private static final String PROP_LEVEL_OLD = "io.netty.leakDetectionLevel";
	private static final String PROP_LEVEL = "io.netty.leakDetection.level";
	private static final Level DEFAULT_LEVEL = Level.SIMPLE;

	private static final String PROP_MAX_RECORDS = "io.netty.leakDetection.maxRecords";
	private static final int DEFAULT_MAX_RECORDS = 4;
	private static final int MAX_RECORDS;

	private static Level level;

	/**
	 * Represents the level of resource leak detection.(资源泄漏检测的级别)
	 */
	public enum Level {
		/**
		 * Disables resource leak detection.
		 */
		DISABLED,
		/**
		 * (默认级别) </br>
		 * Enables simplistic sampling resource leak detection which reports
		 * there is a leak or not, at the cost of small overhead (default).
		 */
		SIMPLE,
		/**
		 * Enables advanced sampling resource leak detection which reports where
		 * the leaked object was accessed recently at the cost of high overhead.
		 */
		ADVANCED,
		/**
		 * Enables paranoid resource leak detection which reports where the
		 * leaked object was accessed recently, at the cost of the highest
		 * possible overhead (for testing purposes only).
		 */
		PARANOID
	}

	// Should be power of two
	static final int DEFAULT_SAMPLING_INTERVAL = 128;

	static {
		final boolean disabled;
		if (SystemPropertyUtil.getJavaSystemPropertyString("io.netty.noResourceLeakDetection") != null) {
			disabled = SystemPropertyUtil.getJavaSystemPropertyBoolean("io.netty.noResourceLeakDetection", false);
			logger.debug("-Dio.netty.noResourceLeakDetection: {}", disabled);
			logger.warn("-Dio.netty.noResourceLeakDetection is deprecated. Use '-D{}={}' instead.", PROP_LEVEL, DEFAULT_LEVEL.name().toLowerCase());
		} else {
			disabled = false;
		}

		Level defaultLevel = disabled ? Level.DISABLED : DEFAULT_LEVEL;

		// First read old property name
		String levelStr = SystemPropertyUtil.getJavaSystemPropertyString(PROP_LEVEL_OLD, defaultLevel.name()).trim().toUpperCase();

		// If new property name is present, use it
		levelStr = SystemPropertyUtil.getJavaSystemPropertyString(PROP_LEVEL, levelStr).trim().toUpperCase();
		Level levelLocalVariables = DEFAULT_LEVEL;
		for (Level l : EnumSet.allOf(Level.class)) {
			if (levelStr.equals(l.name()) || levelStr.equals(String.valueOf(l.ordinal()))) {
				// ordinal() 返回此枚举常量的序数（其枚举声明中的位置，其中初始常量分配的序数为零）
				levelLocalVariables = l;
			}
		}

		MAX_RECORDS = SystemPropertyUtil.getJavaSystemPropertyInt(PROP_MAX_RECORDS, DEFAULT_MAX_RECORDS);
		ResourceLeakDetector.level = levelLocalVariables;

		if (logger.isDebugEnabled()) {
			logger.debug("-D{}: {}", PROP_LEVEL, levelLocalVariables.name().toLowerCase());
			logger.debug("-D{}: {}", PROP_MAX_RECORDS, MAX_RECORDS);
		}
	}

	/**
	 * Returns the current resource leak detection level.
	 * 
	 * @param level
	 * @return
	 */
	public static Level getLevel() {
		return level;
	}

	/**
	 * Sets the resource leak detection level.
	 * 
	 * @param level
	 */
	public static void setLevel(Level level) {
		if (level == null) {
			throw new NullPointerException("level");
		}
		ResourceLeakDetector.level = level;
	}

	/**
	 * Returns {@code true} if resource leak detection is enabled.
	 */
	public static boolean isEnabled() {
		return getLevel().ordinal() > Level.DISABLED.ordinal();
	}

	/**
	 * @deprecated Use {@link #setLevel(Level)} instead.
	 */
	@Deprecated
	public static void setEnabled(boolean enabled) {
		setLevel(enabled ? Level.SIMPLE : Level.DISABLED);
	}
}
