package kakalgy.netty.common.util.internal.logging;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLoggerFactory;

/**
 * Logger factory which creates a <a href="http://www.slf4j.org/">SLF4J</a>
 * logger.
 * 
 * @author Administrator
 *
 */
public class Slf4JLoggerFactory extends InternalLoggerFactory {

	public static final InternalLoggerFactory INSTANCE = new Slf4JLoggerFactory();

	/**
	 * @deprecated Use {@link #INSTANCE} instead.
	 */
	@Deprecated
	public Slf4JLoggerFactory() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 构造函数
	 * 
	 * @param faliIfNOP
	 */
	Slf4JLoggerFactory(boolean faliIfNOP) {
		assert faliIfNOP;// Should be always called with true.

		/*
		 * SFL4J writes it error messages to System.err. Capture them so that
		 * the user does not see such a message on the console during automatic
		 * detection.
		 */
		final StringBuffer buf = new StringBuffer();
		final PrintStream err = System.err;
		try {
			System.setErr(new PrintStream(new OutputStream() {

				@Override
				public void write(int b){
					// TODO Auto-generated method stub
					buf.append((char) b);
				}
			}, true, "US-ASCII"));
		} catch (UnsupportedEncodingException e) {
			// TODO: handle exception
			throw new Error(e);
		}

		try {
			// 若返回NOPLoggerFactory，它返回一个单例的NOPLogger实例，该类不会打印任何日志
			if (LoggerFactory.getILoggerFactory() instanceof NOPLoggerFactory) {
				throw new NoClassDefFoundError(buf.toString());
			} else {
				err.print(buf);
				err.flush();
			}
		} finally {
			System.setErr(err);
		}
	}

	@Override
	protected InternalLogger newInstance(String name) {
		// TODO Auto-generated method stub
		return new Slf4JLogger(LoggerFactory.getLogger(name));
	}
}
