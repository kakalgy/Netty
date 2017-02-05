package kakalgy.netty.common.util.internal.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

/**
 * <a href=
 * "http://java.sun.com/javase/6/docs/technotes/guides/logging/index.html">java.
 * util.logging</a> logger.
 */
public class JdkLogger extends AbstractInternalLogger {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3858919379814954561L;

	final transient Logger logger;

	JdkLogger(Logger logger) {
		// TODO Auto-generated constructor stub
		super(logger.getName());
		this.logger = logger;
	}

	/**
	 * Log the message at the specified level with the specified throwable if
	 * any. This method creates a LogRecord and fills in caller date before
	 * calling this instance's JDK14 logger.
	 *
	 * See bug report #13 for more details.
	 */
	private void log(String callerFQCN, Level level, String msg, Throwable t) {
		// millis and thread are filled by the constructor
		LogRecord record = new LogRecord(level, msg);
		 record.setLoggerName(this.getLoggerInstanceName());
//		record.setLoggerName(this.name());
		record.setThrown(t);
		fillCallerData(callerFQCN, record);
		logger.log(record);
	}

	static final String SELF = JdkLogger.class.getName();
	static final String SUPER = AbstractInternalLogger.class.getName();

	/**
	 * Fill in caller data if possible.
	 *
	 * @param record
	 *            The record to update
	 */
	private static void fillCallerData(String callerFQCN, LogRecord record) {
		StackTraceElement[] steArray = new Throwable().getStackTrace();

		int selfIndex = -1;
		for (int i = 0; i < steArray.length; i++) {
			final String className = steArray[i].getClassName();
			if (className.equals(callerFQCN) || className.equals(SUPER)) {
				selfIndex = i;
				break;
			}
		}

		int found = -1;
		for (int i = selfIndex + 1; i < steArray.length; i--) {
			final String className = steArray[i].getClassName();
			if (!(className.equals(callerFQCN)) || className.endsWith(SUPER)) {
				found = i;
				break;
			}
		}

		if (found != -1) {
			StackTraceElement ste = steArray[found];
			/**
			 * setting the class name has the side effect of setting the
			 * needToInferCaller variable to false.
			 */
			record.setSourceClassName(ste.getClassName());
			record.setSourceMethodName(ste.getMethodName());
		}
	}

	/**
	 * Is this logger instance enabled for the FINEST level?
	 *
	 * @return True if this Logger is enabled for level FINEST, false otherwise.
	 */
	public boolean isTraceEnabled() {
		// TODO Auto-generated method stub
		return this.logger.isLoggable(Level.FINEST);
	}

	/**
	 * Log a message object at level FINEST.
	 *
	 * @param msg
	 *            - the message object to be logged
	 */
	public void trace(String msg) {
		// TODO Auto-generated method stub
		if (this.isTraceEnabled()) {
			this.log(SELF, Level.FINEST, msg, null);
		}
	}

