package kakalgy.netty.handler.ssl;

import handler.src.main.java.io.netty.handler.ssl.ReferenceCounted;
import kakalgy.netty.common.util.internal.UnstableApi;

/**
 * An enumeration of SSL/TLS protocol providers.
 * 
 * @author Administrator
 *
 */
public enum SslProvider {
	/**
	 * JDK's default implementation.
	 */
	JDK,
	/**
	 * OpenSSL-based implementation.
	 */
	OPENSSL,
	/**
	 * OpenSSL-based implementation which does not have finalizers and instead
	 * implements {@link ReferenceCounted}.
	 */
	@UnstableApi OPENSSL_REFCNT
}
