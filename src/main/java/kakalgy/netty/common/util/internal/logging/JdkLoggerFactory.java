package kakalgy.netty.common.util.internal.logging;

import java.util.logging.Logger;

/**
 * Logger factory which creates a
 * <a href="http://docs.oracle.com/javase/7/docs/technotes/guides/logging/">java
 * .util.logging</a> logger.
 * 
 * @author Administrator
 *
 */
public class JdkLoggerFactory extends InternalLoggerFactory {

	public static final InternalLoggerFactory INSTANCE = new JdkLoggerFactory();

	@Deprecated
	public JdkLoggerFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected InternalLogger newInstance(String name) {
		// TODO Auto-generated method stub
		return new JdkLogger(Logger.getLogger(name));
	}
}
