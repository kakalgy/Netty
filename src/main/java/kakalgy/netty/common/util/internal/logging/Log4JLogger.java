package kakalgy.netty.common.util.internal.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

public class Log4JLogger extends AbstractInternalLogger {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3466168702637765153L;

	/**
	 * www.cnblogs.com/lanxuezaipiao/p/3369962.html
	 * 
	 * 1.Transient的作用和使用方法
	 * 
	 * 我们都知道一个对象只要实现了Serilizable接口，这个对象就可以被序列化，java的这种序列化模式为开发者提供了很多便利，
	 * 我们可以不必关心具体序列化的过程，只要这个类实现了Serilizable接口，这个类的所有属性和方法都会自动序列化
	 * 
	 * 然而在实际开发过程中，我们会遇到这样的问题，这个类的有些属性需要序列化，有些不需要；加上transient关键字，
	 * 这个属性的生命周期就仅存于调用者的内存中而不会写到磁盘里持久化
	 * 
	 * 2.使用情况
	 * 
	 * a）当你想把内存中的对象保存到一个文件或者数据库中的时候
	 * 
	 * b）当你想用套接字在网络上传送对象的时候
	 * 
	 * c）当你想通过RMI传输对象的时候
	 * 
	 * 2.使用小结
	 * 
	 * a）一旦变量被transient修饰时，变量将不再是对象持久化的一部分，该变量内容在序列化后无法获得访问
	 * 
	 * b）transient关键字只能修饰变量，而不能修饰方法和类；注意：本地变量时不能被transient修饰的。变量如果是用户自定义类变量，
	 * 则该类需要实现Serialization接口
	 * 
	 * c）被transient修饰的变量不能再被序列化，一个静态变量不管是否被transient修饰，都不能被序列化
	 */
	final transient Logger logger;

	/**
	 * Following the pattern discussed in pages 162 through 168 of "The complete
	 * log4j manual".
	 */
	static final String FQCN = Log4JLogger.class.getName();

	// Does the log4j version in use recognize the TRACE level?
	// The trace level was introduced in log4j 1.2.12.
	final boolean traceCapable;

	Log4JLogger(Logger logger) {
		// TODO Auto-generated constructor stub
		super(logger.getName());
		this.logger = logger;
		this.traceCapable = this.isTraceCapable();
	}

	private boolean isTraceCapable() {
		try {
			this.logger.isTraceEnabled();
			return true;
		} catch (NoSuchMethodError e) {
			// TODO: handle exception
			return false;
		}
	}

	/**
	 * Is this logger instance enabled for the TRACE level?
	 *
	 * @return True if this Logger is enabled for level TRACE, false otherwise.
	 */
	public boolean isTraceEnabled() {
		// TODO Auto-generated method stub
		if (this.traceCapable) {
			return this.logger.isTraceEnabled();
		} else {
			return this.logger.isDebugEnabled();
		}
	}

	/**
	 * get Trace Log Level.
	 * 
	 * @return
	 */
	private Level getLevelIfTraceCapable() {
		if (this.traceCapable) {
			return Level.TRACE;
		} else {
			return Level.DEBUG;
		}
	}

	/**
	 * Log a message object at level TRACE.
	 *
	 * @param msg
	 *            - the message object to be logged
	 */
	public void trace(String msg) {
		// TODO Auto-generated method stub
		this.logger.log(FQCN, this.getLevelIfTraceCapable(), msg, null);
	}

