package kakalgy.netty.common.util.internal.logging;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InternalLoggerFactoryTest {

	private static final Exception e = new Exception();
	private InternalLoggerFactory oldLoggerFactory;
	private InternalLogger mock;

	@Before
	public void init() {
		oldLoggerFactory = InternalLoggerFactory.getDefaultFactory();
		InternalLoggerFactory mockFactory = EasyMock.createMock(InternalLoggerFactory.class);
		mock = EasyMock.createStrictMock(InternalLogger.class);
		EasyMock.expect(mockFactory.newInstance("mock")).andReturn(mock).anyTimes();
		EasyMock.replay(mockFactory);
		InternalLoggerFactory.setDefaultFactory(mockFactory);
	}

	@After
	public void destroy() {
		EasyMock.reset(mock);
		InternalLoggerFactory.setDefaultFactory(oldLoggerFactory);
	}

	@Test(expected = NullPointerException.class)
	public void shouldNotAllowNullDefaultFactory() {
		InternalLoggerFactory.setDefaultFactory(null);
	}

	@Test
	public void shouldGetInstance() {
		InternalLoggerFactory.setDefaultFactory(oldLoggerFactory);

		String helloWorld = "Hello World!";
		InternalLogger one = InternalLoggerFactory.getInstance("helloworld");
		InternalLogger two = InternalLoggerFactory.getInstance(helloWorld.getClass());

		assertNotNull(one);
		assertNotNull(two);
		assertNotSame(one, two);
	}

	@Test
	public void testIsTraceEnabled() {
		EasyMock.expect(mock.isTraceEnabled()).andReturn(true);
		EasyMock.replay(mock);

		InternalLogger logger = InternalLoggerFactory.getInstance("mock");
		assertTrue(logger.isTraceEnabled());
		EasyMock.verify(mock);
	}

	@Test
	public void testIsDebugEnabled() {
		EasyMock.expect(mock.isDebugEnabled()).andReturn(true);
		EasyMock.replay(mock);

		InternalLogger logger = InternalLoggerFactory.getInstance("mock");
		assertTrue(logger.isDebugEnabled());
		EasyMock.verify(mock);
	}

	@Test
	public void testIsInfoEnabled() {
		EasyMock.expect(mock.isInfoEnabled()).andReturn(true);
		EasyMock.replay(mock);

		InternalLogger logger = InternalLoggerFactory.getInstance("mock");
		assertTrue(logger.isInfoEnabled());
		EasyMock.verify(mock);
	}

	@Test
	public void testIsWarnEnabled() {
		EasyMock.expect(mock.isWarnEnabled()).andReturn(true);
		EasyMock.replay(mock);

		InternalLogger logger = InternalLoggerFactory.getInstance("mock");
		assertTrue(logger.isWarnEnabled());
		EasyMock.verify(mock);
	}

	@Test
	public void testIsErrorEnabled() {
		EasyMock.expect(mock.isErrorEnabled()).andReturn(true);
		EasyMock.replay(mock);

		InternalLogger logger = InternalLoggerFactory.getInstance("mock");
		assertTrue(logger.isErrorEnabled());
		EasyMock.verify(mock);
	}

	@Test
	public void testTrace() {
		mock.trace("a");
		EasyMock.replay(mock);

		InternalLogger logger = InternalLoggerFactory.getInstance("mock");
		logger.trace("a");
		EasyMock.verify(mock);
	}

	@Test
	public void testTraceWithException() {
		mock.trace("a", e);
		EasyMock.replay(mock);

		InternalLogger logger = InternalLoggerFactory.getInstance("mock");
		logger.trace("a", e);
		EasyMock.verify(mock);
	}

	@Test
	public void testDebug() {
		mock.debug("a");
		EasyMock.replay(mock);

		InternalLogger logger = InternalLoggerFactory.getInstance("mock");
		logger.debug("a");
		EasyMock.verify(mock);
	}

	@Test
	public void testDebugWithException() {
		mock.debug("a", e);
		EasyMock.replay(mock);

		InternalLogger logger = InternalLoggerFactory.getInstance("mock");
		logger.debug("a", e);
		EasyMock.verify(mock);
	}

	@Test
	public void testInfo() {
		mock.info("a");
		EasyMock.replay(mock);

		InternalLogger logger = InternalLoggerFactory.getInstance("mock");
		logger.info("a");
		EasyMock.verify(mock);
	}

	@Test
	public void testInfoWithException() {
		mock.info("a", e);
		EasyMock.replay(mock);

		InternalLogger logger = InternalLoggerFactory.getInstance("mock");
		logger.info("a", e);
		EasyMock.verify(mock);
	}

	@Test
	public void testWarn() {
		mock.warn("a");
		EasyMock.replay(mock);

		InternalLogger logger = InternalLoggerFactory.getInstance("mock");
		logger.warn("a");
		EasyMock.verify(mock);
	}

	@Test
	public void testWarnWithException() {
		mock.warn("a", e);
		EasyMock.replay(mock);

		InternalLogger logger = InternalLoggerFactory.getInstance("mock");
		logger.warn("a", e);
		EasyMock.verify(mock);
	}

	@Test
	public void testError() {
		mock.error("a");
		EasyMock.replay(mock);

		InternalLogger logger = InternalLoggerFactory.getInstance("mock");
		logger.error("a");
		EasyMock.verify(mock);
	}

	@Test
	public void testErrorWithException() {
		mock.error("a", e);
		EasyMock.replay(mock);

		InternalLogger logger = InternalLoggerFactory.getInstance("mock");
		logger.error("a", e);
		EasyMock.verify(mock);
	}
}
