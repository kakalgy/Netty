package kakalgy.netty.common.util.internal.logging;

import static org.junit.Assert.*;

import org.junit.Test;

public class Slf4JLoggerFactoryTest {
	@Test
	public void testCreation() {
		InternalLogger logger = Slf4JLoggerFactory.INSTANCE.newInstance("foo");
		assertTrue(logger instanceof Slf4JLogger);
		 System.out.println(logger.getLoggerInstanceName());
		 assertEquals("foo", logger.getLoggerInstanceName());
//		System.out.println(logger.name());
//		assertEquals("foo", logger.name());
	}
}
