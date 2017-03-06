package kakalgy.netty.buffer;

/**
 * <p>
 * 线程安全的分配缓冲区接口
 * </p>
 * Implementations are responsible to allocate(分配) buffers. Implementations of
 * this interface are expected to be thread-safe.
 */
public interface ByteBufAllocator {

	ByteBufAllocator DEFAULT = by
}