	/**
	 * Log a message at level TRACE according to the specified format and
	 * argument.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for level TRACE.
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
			this.logger.log(FQCN, this.getLevelIfTraceCapable(), ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log a message at level TRACE according to the specified format and
	 * arguments.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the TRACE level.
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
			this.logger.log(FQCN, this.getLevelIfTraceCapable(), ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log a message at level TRACE according to the specified format and
	 * arguments.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the TRACE level.
	 * </p>
	 *
	 * @param format
	 *            the format string
	 * @param arguments
	 *            an array of arguments
	 */
	public void trace(String format, Object... arguments) {
		// TODO Auto-generated method stub
		if (this.isTraceEnabled()) {
			FormattingTuple ft = MessageFormatter.format(format, arguments);
			this.logger.log(FQCN, this.getLevelIfTraceCapable(), ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log an exception (throwable) at level TRACE with an accompanying message.
	 *
	 * @param msg
	 *            the message accompanying the exception
	 * @param t
	 *            the exception (throwable) to log
	 */
	public void trace(String msg, Throwable t) {
		// TODO Auto-generated method stub
		this.logger.log(FQCN, this.getLevelIfTraceCapable(), msg, t);
	}

	/**
	 * Is this logger instance enabled for the DEBUG level?
	 *
	 * @return True if this Logger is enabled for level DEBUG, false otherwise.
	 */
	public boolean isDebugEnabled() {
		// TODO Auto-generated method stub
		return this.logger.isDebugEnabled();
	}

	/**
	 * Log a message object at level DEBUG.
	 *
	 * @param msg
	 *            - the message object to be logged
	 */
	public void debug(String msg) {
		// TODO Auto-generated method stub
		this.logger.log(FQCN, Level.DEBUG, msg, null);
	}

	/**
	 * Log a message at level DEBUG according to the specified format and
	 * argument.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for level DEBUG.
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
			this.logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log a message at level DEBUG according to the specified format and
	 * arguments.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the DEBUG level.
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
			this.logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log a message at level DEBUG according to the specified format and
	 * arguments.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the DEBUG level.
	 * </p>
	 *
	 * @param format
	 *            the format string
	 * @param arguments
	 *            an array of arguments
	 */
	public void debug(String format, Object... arguments) {
		// TODO Auto-generated method stub
		if (this.isDebugEnabled()) {
			FormattingTuple ft = MessageFormatter.format(format, arguments);
			this.logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log an exception (throwable) at level DEBUG with an accompanying message.
	 *
	 * @param msg
	 *            the message accompanying the exception
	 * @param t
	 *            the exception (throwable) to log
	 */
	public void debug(String msg, Throwable t) {
		// TODO Auto-generated method stub
		this.logger.log(FQCN, Level.DEBUG, msg, t);
	}

	/**
	 * Is this logger instance enabled for the INFO level?
	 *
	 * @return True if this Logger is enabled for the INFO level, false
	 *         otherwise.
	 */
	public boolean isInfoEnabled() {
		// TODO Auto-generated method stub
		return this.logger.isInfoEnabled();
	}

	/**
	 * Log a message object at the INFO level.
	 *
	 * @param msg
	 *            - the message object to be logged
	 */
	public void info(String msg) {
		// TODO Auto-generated method stub
		this.logger.log(FQCN, Level.INFO, msg, null);
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
			this.logger.log(FQCN, Level.INFO, ft.getMessage(), ft.getThrowable());
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
			this.logger.log(FQCN, Level.INFO, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * /** Log a message at level INFO according to the specified format and
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
			this.logger.log(FQCN, Level.INFO, ft.getMessage(), ft.getThrowable());
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
		this.logger.log(FQCN, Level.INFO, msg, t);
	}

	/**
	 * Is this logger instance enabled for the WARN level?
	 *
	 * @return True if this Logger is enabled for the WARN level, false
	 *         otherwise.
	 */
	public boolean isWarnEnabled() {
		// TODO Auto-generated method stub
		return this.logger.isEnabledFor(Level.WARN);
	}

	/**
	 * Log a message object at the WARN level.
	 *
	 * @param msg
	 *            - the message object to be logged
	 */
	public void warn(String msg) {
		// TODO Auto-generated method stub
		this.logger.log(FQCN, Level.WARN, msg, null);
	}

	/**
	 * Log a message at the WARN level according to the specified format and
	 * argument.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the WARN level.
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
			logger.log(FQCN, Level.WARN, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log a message at the WARN level according to the specified format and
	 * arguments.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the WARN level.
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
			logger.log(FQCN, Level.WARN, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log a message at level WARN according to the specified format and
	 * arguments.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the WARN level.
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
			logger.log(FQCN, Level.WARN, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log an exception (throwable) at the WARN level with an accompanying
	 * message.
	 *
	 * @param msg
	 *            the message accompanying the exception
	 * @param t
	 *            the exception (throwable) to log
	 */
	public void warn(String msg, Throwable t) {
		// TODO Auto-generated method stub
		this.logger.log(FQCN, Level.WARN, msg, t);
	}

	/**
	 * Is this logger instance enabled for level ERROR?
	 *
	 * @return True if this Logger is enabled for level ERROR, false otherwise.
	 */
	public boolean isErrorEnabled() {
		// TODO Auto-generated method stub
		return this.logger.isEnabledFor(Level.ERROR);
	}

	/**
	 * Log a message object at the ERROR level.
	 *
	 * @param msg
	 *            - the message object to be logged
	 */
	public void error(String msg) {
		// TODO Auto-generated method stub
		this.logger.log(FQCN, Level.ERROR, msg, null);
	}

	/**
	 * Log a message at the ERROR level according to the specified format and
	 * argument.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the ERROR level.
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
			logger.log(FQCN, Level.ERROR, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log a message at the ERROR level according to the specified format and
	 * arguments.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the ERROR level.
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
			logger.log(FQCN, Level.ERROR, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log a message at level ERROR according to the specified format and
	 * arguments.
	 *
	 * <p>
	 * This form avoids superfluous object creation when the logger is disabled
	 * for the ERROR level.
	 * </p>
	 *
	 * @param format
	 *            the format string
	 * @param argArray
	 *            an array of arguments
	 */
	public void error(String format, Object... arguments) {
		// TODO Auto-generated method stub
		if (this.isErrorEnabled()) {
			FormattingTuple ft = MessageFormatter.format(format, arguments);
			logger.log(FQCN, Level.ERROR, ft.getMessage(), ft.getThrowable());
		}
	}

	/**
	 * Log an exception (throwable) at the ERROR level with an accompanying
	 * message.
	 *
	 * @param msg
	 *            the message accompanying the exception
	 * @param t
	 *            the exception (throwable) to log
	 */
	public void error(String msg, Throwable t) {
		// TODO Auto-generated method stub
		this.logger.log(FQCN, Level.ERROR, msg, t);
	}
}
