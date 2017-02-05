package kakalgy.netty.common.util.internal.logging;

import java.io.ObjectStreamException;
import java.io.Serializable;

import kakalgy.netty.common.util.internal.StringUtil;

public abstract class AbstractInternalLogger implements InternalLogger, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1132899417831655758L;

	private static final String EXCEPTION_MESSAGE = "Unexpected exception:";

	/**
	 * the name of the InternalLogger Instance.
	 */
	private final String name;

	/**
	 * Creates a new instance.
	 */
	public AbstractInternalLogger(String name) {
		// TODO Auto-generated constructor stub
		if (name == null) {
			throw new NullPointerException("name");
		}
		this.name = name;
	}

	/**
	*
	*/
	public String getLoggerInstanceName() {
		// TODO Auto-generated method stub
		return this.name;
	}

//	public String name() {
//		// TODO Auto-generated method stub
//		return this.name;
//	}

	public boolean isEnabled(InternalLogLevel level) {
		// TODO Auto-generated method stub
		switch (level) {
		case TRACE:
			return isTraceEnabled();
		case DEBUG:
			return isDebugEnabled();
		case INFO:
			return isInfoEnabled();
		case WARN:
			return isWarnEnabled();
		case ERROR:
			return isErrorEnabled();
		default:
			throw new Error();
		}
	}

	public void trace(Throwable t) {
		trace(EXCEPTION_MESSAGE, t);
	}

	public void debug(Throwable t) {
		debug(EXCEPTION_MESSAGE, t);
	}

	public void info(Throwable t) {
		info(EXCEPTION_MESSAGE, t);
	}

	public void warn(Throwable t) {
		warn(EXCEPTION_MESSAGE, t);
	}

	public void error(Throwable t) {
		error(EXCEPTION_MESSAGE, t);
	}

	public void log(InternalLogLevel level, String msg, Throwable cause) {
		switch (level) {
		case TRACE:
			trace(msg, cause);
			break;
		case DEBUG:
			debug(msg, cause);
			break;
		case INFO:
			info(msg, cause);
			break;
		case WARN:
			warn(msg, cause);
			break;
		case ERROR:
			error(msg, cause);
			break;
		default:
			throw new Error();
		}
	}

	public void log(InternalLogLevel level, Throwable cause) {
		switch (level) {
		case TRACE:
			trace(cause);
			break;
		case DEBUG:
			debug(cause);
			break;
		case INFO:
			info(cause);
			break;
		case WARN:
			warn(cause);
			break;
		case ERROR:
			error(cause);
			break;
		default:
			throw new Error();
		}
	}

	public void log(InternalLogLevel level, String msg) {
		switch (level) {
		case TRACE:
			trace(msg);
			break;
		case DEBUG:
			debug(msg);
			break;
		case INFO:
			info(msg);
			break;
		case WARN:
			warn(msg);
			break;
		case ERROR:
			error(msg);
			break;
		default:
			throw new Error();
		}
	}

	public void log(InternalLogLevel level, String format, Object arg) {
		switch (level) {
		case TRACE:
			trace(format, arg);
			break;
		case DEBUG:
			debug(format, arg);
			break;
		case INFO:
			info(format, arg);
			break;
		case WARN:
			warn(format, arg);
			break;
		case ERROR:
			error(format, arg);
			break;
		default:
			throw new Error();
		}
	}

	public void log(InternalLogLevel level, String format, Object argA, Object argB) {
		switch (level) {
		case TRACE:
			trace(format, argA, argB);
			break;
		case DEBUG:
			debug(format, argA, argB);
			break;
		case INFO:
			info(format, argA, argB);
			break;
		case WARN:
			warn(format, argA, argB);
			break;
		case ERROR:
			error(format, argA, argB);
			break;
		default:
			throw new Error();
		}
	}

	public void log(InternalLogLevel level, String format, Object... arguments) {
		switch (level) {
		case TRACE:
			trace(format, arguments);
			break;
		case DEBUG:
			debug(format, arguments);
			break;
		case INFO:
			info(format, arguments);
			break;
		case WARN:
			warn(format, arguments);
			break;
		case ERROR:
			error(format, arguments);
			break;
		default:
			throw new Error();
		}
	}

	/**
	 * 
	 * @return
	 * @throws ObjectStreamException
	 */
	protected Object readResolve() throws ObjectStreamException {
		return InternalLoggerFactory.getInstance(getLoggerInstanceName());
		// return InternalLoggerFactory.getInstance(name());
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		// return super.toString();
		return StringUtil.simpleClassName(this) + '(' + this.getLoggerInstanceName() + ')';
		// return StringUtil.simpleClassName(this) + '(' + this.name() + ')';
	}
}
