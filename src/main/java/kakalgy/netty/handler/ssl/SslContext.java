package kakalgy.netty.handler.ssl;

import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import handler.src.main.java.io.netty.handler.ssl.SslProvider;

public abstract class SslContext {

	static final CertificateFactory X509_CERT_FACTORY;

	static {
		try {
			X509_CERT_FACTORY = CertificateFactory.getInstance("X.509");
		} catch (CertificateException e) {
			// TODO: handle exception
			throw new IllegalStateException("unable to instance X.509 CertificateFactory", e);
		}
	}

	private final boolean startTls;

	/**
	 * 返回当前服务器端的默认Provider实现</br>
	 * Returns the default server-side implementation provider currently in use.
	 *
	 * @return {@link SslProvider#OPENSSL} if OpenSSL is available.
	 *         {@link SslProvider#JDK} otherwise.
	 * @return
	 */
	public static SslProvider defaultServerProvider() {
		return defaultProvider();
	}

	private static SslProvider defaultProvider(){
		if(OpenSsl)
	}
}
