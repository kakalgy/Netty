package kakalgy.netty.common.util.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

import kakalgy.netty.common.util.internal.logging.InternalLogger;
import kakalgy.netty.common.util.internal.logging.InternalLoggerFactory;
import sun.misc.Unsafe;

/**
 * The {@link PlatformDependent} operations which requires access to
 * {@code sun.misc.*}.
 */
public class PlatformDependent0 {
	private static final InternalLogger logger = InternalLoggerFactory.getInstance(PlatformDependent0.class);

	private PlatformDependent0() {
		// TODO Auto-generated constructor stub
	}

	static final Unsafe UNSAFE;

	/**
	 * 
	 */
	private static final long ADDRESS_FIELD_OFFSET;
	/**
	 * 
	 */
	private static final long BYTE_ARRAY_BASE_OFFSET;
	/**
	 * 
	 */
	private static final Constructor<?> DIRECT_BUFFER_CONSTRUCTOR;

	// constants borrowed from murmur3
	static final int HASH_CODE_ASCII_SEED = 0xc2b2ae35;
	static final int HASH_CODE_C1 = 0x1b873593;
	static final int HASH_CODE_C2 = 0x1b873593;

	/**
	 * Limits the number of bytes to copy per
	 * {@link Unsafe#copyMemory(long, long, long)} to allow safepoint polling
	 * during a large copy.
	 */
	private static final long UNSAFE_COPY_THRESHOLD = 1024L * 1024L;

	/**
	 * 
	 */
	private static final boolean UNALIGNED;

	/**
	 * 静态体
	 */
	static {
		final ByteBuffer direct;
		final Field addressField;

		if (PlatformDependent.isExplicitNoUnsafe()) {
			direct = null;
			addressField = null;
		} else {
			direct = ByteBuffer.allocate(1);
			// attempt to access field Buffer#address
			final Object maybeAddressField = AccessController.doPrivileged(new PrivilegedAction<Object>() {
				public Object run() {
					// TODO Auto-generated method stub
					try {
						final Field field = Buffer.class.getDeclaredField("address");
						field.setAccessible(true);
						/*
						 * if direct really is a direct buffer, address will be
						 * non-zero
						 */
						if (field.getLong(direct) == 0) {
							return null;
						}
						return field;
					} catch (IllegalAccessException e) {
						return e;
					} catch (NoSuchFieldException e) {
						return e;
					} catch (SecurityException e) {
						return e;
					}
				}
			});

			if (maybeAddressField instanceof Field) {
				addressField = (Field) maybeAddressField;
				logger.debug("java.nio.Buffer.address: available");
			} else {
				logger.debug("java.nio.Buffer.address: unavailable", (Exception) maybeAddressField);
				addressField = null;
			}
		}

		Unsafe unsafe;
		if (addressField != null) {
			// attempt to access field Unsafe#theUnsafe
			final Object maybeUnsafe = AccessController.doPrivileged(new PrivilegedAction<Object>() {
				public Object run() {
					// TODO Auto-generated method stub
					try {
						final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
						unsafeField.setAccessible(true);
						// the unsafe instance
						return unsafeField.get(null);
					} catch (NoSuchFieldException e) {
						return e;
					} catch (SecurityException e) {
						return e;
					} catch (IllegalAccessException e) {
						return e;
					}
				}
			});

			/*
			 * the conditional check here can not be replaced with checking that
			 * maybeUnsafe is an instanceof Unsafe and reversing the if and else
			 * blocks; this is because an instanceof check against Unsafe will
			 * trigger a class load and we might not have the runtime permission
			 * accessClassInPackage.sun.misc
			 */
			if (maybeUnsafe instanceof Exception) {
				unsafe = null;
				logger.debug("sun.misc.Unsafe.theUnsafe: unavailable", (Exception) maybeUnsafe);
			} else {
				unsafe = (Unsafe) maybeUnsafe;
				logger.debug("sun.misc.Unsafe.theUnsafe: available");
			}

			// ensure the unsafe supports all necessary methods to work around
			// the mistake in the latest OpenJDK

			// https://github.com/netty/netty/issues/1061
			// http://www.mail-archive.com/jdk6-dev@openjdk.java.net/msg00698.html
			if (unsafe != null) {
				final Unsafe finalUnsafe = unsafe;
				final Object maybeException = AccessController.doPrivileged(new PrivilegedAction<Object>() {
					public Object run() {
						// TODO Auto-generated method stub
						try {
							finalUnsafe.getClass().getDeclaredMethod("copyMemory", Object.class, long.class, Object.class, long.class, long.class);
							return null;
						} catch (NoSuchMethodException e) {
							return e;
						} catch (SecurityException e) {
							return e;
						}
					}
				});

				if (maybeException == null) {
					logger.debug("sun.misc.Unsafe.copyMemory: available");
				} else {
					// Unsafe.copyMemory(Object, long, Object, long, long)
					// unavailable.
					unsafe = null;
					logger.debug("sun.misc.Unsafe.copyMemory: unavailable", (Exception) maybeException);
				}
			}

		} else {
			// If we cannot access the address of a direct buffer, there's no
			// point of using unsafe.

			// Let's just pretend unsafe is unavailable for overall simplicity.
			unsafe = null;
		}

		UNSAFE = unsafe;

		if (unsafe == null) {
			ADDRESS_FIELD_OFFSET = -1;
			BYTE_ARRAY_BASE_OFFSET = -1;
			UNALIGNED = false;
			DIRECT_BUFFER_CONSTRUCTOR = null;
		} else {
			Constructor<?> directBufferConstructor;
			long address = -1;
			try {
				final Object maybeDirectBufferConstructor = AccessController.doPrivileged(new PrivilegedAction<Object>() {
					public Object run() {
						// TODO Auto-generated method stub
						try {
							final Constructor constructor = direct.getClass().getDeclaredConstructor(long.class, int.class);
							constructor.setAccessible(true);
							return constructor;
						} catch (NoSuchMethodException e) {
							return e;
						} catch (SecurityException e) {
							return e;
						}
					}
				});

				if (maybeDirectBufferConstructor instanceof Constructor<?>) {
					address = UNSAFE.allocateMemory(1);
					// try to use the constructor now
					try {
						((Constructor) maybeDirectBufferConstructor).newInstance(address, 1);
						directBufferConstructor = (Constructor<?>) maybeDirectBufferConstructor;
						logger.debug("direct buffer constructor: available");
					} catch (InstantiationException e) {
						directBufferConstructor = null;
					} catch (IllegalAccessException e) {
						directBufferConstructor = null;
					} catch (InvocationTargetException e) {
						directBufferConstructor = null;
					}
				} else {
					logger.debug("direct buffer constructor: unavailable", (Exception) maybeDirectBufferConstructor);
					directBufferConstructor = null;
				}
			} finally {
				if (address != -1) {
					UNSAFE.freeMemory(address);
				}
			}
			DIRECT_BUFFER_CONSTRUCTOR = directBufferConstructor;

			ADDRESS_FIELD_OFFSET = objectFieldOffset(addressField);
			BYTE_ARRAY_BASE_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
			boolean unaligned;
			Object maybeUnaligned = AccessController.doPrivileged(new PrivilegedAction<Object>() {
				public Object run() {
					try {
						Class<?> bitsClass = Class.forName("java.nio.Bits", false, PlatformDependent.getSystemClassLoader());
						Method unalignedMethod = bitsClass.getDeclaredMethod("unaligned");
						unalignedMethod.setAccessible(true);
						return unalignedMethod.invoke(null);
					} catch (Throwable cause) {
						return cause;
					}
				}
			});

			if (maybeUnaligned instanceof Boolean) {
				unaligned = (Boolean) maybeUnaligned;
				logger.debug("java.nio.Bits.unaligned: available, {}", unaligned);
			} else {
				String arch = SystemPropertyUtil.getJavaSystemPropertyString("os.arch", "");
				// noinspection DynamicRegexReplaceableByCompiledPattern
				unaligned = arch.matches("^(i[3-6]86|x86(_64)?|x64|amd64)$");
				Throwable t = (Throwable) maybeUnaligned;
				logger.debug("java.nio.Bits.unaligned: unavailable {}", unaligned, t);
			}

			UNALIGNED = unaligned;
		}

		logger.debug("java.nio.DirectByteBuffer.<init>(long, int): {}", DIRECT_BUFFER_CONSTRUCTOR != null ? "available" : "unavailable");

		if (direct != null) {
			freeDirectBuffer(direct);
		}
	}

