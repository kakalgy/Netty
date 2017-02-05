package kakalgy.netty.common.util.internal.logging;

import org.junit.Test;
import org.slf4j.Logger;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class Slf4JLoggerTest {
	private static final Exception e = new Exception();

	@Test
	public void testIsTraceEnabled() {
		Logger mock = createStrictMock(Logger.class);

		expect(mock.getName()).andReturn("foo");
		expect(mock.isTraceEnabled()).andReturn(true);
		replay(mock);

		InternalLogger logger = new Slf4JLogger(mock);
		assertTrue(logger.isTraceEnabled());
		verify(mock);
	}

	@Test
	public void testIsDebugEnabled() {
		Logger mock = createStrictMock(Logger.class);

		expect(mock.getName()).andReturn("foo");
		expect(mock.isDebugEnabled()).andReturn(true);
		replay(mock);

		InternalLogger logger = new Slf4JLogger(mock);
		assertTrue(logger.isDebugEnabled());
		verify(mock);
	}

	@Test
	public void testIsInfoEnabled() {
		Logger mock = createStrictMock(Logger.class);

		expect(mock.getName()).andReturn("foo");
		expect(mock.isInfoEnabled()).andReturn(true);
		replay(mock);

		InternalLogger logger = new Slf4JLogger(mock);
		assertTrue(logger.isInfoEnabled());
		verify(mock);
	}

	@Test
	public void testIsWarnEnabled() {
		Logger mock = createStrictMock(Logger.class);

		expect(mock.getName()).andReturn("foo");
		expect(mock.isWarnEnabled()).andReturn(true);
		replay(mock);

		InternalLogger logger = new Slf4JLogger(mock);
		assertTrue(logger.isWarnEnabled());
		verify(mock);
	}

	@Test
	public void testIsErrorEnabled() {
		Logger mock = createStrictMock(Logger.class);

		expect(mock.getName()).andReturn("foo");
		expect(mock.isErrorEnabled()).andReturn(true);
		replay(mock);

		InternalLogger logger = new Slf4JLogger(mock);
		assertTrue(logger.isErrorEnabled());
		verify(mock);
	}

	@Test
	public void testTrace() {
		Logger mock = createStrictMock(Logger.class);

		expect(mock.getName()).andReturn("foo");
		mock.trace("a");
		replay(mock);

		InternalLogger logger = new Slf4JLogger(mock);
		logger.trace("a");
		verify(mock);
	}

	@Test
	public void testTraceWithException() {
		Logger mock = createStrictMock(Logger.class);

		expect(mock.getName()).andReturn("foo");
		mock.trace("a", e);
		replay(mock);

		InternalLogger logger = new Slf4JLogger(mock);
		logger.trace("a", e);
		verify(mock);
	}

	@Test
	public void testDebug() {
		Logger mock = createStrictMock(Logger.class);

		expect(mock.getName()).andReturn("foo");
		mock.debug("a");
		replay(mock);

		InternalLogger logger = new Slf4JLogger(mock);
		logger.debug("a");
		verify(mock);
	}

	@Test
	public void testDebugWithException() {
		Logger mock = createStrictMock(Logger.class);

		expect(mock.getName()).andReturn("foo");
		mock.debug("a", e);
		replay(mock);

		InternalLogger logger = new Slf4JLogger(mock);
		logger.debug("a", e);
		verify(mock);
	}

	@Test
	public void testInfo() {
		Logger mock = createStrictMock(Logger.class);

		expect(mock.getName()).andReturn("foo");
		mock.info("a");
		replay(mock);

		InternalLogger logger = new Slf4JLogger(mock);
		logger.info("a");
		verify(mock);
	}

	@Test
	public void testInfoWithException() {
		Logger mock = createStrictMock(Logger.class);

		expect(mock.getName()).andReturn("foo");
		mock.info("a", e);
		replay(mock);

		InternalLogger logger = new Slf4JLogger(mock);
		logger.info("a", e);
		verify(mock);
	}

	@Test
	public void testWarn() {
		Logger mock = createStrictMock(Logger.class);

		expect(mock.getName()).andReturn("foo");
		mock.warn("a");
		replay(mock);

		InternalLogger logger = new Slf4JLogger(mock);
		logger.warn("a");
		verify(mock);
	}

	@Test
	public void testWarnWithException() {
		Logger mock = createStrictMock(Logger.class);

		expect(mock.getName()).andReturn("foo");
		mock.warn("a", e);
		replay(mock);

		InternalLogger logger = new Slf4JLogger(mock);
		logger.warn("a", e);
		verify(mock);
	}

	@Test
	public void testError() {
		Logger mock = createStrictMock(Logger.class);

		expect(mock.getName()).andReturn("foo");
		mock.error("a");
		replay(mock);

		InternalLogger logger = new Slf4JLogger(mock);
		logger.error("a");
		verify(mock);
	}

	@Test
	public void testErrorWithException() {
		Logger mock = createStrictMock(Logger.class);

		expect(mock.getName()).andReturn("foo");
		mock.error("a", e);
		replay(mock);

		InternalLogger logger = new Slf4JLogger(mock);
		logger.error("a", e);
		verify(mock);
	}
}
