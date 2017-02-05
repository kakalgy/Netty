package kakalgy.netty.common.util.internal.logging;

import org.slf4j.Logger;

public class Slf4JLogger extends AbstractInternalLogger {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5813558676825236307L;

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
	private final transient Logger logger;

	public Slf4JLogger(Logger logger) {
		// TODO Auto-generated constructor stub
		super(logger.getName());
		this.logger = logger;
	}

	public boolean isTraceEnabled() {
		// TODO Auto-generated method stub
		return this.logger.isTraceEnabled();
	}

	public void trace(String msg) {
		// TODO Auto-generated method stub
		this.logger.trace(msg);
	}

	public void trace(String format, Object arg) {
		// TODO Auto-generated method stub
		this.logger.trace(format, arg);
	}

	public void trace(String format, Object argA, Object argB) {
		// TODO Auto-generated method stub
		this.logger.trace(format, argA, argB);
	}

	public void trace(String format, Object... arguments) {
		// TODO Auto-generated method stub
		this.logger.trace(format, arguments);
	}

	public void trace(String msg, Throwable t) {
		// TODO Auto-generated method stub
		this.logger.trace(msg, t);
	}

	public boolean isDebugEnabled() {
		// TODO Auto-generated method stub
		return this.logger.isDebugEnabled();
	}

	public void debug(String msg) {
		// TODO Auto-generated method stub
		this.logger.debug(msg);
	}

	public void debug(String format, Object arg) {
		// TODO Auto-generated method stub
		this.logger.debug(format, arg);
	}

	public void debug(String format, Object argA, Object argB) {
		// TODO Auto-generated method stub
		this.logger.debug(format, argA, argB);
	}

	public void debug(String format, Object... arguments) {
		// TODO Auto-generated method stub
		this.logger.debug(format, arguments);
	}

	public void debug(String msg, Throwable t) {
		// TODO Auto-generated method stub
		this.logger.debug(msg, t);
	}

	public boolean isInfoEnabled() {
		// TODO Auto-generated method stub
		return this.logger.isInfoEnabled();
	}

	public void info(String msg) {
		// TODO Auto-generated method stub
		this.logger.info(msg);
	}

	public void info(String format, Object arg) {
		// TODO Auto-generated method stub
		this.logger.info(format, arg);
	}

	public void info(String format, Object argA, Object argB) {
		// TODO Auto-generated method stub
		this.logger.info(format, argA, argB);
	}

	public void info(String format, Object... arguments) {
		// TODO Auto-generated method stub
		this.logger.info(format, arguments);
	}

	public void info(String msg, Throwable t) {
		// TODO Auto-generated method stub
		this.logger.info(msg, t);
	}

	public boolean isWarnEnabled() {
		// TODO Auto-generated method stub
		return this.logger.isWarnEnabled();
	}

	public void warn(String msg) {
		// TODO Auto-generated method stub
		this.logger.warn(msg);
	}

	public void warn(String format, Object arg) {
		// TODO Auto-generated method stub
		this.logger.warn(format, arg);
	}

	public void warn(String format, Object argA, Object argB) {
		// TODO Auto-generated method stub
		this.warn(format, argA, argB);
	}

	public void warn(String format, Object... arguments) {
		// TODO Auto-generated method stub
		this.logger.warn(format, arguments);
	}

	public void warn(String msg, Throwable t) {
		// TODO Auto-generated method stub
		this.logger.warn(msg, t);
	}

	public boolean isErrorEnabled() {
		// TODO Auto-generated method stub
		return this.logger.isErrorEnabled();
	}

	public void error(String msg) {
		// TODO Auto-generated method stub
		this.logger.error(msg);
	}

	public void error(String format, Object arg) {
		// TODO Auto-generated method stub
		this.error(format, arg);
	}

	public void error(String format, Object argA, Object argB) {
		// TODO Auto-generated method stub
		this.error(format, argA, argB);
	}

	public void error(String format, Object... arguments) {
		// TODO Auto-generated method stub
		this.error(format, arguments);
	}

	public void error(String msg, Throwable t) {
		// TODO Auto-generated method stub
		this.logger.error(msg, t);
	}

}
