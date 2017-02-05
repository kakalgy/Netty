package kakalgy.netty.common.util.internal.logging;

import org.apache.log4j.Logger;

/**
 * Logger factory which creates an
 * <a href="http://logging.apache.org/log4j/1.2/index.html">Apache Log4J</a>
 * logger.
 */
public class Log4JLoggerFactory extends InternalLoggerFactory {
	
	public static final InternalLoggerFactory INSTANCE = new Log4JLoggerFactory();

	/**
     * @deprecated Use {@link #INSTANCE} instead.
     */
    @Deprecated
    public Log4JLoggerFactory() {
    }

	@Override
	protected InternalLogger newInstance(String name) {
		// TODO Auto-generated method stub
		return new Log4JLogger(Logger.getLogger(name));
	}
}
