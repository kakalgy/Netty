package kakalgy.netty.common.util;

/**
 * A reference-counted object(引用计数对象) that requires explicit deallocation(显式释放).
 * <p>
 * When a new {@link ReferenceCounted} is instantiated(实例化), it starts with the
 * reference count of {@code 1}. {@link #retain()} increases the reference
 * count(retain()方法增加引用计数), and {@link #release()} decreases the reference
 * count(release()方法减少引用计数). If the reference count is decreased to {@code 0},
 * the object will be deallocated explicitly, and accessing the deallocated
 * object will usually result in an access
 * violation.（当引用计数减为0时，对象会被显式释放，访问被释放的对象会导致访问冲突）
 * </p>
 * <p>
 * If an object that implements {@link ReferenceCounted} is a container of other
 * objects that implement {@link ReferenceCounted}, the contained objects will
 * also be released via {@link #release()} when the container's reference count
 * becomes 0.(如果实现ReferenceCounted的对象是实现ReferenceCounted的其他对象的容器，则当容器的引用计数变为0时，
 * 包含的对象也将通过release（）释放。)
 * </p>
 */
public interface ReferenceCounted {
	/**
	 * Returns the reference count of this object. If {@code 0}, it means this
	 * object has been deallocated.
	 * 
	 * @return
	 */
	int refCnt();

	/**
	 * Increases the reference count by {@code 1}.
	 * 
	 * @return
	 */
	ReferenceCounted retain();

	/**
	 * Increases the reference count by the specified {@code increment}.
	 * 
	 * @param increment
	 * @return
	 */
	ReferenceCounted retain(int increment);

	/**
	 * Records the current access location of this object for debugging
	 * purposes. If this object is determined to be leaked, the information
	 * recorded by this operation will be provided to you via
	 * {@link ResourceLeakDetector}. This method is a shortcut to
	 * {@link #touch(Object) touch(null)}.
	 * (记录目前Object的进入位置，如果Object泄露了，则会通过ResourceLeakDetector来提供信息)
	 * 
	 * @return
	 */
	ReferenceCounted touch();

	/**
	 * Records the current access location of this object with an additional
	 * arbitrary(主观的) information for debugging purposes. If this object is
	 * determined to be leaked, the information recorded by this operation will
	 * be provided to you via {@link ResourceLeakDetector}.
	 */
	ReferenceCounted touch(Object hint);

	/**
	 * Decreases the reference count by {@code 1} and deallocates this object if
	 * the reference count reaches at {@code 0}. (引用计数减一，如果计数到达0，则释放对象)
	 *
	 * @return {@code true} if and only if the reference count became {@code 0}
	 *         and this object has been deallocated
	 */
	boolean release();

	/**
	 * Decreases the reference count by the specified {@code decrement} and
	 * deallocates this object if the reference count reaches at {@code 0}.
	 *
	 * @return {@code true} if and only if the reference count became {@code 0}
	 *         and this object has been deallocated
	 */
	boolean release(int decrement);
}
