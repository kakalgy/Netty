package kakalgy.netty.common.util.internal;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;

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
	 * 默认的初始化ArrayList的容量
	 */
	private static final int DEFAULT_ARRAY_LIST_INITIAL_CAPACITY = 8;
	/**
	 * 
	 */
	public static final Object UNSET = new Object();

	/**
	 * 构造函数
	 */
	private InternalThreadLocalMap() {
		super(newIndexedVariableTable());
	}

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
	public static InternalThreadLocalMap get() {
		Thread thread = Thread.currentThread();
		if (thread instanceof FastThreadLocalThread) {
			return fastGet((FastThreadLocalThread) thread);
		} else {
			return slowGet();
		}
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
	 * 返回默认的slowThreadLocalMap，来自于UnpaddedInternalThreadLocalMap
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

	/**
	 * 将线程中的InternalThreadLocalMap设置为null
	 */
	public static void remove() {
		Thread thread = Thread.currentThread();
		if (thread instanceof FastThreadLocalThread) {
			((FastThreadLocalThread) thread).setInternalThreadLocalMap(null);
		} else {
			slowThreadLocalMap.remove();
		}
	}

	/**
	 * 移除 the internal data structure that keeps the thread-local variables
	 * bound to this thread
	 */
	public static void destroy() {
		slowThreadLocalMap.remove();
	}

	/**
	 * 返回一个自增长的索引 int类型
	 * 
	 * @return
	 */
	public static int nextVaraibleIndex() {
		int index = nextIndex.getAndIncrement();
		if (index < 0) {
			nextIndex.decrementAndGet();
			throw new IllegalStateException("too many thread-local indexed variables");
		}
		return index;
	}

	/**
	 * 返回最后的一个索引值
	 * 
	 * @return
	 */
	public static int lastVariableIndex() {
		return nextIndex.get() - 1;
	}

	// Cache line padding (must be public)
	// With CompressedOops(压缩普通对象指针) enabled, an instance of this class should
	// occupy at least 128 bytes.
	public long rp1, rp2, rp3, rp4, rp5, rp6, rp7, rp8, rp9;

	/**
	 * 返回一个32位Object数组，数组中每个Object都是用UNSET初始化
	 * 
	 * @return
	 */
	private static Object[] newIndexedVariableTable() {
		Object[] array = new Object[32];
		Arrays.fill(array, UNSET);// 将array数组全部用UNSET初始化
		return array;
	}

	/**
	 * 计算UnpaddedInternalThreadLocalMap中的所有属性中有多少个属性被设置过
	 * 
	 * @return
	 */
	public int size() {
		int count = 0;

		if (futureListenerStackDepth != 0) {
			count++;
		}
		if (localChannelReaderStackDepth != 0) {
			count++;
		}
		if (handlerSharableCache != null) {
			count++;
		}
		if (counterHashCode != null) {
			count++;
		}
		if (random != null) {
			count++;
		}
		if (typeParameterMatcherGetCache != null) {
			count++;
		}
		if (typeParameterMatcherFindCache != null) {
			count++;
		}
		if (stringBuilder != null) {
			count++;
		}
		if (charsetEncoderCache != null) {
			count++;
		}
		if (charsetDecoderCache != null) {
			count++;
		}
		if (arrayList != null) {
			count++;
		}

		for (Object o : indexedVariables) {
			if (o != UNSET) {
				count++;
			}
		}

		// ?????
		// We should subtract 1 from the count because the first element in
		// 'indexedVariables' is reserved
		// by 'FastThreadLocal' to keep the list of 'FastThreadLocal's to remove
		// on 'FastThreadLocal.removeAll()'.
		return count - 1;
	}

	/**
	 * 返回stringbuilder，若未设置过，则设置StringBuilder初始化
	 * 
	 * @return
	 */
	public StringBuilder stringBuilder() {
		StringBuilder builder = stringBuilder;
		if (builder == null) {
			stringBuilder = builder = new StringBuilder(512);
		} else {
			builder.setLength(0);
		}
		return builder;
	}

	/**
	 * 返回charsetEncoderCache，若未设置过，则设置charsetEncoderCache初始化
	 * 
	 * @return
	 */
	public Map<Charset, CharsetEncoder> charsetEncoderCache() {
		Map<Charset, CharsetEncoder> cache = charsetEncoderCache;
		if (cache == null) {
			// 在 IdentityHashMap 中，当且仅当 (k1==k2) 时，才认为两个键 k1 和 k2 相等（在正常 Map
			// 实现（如 HashMap）中，
			// 当且仅当满足下列条件时才认为两个键 k1 和 k2 相等：(k1==null ? k2==null :
			// e1.equals(e2))）。
			charsetEncoderCache = cache = new IdentityHashMap<Charset, CharsetEncoder>();
		}
		return cache;
	}

	/**
	 * 返回charsetDecoderCache，若未设置过，则设置charsetDecoderCache初始化
	 * 
	 * @return
	 */
	public Map<Charset, CharsetDecoder> charsetDecoderCache() {
		Map<Charset, CharsetDecoder> cache = charsetDecoderCache;
		if (cache == null) {
			charsetDecoderCache = cache = new IdentityHashMap<Charset, CharsetDecoder>();
		}
		return cache;
	}

	/**
	 * 重构函数 见arrayList(int minCapacity)
	 * 
	 * @return
	 */
	public <E> ArrayList<E> arrayList() {
		return arrayList(DEFAULT_ARRAY_LIST_INITIAL_CAPACITY);
	}

	/**
	 * 返回arrayList，但是是个空的list，若arrayList未设置，则初始化为最小的容量
	 * 
	 * @param minCapacity
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E> ArrayList<E> arrayList(int minCapacity) {
		ArrayList<E> list = (ArrayList<E>) arrayList;
		if (list == null) {
			arrayList = new ArrayList<Object>(minCapacity);
			return (ArrayList<E>) arrayList;
		}
		list.clear();
		list.ensureCapacity(minCapacity);
		return list;
	}
	
	public int futureListenerStackDepth(){
		return this.futureListenerStackDepth;
	}
	
	public void setFutureListenerStackDepth(int futureListenerStackDepth){
		this.futureListenerStackDepth = futureListenerStackDepth;
	}
	
	
}
