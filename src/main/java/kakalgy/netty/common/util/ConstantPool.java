package kakalgy.netty.common.util;

import java.util.concurrent.ConcurrentMap;

/**
 * A pool of {@link Constant}s.
 *
 * @param <T>
 *            the type of the constant
 */
public abstract class ConstantPool<T extends Constant<T>> {

	private final ConcurrentMap<String, T> constants = 
}
