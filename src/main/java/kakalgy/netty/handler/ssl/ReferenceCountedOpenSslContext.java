package kakalgy.netty.handler.ssl;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

import kakalgy.netty.common.util.ReferenceCounted;
import kakalgy.netty.common.util.internal.SystemPropertyUtil;
import kakalgy.netty.common.util.internal.logging.InternalLogger;
import kakalgy.netty.common.util.internal.logging.InternalLoggerFactory;

/**
 * An implementation of {@link SslContext} which works with libraries that
 * support the <a href="https://www.openssl.org/">OpenSsl</a> C library API.
 * <p>
 * Instances of this class must be {@link #release() released} or else native
 * memory will leak!(这个类的实例必须被释放，否则会导致内存泄漏)
 *
 * <p>
 * Instances of this class <strong>must not</strong> be released before any
 * {@link ReferenceCountedOpenSslEngine} which depends upon the instance of this
 * class is released. Otherwise if any method of
 * {@link ReferenceCountedOpenSslEngine} is called which uses this class's JNI
 * resources the JVM may
 * crash.(如果任何一个基于这个类的实例的ReferenceCountedOpenSslEngine没有被释放，则这个类的实例也不能被释放，
 * 否则再调用ReferenceCountedOpenSslEngine的方法使用这个类的JNI资源时会导致JVM崩溃)
 */
public abstract class ReferenceCountedOpenSslContext extends SslContext implements ReferenceCounted {
	private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslContext.class);

	/**
	 * 获得rejectClientInitiatedRenegotiation属性，属于SSL部分内容:Client-initiated
	 * renegotiation
	 */
	private static final boolean JDK_REJECT_CLIENT_INITIATED_RENEGOTIATION = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
		public Boolean run() {
			// TODO Auto-generated method stub
			return SystemPropertyUtil.getJavaSystemPropertyBoolean("jdk.tls.rejectClientInitiatedRenegotiation", false);
		}
	});
	/**
	 * 默认密码
	 */
	private static final List<String> DEFAULT_CIPHERS;
	/**
	 * 
	 */
	private static final Integer DH_KEY_LENGTH;

}
