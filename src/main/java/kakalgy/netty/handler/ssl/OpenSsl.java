package kakalgy.netty.handler.ssl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import kakalgy.netty.common.util.internal.logging.InternalLogger;
import kakalgy.netty.common.util.internal.logging.InternalLoggerFactory;

/**
 * Tells if <a href="http://netty.io/wiki/forked-tomcat-native.html">
 * {@code netty-tcnative}</a> and its OpenSSL support are available.
 * 
 * @author Administrator
 *
 */
public final class OpenSsl {

	private static final InternalLogger logger = InternalLoggerFactory.getInstance(OpenSsl.class);

	private static final String LINUX = "linux";
	private static final String UNKNOWN = "unknown";
	private static final Throwable UNAVAILABILITY_CAUSE;

	/**
	 * 密码套件
	 */
	static final Set<String> AVAILABLE_CIPHER_SUITES;
	private static final Set<String> AVAILABLE_OPENSSL_CIPHER_SUITES;
	private static final Set<String> AVAILABLE_JAVA_CIPHER_SUITES;

	/**
	 * 实现指定密钥管理算法的 KeyManagerFactory 对象
	 */
	private static final boolean SUPPORTS_KEYMANAGER_FACTORY;
	private static final boolean USE_KEYMANAGER_FACTORY;

	/**
	 * 协议Protocols
	 */
	static final String PROTOCOL_SSL_V2_HELLO = "SSLv2Hello";
	static final String PROTOCOL_SSL_V2 = "SSLv2";
	static final String PROTOCOL_SSL_V3 = "SSLv3";
	static final String PROTOCOL_TLS_V1 = "TLSv1";
	static final String PROTOCOL_TLS_V1_1 = "TLSv1.1";
	static final String PROTOCOL_TLS_V1_2 = "TLSv1.2";

	private static final String[] SUPPORTED_PROTOCOLS = { PROTOCOL_SSL_V2_HELLO, PROTOCOL_SSL_V2, PROTOCOL_SSL_V3, PROTOCOL_TLS_V1, PROTOCOL_TLS_V1_1, PROTOCOL_TLS_V1_2 };

	static final Set<String> SUPPORTED_PROTOCOLS_SET = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(SUPPORTED_PROTOCOLS)));

	static {
		Throwable cause = null;

		// Test if netty-tcnative is in the classpath first.
		// 测试netty-tcnative是否在classpath中
		try {
			Class.forName("org.apache.tomcat.jni.SSL", false, OpenSsl.class.getClassLoader());
		} catch (ClassNotFoundException e) {
			// TODO: handle exception
			cause = e;
			logger.debug("netty-tcnative not in the classpath; " + OpenSslEngine.class.getSimpleName() + " will be unavailable.");
		}

	}
}
