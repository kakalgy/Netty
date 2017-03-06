package kakalgy.netty.common.util.internal;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 这个内部的数据结构存储的是为Netty和所有的FastThreadLocal的thread-local变量
 * </p>
 * The internal data structure that stores the thread-local variables for Netty
 * and all {@link FastThreadLocal}s. Note that this class is for internal use
 * only and is subject to change at any time. Use {@link FastThreadLocal} unless
 * you know what you are doing.
 */
class UnpaddedInternalThreadLocalMap {

	/**
	 * 
	 */
	static final ThreadLocal<InternalThreadLocalMap> slowThreadLocalMap = new ThreadLocal<InternalThreadLocalMap>();
	/**
	 * 
	 */
	static final AtomicInteger nextIndex = new AtomicInteger();

	/** Used by {@link FastThreadLocal} */
	Object[] indexedVariables;
	
	//Core thread-locals
	int futureListenerStackDepth;
	int localChannelReaderStackDepth;
	Map<Class<?>, Boolean> handlerSharableCache;
	IntegerHolder counterHashCode;
	ThreadLocalRandom random;
	Map<Class<?>, TypePa>
}
