package kakalgy.netty.common.util.internal;

import java.util.Map;

public abstract class TypeParameterMatcher {

	/**
	 * 
	 */
	private static final TypeParameterMatcher NOOP = new NoOpTypeParameterMatcher();
	/**
	 * 
	 */
	private static final Object TEST_OBJECT = new Object();

	/**
	 * 空构造函数
	 */
	protected TypeParameterMatcher() {

	}
	
	public static TypeParameterMatcher get(final Class<?> parameterType){
		final Map<Class<?>, TypeParameterMatcher> getCache = InternalThreadLocalMap.get().
	}

	public abstract boolean match(Object msg);

}
