package kakalgy.netty.common.util.internal;

import kakalgy.netty.common.util.concurrent.FastThreadLocalThread;

/**
 * <p>
 * 这个内部的数据结构存储的是为Netty和所有的FastThreadLocal的thread-local变量
 * </p>
 * The internal data structure that stores the thread-local variables for Netty
 * and all {@link FastThreadLocal}s. Note that this class is for internal use
 * only and is subject to change at any time. Use {@link FastThreadLocal} unless
 * you know what you are doing.
 */
public class InternalThreadLocalMap extends UnpaddedInternalThreadLocalMap {

	/**
	 * 
	 */
	private static final int DEFAULT_ARRAY_LIST_INITIAL_CAPACITY = 8;
	/**
	 * 
	 */
	public static final Object UNSET = new Object();

	/**
	 * 当当前线程是FastThreadLocalThread实例时，返回FastThreadLocalThread的threadLocalMap，
	 * 否则返回slowThreadLocalMap的InternalThreadLocalMap
	 * 
	 * @return
	 */
	public static InternalThreadLocalMap getIfSet() {
		Thread thread = Thread.currentThread();
		if (thread instanceof FastThreadLocalThread) {
			return ((FastThreadLocalThread) thread).getInternalThreadLocalMap();
		}
		return slowThreadLocalMap.get();
	}

	/**
	 * 
	 * @return
	 */
	public static InternalThreadLocalMap get(){
		Thread thread = Thread.currentThread();
		if()
	}

	/**
	 * 快速获得thread的InternalThreadLocalMap
	 * 
	 * @param thread
	 * @return
	 */
	private static InternalThreadLocalMap fastGet(FastThreadLocalThread thread) {
		InternalThreadLocalMap threadLocalMap = thread.getInternalThreadLocalMap();
		if (threadLocalMap == null) {
			thread.setInternalThreadLocalMap(threadLocalMap = new InternalThreadLocalMap());
		}
		return threadLocalMap;
	}

	/**
	 * 
	 * @return
	 */
	private static InternalThreadLocalMap slowGet() {
		ThreadLocal<InternalThreadLocalMap> slowThreadLocalMap = UnpaddedInternalThreadLocalMap.slowThreadLocalMap;
		InternalThreadLocalMap ret = slowThreadLocalMap.get();
		if (ret == null) {
			ret = new InternalThreadLocalMap();
			slowThreadLocalMap.set(ret);
		}
		return ret;
	}

}
