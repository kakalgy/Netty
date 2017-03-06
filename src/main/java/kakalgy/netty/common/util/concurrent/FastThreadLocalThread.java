package kakalgy.netty.common.util.concurrent;

import kakalgy.netty.common.util.internal.InternalThreadLocalMap;

/**
 * <p>
 * 一个可以能够快速进入FastThreadLocal变量的线程
 * </p>
 * A special {@link Thread} that provides fast access to {@link FastThreadLocal}
 * variables.
 */
public class FastThreadLocalThread extends Thread {

	/**
	 * the internal data structure that keeps the thread-local variables bound
	 * to this thread
	 */
	private InternalThreadLocalMap threadLocalMap;

	/**
	 * 空构造函数
	 */
	public FastThreadLocalThread() {
	}

	/**
	 * 构造函数
	 * 
	 * @param target
	 */
	public FastThreadLocalThread(Runnable target) {
		super(target);
	}

	/**
	 * 构造函数
	 * 
	 * @param group
	 * @param target
	 */
	public FastThreadLocalThread(ThreadGroup group, Runnable target) {
		super(group, target);
	}

	/**
	 * 构造函数
	 * 
	 * @param name
	 */
	public FastThreadLocalThread(String name) {
		super(name);
	}

	/**
	 * 构造函数
	 * 
	 * @param group
	 * @param name
	 */
	public FastThreadLocalThread(ThreadGroup group, String name) {
		super(group, name);
	}

	/**
	 * 构造函数
	 * 
	 * @param target
	 * @param name
	 */
	public FastThreadLocalThread(Runnable target, String name) {
		super(target, name);
	}

	/**
	 * 构造函数
	 * 
	 * @param group
	 * @param target
	 * @param name
	 */
	public FastThreadLocalThread(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
	}

	/**
	 * 构造函数
	 * 
	 * @param group
	 * @param target
	 * @param name
	 * @param stackSize
	 */
	public FastThreadLocalThread(ThreadGroup group, Runnable target, String name, long stackSize) {
		super(group, target, name, stackSize);
	}

	/**
	 * Returns the internal data structure that keeps the thread-local variables
	 * bound to this thread. Note that this method is for internal use only, and
	 * thus is subject to change at any time.
	 */
	public final InternalThreadLocalMap getInternalThreadLocalMap() {
		return this.threadLocalMap;
	}

	/**
	 * Sets the internal data structure that keeps the thread-local variables
	 * bound to this thread. Note that this method is for internal use only, and
	 * thus is subject to change at any time.
	 */
	public final void setInternalThreadLocalMap(InternalThreadLocalMap threadLocalMap) {
		this.threadLocalMap = threadLocalMap;
	}
}