	static ClassLoader getSystemClassLoader() {
		if (System.getSecurityManager() == null) {
			return ClassLoader.getSystemClassLoader();
		} else {
			return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
				public ClassLoader run() {
					// TODO Auto-generated method stub
					return ClassLoader.getSystemClassLoader();
				}
			});
		}
	}

	/**
	 * Unsafe 是否为null 为null返回false 不为null返回true
	 * 
	 * @return
	 */
	static boolean hasUnsafe() {
		if (UNSAFE == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 
	 * @param object
	 * @param fieldOffset
	 * @return
	 */
	static Object getObject(Object object, long fieldOffset) {
		return UNSAFE.getObject(object, fieldOffset);
	}

	/**
	 * 
	 * @param field
	 * @return
	 */
	static long objectFieldOffset(Field field) {
		return UNSAFE.objectFieldOffset(field);
	}

	static boolean isUnaligned() {
		return UNALIGNED;
	}

	static boolean unalignedAccess() {
		return UNALIGNED;
	}

	static void throwException(Throwable cause) {
		// JVM has been observed to crash when passing a null argument. See
		// https://github.com/netty/netty/issues/4131.
		UNSAFE.throwException(checkNotNull(cause, "cause"));
	}

	static boolean hasDirectBufferNoCleanerConstructor() {
		return DIRECT_BUFFER_CONSTRUCTOR != null;
	}

	static ByteBuffer reallocateDirectNoCleaner(ByteBuffer buffer, int capacity) {
		return newDirectBuffer(UNSAFE.reallocateMemory(di, arg1));
	}

	static ByteBuffer allocateDirectNoCleaner(int capacity) {

	}

	/**
	 * 返回直接内存的ByteBuffer
	 * 
	 * @param address
	 * @param capacity
	 * @return
	 */
	static ByteBuffer newDirectBuffer(long address, int capacity) {
		ObjectUtil.checkPositiveOrzero(address, "address");
		ObjectUtil.checkPositiveOrZero(capacity, "capacity");

		try {
			return (ByteBuffer) DIRECT_BUFFER_CONSTRUCTOR.newInstance(address, capacity);
		} catch (Throwable cause) {
			// TODO: handle exception
			if (cause instanceof Error) {
				throw (Error) cause;
			}
			throw new Error(cause);
		}
	}

	static void freeDirectBuffer(ByteBuffer buffer) {
		// Delegate to other class to not break on android
		// See https://github.com/netty/netty/issues/2604
		Cleaner0.freeDirectBuffer(buffer);
	}
}
