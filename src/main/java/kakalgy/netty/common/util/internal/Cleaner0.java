package kakalgy.netty.common.util.internal;

import java.nio.ByteBuffer;

/**
 * Allows to free direct {@link ByteBuffer} by using Cleaner. This is
 * encapsulated in an extra class to be able to use {@link PlatformDependent0}
 * on Android without problems.
 *
 * For more details see
 * <a href="https://github.com/netty/netty/issues/2604">#2604</a>.
 */
public class Cleaner0 {

	
}
