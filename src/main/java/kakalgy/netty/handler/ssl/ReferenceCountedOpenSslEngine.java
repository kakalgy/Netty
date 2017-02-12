package kakalgy.netty.handler.ssl;

import javax.net.ssl.SSLEngine;

import kakalgy.netty.common.util.ReferenceCounted;

/**
 * Implements a {@link SSLEngine} using
 * <a href="https://www.openssl.org/docs/crypto/BIO_s_bio.html#EXAMPLE">OpenSSL
 * BIO abstractions</a>.
 * <p>
 * Instances of this class must be {@link #release() released} or else native
 * memory will leak!(这个类的实例必须被释放，否则会导致内存泄漏)
 *
 * <p>
 * Instances of this class <strong>must</strong> be released before the
 * {@link ReferenceCountedOpenSslContext} the instance depends upon are
 * released. Otherwise if any method of this class is called which uses the the
 * {@link ReferenceCountedOpenSslContext} JNI resources the JVM may crash.
 * (如果任何一个基于这个类的实例的ReferenceCountedOpenSslContext没有被释放，则这个类的实例也不能被释放，
 * 否则再调用ReferenceCountedOpenSslContext的方法使用这个类的JNI资源时会导致JVM崩溃)
 */
public class ReferenceCountedOpenSslEngine extends SSLEngine implements ReferenceCounted {

}
