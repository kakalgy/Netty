package kakalgy.netty.handler.ssl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import kakalgy.netty.common.util.internal.SystemPropertyUtil;
import kakalgy.netty.common.util.internal.logging.InternalLogger;
import kakalgy.netty.common.util.internal.logging.InternalLoggerFactory;

/**
 * Generates a temporary self-signed certificate(自签名的证书) for testing purposes.
 * <p>
 * <strong>NOTE:</strong> Never use the certificate and private key generated by
 * this class in production. It is purely(纯粹的) for testing purposes, and thus it
 * is very insecure. It even uses an insecure pseudo-random(伪随机的) generator for
 * faster generation internally(本质的).
 * </p>
 * <p>
 * A X.509 certificate file and a RSA private key file are generated in a
 * system's temporary directory using
 * {@link java.io.File#createTempFile(String, String)}, and they are deleted
 * when the JVM exits using {@link java.io.File#deleteOnExit()}.
 * </p>
 * <p>
 * At first, this method tries to use OpenJDK's X.509 implementation (the
 * {@code sun.security.x509} package). If it fails, it tries to use
 * <a href="http://www.bouncycastle.org/">Bouncy Castle</a> as a fallback.
 * </p>
 * <p>
 * 一种用于指定计算机在域层次结构中确切位置的明确域名。 </br>
 * 一台特定计算机或主机的完整 Internet 域名。FQDN 包括两部分：主机名和域名。例如 mycomputer.mydomain.com。 </br>
 * 一种包含主机名和域名（包括顶级域）的 URL。例如，www.symantec.com 是完全限定域名。其中 www 是主机，symantec
 * 是二级域，.com 是顶级域。FQDN 总是以主机名开始且以顶级域名结束，因此 www.sesa.symantec.com 也是一个 FQDN。
 * </p>
 * <p>
 * FQDN：(Fully Qualified Domain
 * Name)完全合格域名/全称域名，是指主机名加上全路径，全路径中列出了序列中所有域成员。全域名可以从逻辑上准确地表示出主机在什么地方，
 * 也可以说全域名是主机名的一种完全表示形式。
 * </p>
 */
public final class SelfSignedCertificate {
	private static final InternalLogger logger = InternalLoggerFactory.getInstance(SelfSignedCertificate.class);

	/**
	 * Current time minus 1 year, just in case software clock goes back due to
	 * time synchronization
	 */
	private static final Date DEFAULT_NOT_BEFORE = new Date(
			SystemPropertyUtil.getJavaSystemPropertyLong("io.netty.selfSignedCertificate.defaultNotBefore", System.currentTimeMillis() - 86400000L * 365));
	/**
	 * The maximum possible value in X.509 specification: 9999-12-31 23:59:59
	 */
	private static final Date DEFAULT_NOT_AFTER = new Date(SystemPropertyUtil.getJavaSystemPropertyLong("io.netty.selfSignedCertificate.defaultNotAfter", 253402300799000L));

	/**
	 * 验证文件,可以通过读取certificate文件来得到X509Certificate实例
	 */
	private final File certificate;
	/**
	 * 私有key文件
	 */
	private final File privateKey;
	/**
	 * X509验证实例
	 */
	private final X509Certificate cert;
	/**
	 * 私有key实例
	 */
	private final PrivateKey key;

	/**
	 * 构造函数</br>
	 * Creates a new instance.
	 * 
	 * @param fqdn
	 *            a fully qualified domain name(完全限定域名)
	 * @param random
	 *            the {@link java.security.SecureRandom} to use
	 * @param bits
	 *            the number of bits of the generated private key
	 * @param notBefore
	 *            Certificate is not valid before this time
	 * @param notAfter
	 *            Certificate is not valid after this time
	 * 
	 * @throws CertificateException
	 */
	public SelfSignedCertificate(String fqdn, SecureRandom random, int bits, Date notBefore, Date notAfter) throws CertificateException {
		// Generate an RSA key pair.
		final KeyPair keypair;
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(bits, random);
			keypair = keyGen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			// TODO: handle exception
			// Should not reach here because every Java implementation must have
			// RSA key pair generator.
			logger.error("SelfSignedCertificate: " + e.getMessage());
			throw new Error(e);
		}

		String[] paths;
		try {
			// Try the OpenJDK's proprietary implementation.
			paths = OpenJdkSelfSignedCertGenerator.generate(fqdn, keypair, random, notBefore, notAfter);
		} catch (Throwable t) {
			// TODO: handle exception
			logger.debug("Failed to generate a self-signed X.509 certificate using sun.security.x509:", t);
			try {
				// Try Bouncy Castle if the current JVM didn't have
				// sun.security.x509.
				paths = BouncyCastleSelfSignedCertGenerator.generate(fqdn, keypair, random, notBefore, notAfter);
			} catch (Throwable t2) {
				// TODO: handle exception
				logger.debug("Failed to generate a self-signed X.509 certificate using Bouncy Castle:", t2);
				throw new CertificateException("No provider succeeded to generate a self-signed certificate. " + "See debug log for the root cause.");
			}
		}

