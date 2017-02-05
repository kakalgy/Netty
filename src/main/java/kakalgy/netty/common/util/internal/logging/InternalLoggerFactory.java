package kakalgy.netty.common.util.internal.logging;

public abstract class InternalLoggerFactory {

	private static volatile InternalLoggerFactory defaultFactory;

	@SuppressWarnings("UnusedCatchParameter")
	private static InternalLoggerFactory newDefaultFacotory(String name) {
		InternalLoggerFactory f;
		try {
			f = new Slf4JLoggerFactory(true);
			f.newInstance(name).debug("Using SLF4J as the default logging framework");
		} catch (Throwable t1) {
			try {
				f = Log4JLoggerFactory.INSTANCE;
				f.newInstance(name).debug("Using Log4J as the default logging framework");
			} catch (Throwable t2) {
				// TODO: handle exception
				f = JdkLoggerFactory.INSTANCE;
				f.newInstance(name).debug("Using java.util.logging as the default logging framework");
			}
		}
		return f;
	}

	/**
	 * Returns the default factory. The initial default factory is
	 * {@link JdkLoggerFactory}.
	 * 
	 * @return
	 */
	public static InternalLoggerFactory getDefaultFactory() {
		if (defaultFactory == null) {
			defaultFactory = newDefaultFacotory(InternalLoggerFactory.class.getName());
		}
		return defaultFactory;
	}

	/**
	 * Changes the default factory.
	 * 
	 * @param defaultFactory
	 */
	public static void setDefaultFactory(InternalLoggerFactory defaultFactory) {
		if (defaultFactory == null) {
			throw new NullPointerException("defaultFactory");
		}
		InternalLoggerFactory.defaultFactory = defaultFactory;
	}

	/**
	 * Creates a new logger instance with the name of the specified class.
	 */
	public static InternalLogger getInstance(Class<?> clazz) {
		return getInstance(clazz.getName());
	}

	/**
	 * Creates a new logger instance with the specified name.
	 */
	public static InternalLogger getInstance(String name) {
		return getDefaultFactory().newInstance(name);
	}

	/**
	 * Creates a new logger instance with the specified name.
	 */
	protected abstract InternalLogger newInstance(String name);
}
