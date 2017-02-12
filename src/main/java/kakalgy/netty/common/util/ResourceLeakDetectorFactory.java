package kakalgy.netty.common.util;

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;

import kakalgy.netty.common.util.internal.PlatformDependent;
import kakalgy.netty.common.util.internal.SystemPropertyUtil;
import kakalgy.netty.common.util.internal.logging.InternalLogger;
import kakalgy.netty.common.util.internal.logging.InternalLoggerFactory;

/**
 * This static factory should be used to load {@link ResourceLeakDetector}s as
 * needed.
 * 
 * </br>
 * </br>
 * 
 * 检测资源是否泄漏的类
 */
public abstract class ResourceLeakDetectorFactory {

	private static final InternalLogger logger = InternalLoggerFactory.getInstance(ResourceLeakDetectorFactory.class);

	private static volatile ResourceLeakDetectorFactory factoryInstance = new DefaultResourceLeakDetectorFactory();

	/**
	 * Get the singleton instance of this factory class.(返回单例实例)
	 *
	 * @return - the current {@link ResourceLeakDetectorFactory}
	 */
	public static ResourceLeakDetectorFactory instance() {
		return factoryInstance;
	}

	/**
	 * 默认的配置生成的ResourceLeakDetectorFactory类</br>
	 * </br>
	 * Default implementation that loads custom leak detector via system
	 * property
	 */
	private static final class DefaultResourceLeakDetectorFactory extends ResourceLeakDetectorFactory {
		private final Constructor<?> customClassConstructor;

		/**
		 * 构造函数
		 */
		public DefaultResourceLeakDetectorFactory() {
			// TODO Auto-generated constructor stub
			String customLeakDetector;
			try {
				customLeakDetector = AccessController.doPrivileged(new PrivilegedAction<String>() {
					public String run() {
						// TODO Auto-generated method stub
						return SystemPropertyUtil.getJavaSystemPropertyString("io.netty.customResourceLeakDetector");
					}
				});
			} catch (Throwable cause) {
				// TODO: handle exception
				logger.error("Could not access System property: io.netty.customResourceLeakDetector ", cause);
				customLeakDetector = null;
			}

			customClassConstructor = customLeakDetector == null ? null : customClassConstructorMethod(customLeakDetector);
		}

		/**
		 * 
		 * @param customLeakDetector
		 * @return
		 */
		private static Constructor<?> customClassConstructorMethod(
				String customLeakDetector) {
			try {
				final Class<?> detectorClass = Class.forName(
						customLeakDetector, true,
						PlatformDependent.getSystemClassLoader());
				
				if()
			} catch (Throwable t) {
				// TODO: handle exception
				logger.error(
						"Could not load custom resource leak detector class provided: {}",
						customLeakDetector, t);
			}
			return null;
		}
		
		
	}

}