		certificate = new File(paths[0]);
		privateKey = new File(paths[1]);
		key = keypair.getPrivate();
		FileInputStream certificateInput = null;

		try {
			certificateInput = new FileInputStream(certificate);
			cert = (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(certificateInput);
		} catch (Exception e) {
			// TODO: handle exception
			throw new CertificateEncodingException(e);
		} finally {
			if (certificateInput != null) {
				try {
					certificateInput.close();
				} catch (IOException e2) {
					// TODO: handle exception
					logger.warn("Failed to close a file: " + certificate, e2);
				}
			}
		}
	}

	/**
	 * 构造函数
	 * </p>
	 * Creates a new instance.
	 *
	 * @param fqdn
	 *            a fully qualified domain name
	 * @param random
	 *            the {@link java.security.SecureRandom} to use
	 * @param bits
	 *            the number of bits of the generated private key
	 */
	public SelfSignedCertificate(String fqdn, SecureRandom random, int bits) throws CertificateException {
		this(fqdn, random, bits, DEFAULT_NOT_BEFORE, DEFAULT_NOT_AFTER);
	}

	/**
	 * 构造函数
	 * </p>
     * Creates a new instance.
     *
     * @param fqdn a fully qualified domain name
     * @param notBefore Certificate is not valid before this time
     * @param notAfter Certificate is not valid after this time
     */
	public SelfSignedCertificate(String fqdn, Date notBefore, Date notAfter) throws CertificateException {
		// Bypass entrophy collection by using insecure random generator.
		// We just want to generate it without any delay because it's for
		// testing purposes only.
		this(fqdn, ThreadLocalInsecureRandom., 1024, notBefore, notAfter);
	}

	/**
	 * 构造函数
	 * </p>
	 * Creates a new instance.
	 *
	 * @param fqdn
	 *            a fully qualified domain name
	 */
	public SelfSignedCertificate(String fqdn) throws CertificateException {
		this(fqdn, DEFAULT_NOT_BEFORE, DEFAULT_NOT_AFTER);
	}

	/**
	 * 构造函数
	 * </p>
	 * Creates a new instance.
	 * 
	 * @param notBefore
	 *            Certificate is not valid before this time
	 * @param notAfter
	 *            Certificate is not valid after this time
	 */
	public SelfSignedCertificate(Date notBefore, Date notAfter) throws CertificateException {
		this("example.com", notBefore, notAfter);
	}

	/**
	 * 构造函数</br>
	 * Creates a new instance.
	 * 
	 * @throws CertificateException
	 */
	public SelfSignedCertificate() throws CertificateException {
		// TODO Auto-generated constructor stub
		this(DEFAULT_NOT_BEFORE, DEFAULT_NOT_AFTER);
	}

	/**
	 * 
	 * @param fqdn
	 * @param key
	 * @param cert
	 * @return
	 * @throws IOException
	 * @throws CertificateEncodingException
	 */
	static String[] newSelfSignedCertificate(String fqdn, PrivateKey key, X509Certificate cert) throws IOException, CertificateEncodingException {
		// Encode the private key into a file.
		Bytebuf
	}

	/**
	 * 删除参数文件，但是会判断是否删除成功,没有返回值，当删除失败时只是在日志中打印告警
	 * 
	 * @param certFile
	 */
	private static void safeDelete(File certFile) {
		if (!certFile.delete()) {
			logger.warn("Failed to delete a file: " + certFile);
		}
	}

	/**
	 * 关闭keyOut输出流，若关闭出现异常，打印日志
	 * 
	 * @param keyFile
	 * @param keyOut
	 */
	private static void safeClose(File keyFile, OutputStream keyOut) {
		try {
			keyOut.close();
		} catch (IOException e) {
			// TODO: handle exception
			logger.warn("Failed to close a file: " + keyFile, e);
		}
	}

	/**
	 * Deletes the generated X.509 certificate file and RSA private key file.
	 */
	public void delete() {
		safeDelete(certificate);
		safeDelete(privateKey);
	}

	/************************** get/set方法 **************************/
	/**
	 * Returns the generated X.509 certificate file in PEM format.
	 */
	public File getCertificate() {
		return certificate;
	}

	/**
	 * Returns the generated RSA private key file in PEM format.
	 */
	public File getPrivateKey() {
		return privateKey;
	}

	/**
	 * Returns the generated X.509 certificate.
	 */
	public X509Certificate getCert() {
		return cert;
	}

	/**
	 * Returns the generated RSA private key.
	 */
	public PrivateKey getKey() {
		return key;
	}
}