	/**
	 * Log a message at level FINEST according to the specified format and
	 * argument.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for level FINEST.
	 * </p>
	 *
	 * @param format
	 *            the format string
	 * @param arg
	 *            the argument
	 */
	public void trace(String format, Object arg) {
		// TODO Auto-generated method stub
		if (this.isTraceEnabled()) {
			FormattingTuple ft = MessageFormatter.format(format, arg);
			this.log(SELF, Level.FINEST, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log a message at level FINEST according to the specified format and
	 * arguments.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the FINEST level.
	 * </p>
	 *
	 * @param format
	 *            the format string
	 * @param argA
	 *            the first argument
	 * @param argB
	 *            the second argument
	 */
	public void trace(String format, Object argA, Object argB) {
		// TODO Auto-generated method stub
		if (this.isTraceEnabled()) {
			FormattingTuple ft = MessageFormatter.format(format, argA, argB);
			this.log(SELF, Level.FINEST, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log a message at level FINEST according to the specified format and
	 * arguments.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the FINEST level.
	 * </p>
	 *
	 * @param format
	 *            the format string
	 * @param argArray
	 *            an array of arguments
	 */
	public void trace(String format, Object... arguments) {
		// TODO Auto-generated method stub
		if (this.isTraceEnabled()) {
			FormattingTuple ft = MessageFormatter.format(format, arguments);
			this.log(SELF, Level.FINEST, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log an exception (throwable) at level FINEST with an accompanying
	 * message.
	 *
	 * @param msg
	 *            the message accompanying the exception
	 * @param t
	 *            the exception (throwable) to log
	 */
	public void trace(String msg, Throwable t) {
		// TODO Auto-generated method stub
		if (this.isTraceEnabled()) {
			this.log(SELF, Level.FINEST, msg, t);
		}
	}

	/**
	 * Is this logger instance enabled for the FINE level?
	 *
	 * @return True if this Logger is enabled for level FINE, false otherwise.
	 */
	public boolean isDebugEnabled() {
		// TODO Auto-generated method stub
		return this.logger.isLoggable(Level.FINE);
	}

	/**
	 * Log a message object at level FINE.
	 *
	 * @param msg
	 *            - the message object to be logged
	 */
	public void debug(String msg) {
		// TODO Auto-generated method stub
		if (this.isDebugEnabled()) {
			this.log(SELF, Level.FINE, msg, null);
		}
	}

	/**
	 * Log a message at level FINE according to the specified format and
	 * argument.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for level FINE.
	 * </p>
	 *
	 * @param format
	 *            the format string
	 * @param arg
	 *            the argument
	 */
	public void debug(String format, Object arg) {
		// TODO Auto-generated method stub
		if (this.isDebugEnabled()) {
			FormattingTuple ft = MessageFormatter.format(format, arg);
			this.log(SELF, Level.FINE, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log a message at level FINE according to the specified format and
	 * arguments.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the FINE level.
	 * </p>
	 *
	 * @param format
	 *            the format string
	 * @param argA
	 *            the first argument
	 * @param argB
	 *            the second argument
	 */
	public void debug(String format, Object argA, Object argB) {
		// TODO Auto-generated method stub
		if (this.isDebugEnabled()) {
			FormattingTuple ft = MessageFormatter.format(format, argA, argB);
			this.log(SELF, Level.FINE, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log a message at level FINE according to the specified format and
	 * arguments.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the FINE level.
	 * </p>
	 *
	 * @param format
	 *            the format string
	 * @param argArray
	 *            an array of arguments
	 */
	public void debug(String format, Object... arguments) {
		// TODO Auto-generated method stub
		if (this.isDebugEnabled()) {
			FormattingTuple ft = MessageFormatter.format(format, arguments);
			this.log(SELF, Level.FINE, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log an exception (throwable) at level FINE with an accompanying message.
	 *
	 * @param msg
	 *            the message accompanying the exception
	 * @param t
	 *            the exception (throwable) to log
	 */
	public void debug(String msg, Throwable t) {
		// TODO Auto-generated method stub
		if (this.isDebugEnabled()) {
			this.log(SELF, Level.FINE, msg, t);
		}
	}

	/**
	 * Is this logger instance enabled for the INFO level?
	 *
	 * @return True if this Logger is enabled for the INFO level, false
	 *         otherwise.
	 */
	public boolean isInfoEnabled() {
		// TODO Auto-generated method stub
		return this.logger.isLoggable(Level.INFO);
	}

	/**
	 * Log a message object at the INFO level.
	 *
	 * @param msg
	 *            - the message object to be logged
	 */
	public void info(String msg) {
		// TODO Auto-generated method stub
		if (this.isInfoEnabled()) {
			this.log(SELF, Level.INFO, msg, null);
		}
	}

	/**
	 * Log a message at level INFO according to the specified format and
	 * argument.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the INFO level.
	 * </p>
	 *
	 * @param format
	 *            the format string
	 * @param arg
	 *            the argument
	 */
	public void info(String format, Object arg) {
		// TODO Auto-generated method stub
		if (this.isInfoEnabled()) {
			FormattingTuple ft = MessageFormatter.format(format, arg);
			this.log(SELF, Level.INFO, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log a message at the INFO level according to the specified format and
	 * arguments.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the INFO level.
	 * </p>
	 *
	 * @param format
	 *            the format string
	 * @param argA
	 *            the first argument
	 * @param argB
	 *            the second argument
	 */
	public void info(String format, Object argA, Object argB) {
		// TODO Auto-generated method stub
		if (this.isInfoEnabled()) {
			FormattingTuple ft = MessageFormatter.format(format, argA, argB);
			this.log(SELF, Level.INFO, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log a message at level INFO according to the specified format and
	 * arguments.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the INFO level.
	 * </p>
	 *
	 * @param format
	 *            the format string
	 * @param argArray
	 *            an array of arguments
	 */
	public void info(String format, Object... arguments) {
		// TODO Auto-generated method stub
		if (this.isInfoEnabled()) {
			FormattingTuple ft = MessageFormatter.format(format, arguments);
			this.log(SELF, Level.INFO, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log an exception (throwable) at the INFO level with an accompanying
	 * message.
	 *
	 * @param msg
	 *            the message accompanying the exception
	 * @param t
	 *            the exception (throwable) to log
	 */
	public void info(String msg, Throwable t) {
		// TODO Auto-generated method stub
		if (this.isInfoEnabled()) {
			this.log(SELF, Level.INFO, msg, t);
		}
	}

	/**
	 * Is this logger instance enabled for the WARNING level?
	 *
	 * @return True if this Logger is enabled for the WARNING level, false
	 *         otherwise.
	 */
	public boolean isWarnEnabled() {
		// TODO Auto-generated method stub
		return this.logger.isLoggable(Level.WARNING);
	}

	/**
	 * Log a message object at the WARNING level.
	 *
	 * @param msg
	 *            - the message object to be logged
	 */
	public void warn(String msg) {
		// TODO Auto-generated method stub
		if (this.isWarnEnabled()) {
			this.log(SELF, Level.WARNING, msg, null);
		}
	}

	/**
	 * Log a message at the WARNING level according to the specified format and
	 * argument.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the WARNING level.
	 * </p>
	 *
	 * @param format
	 *            the format string
	 * @param arg
	 *            the argument
	 */
	public void warn(String format, Object arg) {
		// TODO Auto-generated method stub
		if (this.isWarnEnabled()) {
			FormattingTuple ft = MessageFormatter.format(format, arg);
			this.log(SELF, Level.WARNING, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log a message at the WARNING level according to the specified format and
	 * arguments.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the WARNING level.
	 * </p>
	 *
	 * @param format
	 *            the format string
	 * @param argA
	 *            the first argument
	 * @param argB
	 *            the second argument
	 */
	public void warn(String format, Object argA, Object argB) {
		// TODO Auto-generated method stub
		if (this.isWarnEnabled()) {
			FormattingTuple ft = MessageFormatter.format(format, argA, argB);
			this.log(SELF, Level.WARNING, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log a message at level WARNING according to the specified format and
	 * arguments.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the WARNING level.
	 * </p>
	 *
	 * @param format
	 *            the format string
	 * @param argArray
	 *            an array of arguments
	 */
	public void warn(String format, Object... arguments) {
		// TODO Auto-generated method stub
		if (this.isWarnEnabled()) {
			FormattingTuple ft = MessageFormatter.format(format, arguments);
			this.log(SELF, Level.WARNING, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log an exception (throwable) at the WARNING level with an accompanying
	 * message.
	 *
	 * @param msg
	 *            the message accompanying the exception
	 * @param t
	 *            the exception (throwable) to log
	 */
	public void warn(String msg, Throwable t) {
		// TODO Auto-generated method stub
		if (this.isWarnEnabled()) {
			this.log(SELF, Level.WARNING, msg, t);
		}
	}

	/**
	 * Is this logger instance enabled for level SEVERE?
	 *
	 * @return True if this Logger is enabled for level SEVERE, false otherwise.
	 */
	public boolean isErrorEnabled() {
		// TODO Auto-generated method stub
		return this.logger.isLoggable(Level.SEVERE);
	}

	/**
	 * Log a message object at the SEVERE level.
	 *
	 * @param msg
	 *            - the message object to be logged
	 */
	public void error(String msg) {
		// TODO Auto-generated method stub
		if (this.isErrorEnabled()) {
			this.log(SELF, Level.SEVERE, msg, null);
		}
	}

	/**
	 * Log a message at the SEVERE level according to the specified format and
	 * argument.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the SEVERE level.
	 * </p>
	 *
	 * @param format
	 *            the format string
	 * @param arg
	 *            the argument
	 */
	public void error(String format, Object arg) {
		// TODO Auto-generated method stub
		if (this.isErrorEnabled()) {
			FormattingTuple ft = MessageFormatter.format(format, arg);
			this.log(SELF, Level.SEVERE, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log a message at the SEVERE level according to the specified format and
	 * arguments.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the SEVERE level.
	 * </p>
	 *
	 * @param format
	 *            the format string
	 * @param argA
	 *            the first argument
	 * @param argB
	 *            the second argument
	 */
	public void error(String format, Object argA, Object argB) {
		// TODO Auto-generated method stub
		if (this.isErrorEnabled()) {
			FormattingTuple ft = MessageFormatter.format(format, argA, argB);
			this.log(SELF, Level.SEVERE, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log a message at level SEVERE according to the specified format and
	 * arguments.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the SEVERE level.
	 * </p>
	 *
	 * @param format
	 *            the format string
	 * @param arguments
	 *            an array of arguments
	 */
	public void error(String format, Object... arguments) {
		// TODO Auto-generated method stub
		if (this.isErrorEnabled()) {
			FormattingTuple ft = MessageFormatter.format(format, arguments);
			this.log(SELF, Level.SEVERE, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log an exception (throwable) at the SEVERE level with an accompanying
	 * message.
	 *
	 * @param msg
	 *            the message accompanying the exception
	 * @param t
	 *            the exception (throwable) to log
	 */
	public void error(String msg, Throwable t) {
		// TODO Auto-generated method stub
		if (this.isErrorEnabled()) {
			this.log(SELF, Level.SEVERE, msg, t);
		}
	}
}
