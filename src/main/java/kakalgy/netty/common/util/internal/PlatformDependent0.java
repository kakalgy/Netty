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

import static kakalgy.netty.common.util.internal.ObjectUtil.checkNotNull;

/**
 * The {@link PlatformDependent} operations which requires access to
 * {@code sun.misc.*}.
 */
@SuppressWarnings("restriction")
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
		return newDirectBuffer(UNSAFE.reallocateMemory(directBufferAddress(buffer), capacity), capacity);
	}

	@SuppressWarnings("restriction")
	static ByteBuffer allocateDirectNoCleaner(int capacity) {
		return newDirectBuffer(UNSAFE.allocateMemory(capacity), capacity);
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

	static long directBufferAddress(ByteBuffer buffer) {
		return getLong(buffer, ADDRESS_FIELD_OFFSET);
	}

	static long byteArrayBaseOffset() {
		return BYTE_ARRAY_BASE_OFFSET;
	}

	static Object getObjectVolatile(Object object, long fieldOffset) {
		return UNSAFE.getObjectVolatile(object, fieldOffset);
	}

	/**
	 * 
	 * @param object
	 * @param address
	 * @param value
	 */
	static void putOrderedObject(Object object, long address, Object value) {
		UNSAFE.putOrderedObject(object, address, value);
	}

	static int getInt(Object object, long fieldOffset) {
		return UNSAFE.getInt(object, fieldOffset);
	}

	static int getInt(long address) {
		return UNSAFE.getInt(address);
	}

	static int getInt(byte[] data, int index) {
		return UNSAFE.getInt(data, BYTE_ARRAY_BASE_OFFSET + index);
	}

	static void putInt(long address, int value) {
		UNSAFE.putInt(address, value);
	}

	static void putInt(byte[] data, int index, int value) {
		UNSAFE.putInt(data, BYTE_ARRAY_BASE_OFFSET + index, value);
	}

	private static long getLong(Object object, long fieldOffset) {
		return UNSAFE.getLong(object, fieldOffset);
	}

	static long getLong(long address) {
		return UNSAFE.getLong(address);
	}

	static long getLong(byte[] data, int index) {
		return UNSAFE.getLong(data, BYTE_ARRAY_BASE_OFFSET + index);
	}

	static void putLong(long address, long value) {
		UNSAFE.putLong(address, value);
	}

	static void putLong(byte[] data, int index, int value) {
		UNSAFE.putLong(data, BYTE_ARRAY_BASE_OFFSET + index, value);
	}

	static byte getByte(long address) {
		return UNSAFE.getByte(address);
	}

	static byte getByte(byte[] data, int index) {
		return UNSAFE.getByte(data, BYTE_ARRAY_BASE_OFFSET + index);
	}

	@SuppressWarnings("restriction")
	static void putByte(long address, byte value) {
		UNSAFE.putByte(address, value);
	}

	static void putByte(byte[] data, int index, byte value) {
		UNSAFE.putByte(data, BYTE_ARRAY_BASE_OFFSET + index, value);
	}

	static short getShort(long address) {
		return UNSAFE.getShort(address);
	}

	static short getShort(byte[] data, int index) {
		return UNSAFE.getShort(data, BYTE_ARRAY_BASE_OFFSET + index);
	}

	static void putShort(long address, short value) {
		UNSAFE.putShort(address, value);
	}

	static void putShort(byte[] data, int index, short value) {
		UNSAFE.putShort(data, BYTE_ARRAY_BASE_OFFSET + index, value);
	}

	/**
	 * 将长度为length的内存地址从srcAddress复制到destAddr
	 * 
	 * @param srcAddr
	 * @param destAddr
	 * @param length
	 */
	static void copyMemory(long srcAddr, long destAddr, long length) {
		// UNSAFE.copyMemory(srcAddr, dstAddr, length);
		while (length > 0) {
			long size = Math.min(length, UNSAFE_COPY_THRESHOLD);
			UNSAFE.copyMemory(srcAddr, destAddr, size);
			length -= size;
			srcAddr += size;
			destAddr += size;
		}
	}

	/**
	 * 
	 * @param src
	 * @param srcOffset
	 * @param dest
	 * @param destOffset
	 * @param length
	 */
	@SuppressWarnings("restriction")
	static void copyMemory(Object src, long srcOffset, Object dest, long destOffset, long length) {
		while (length > 0) {
			long size = Math.min(length, UNSAFE_COPY_THRESHOLD);
			UNSAFE.copyMemory(src, srcOffset, dest, destOffset, size);
			length -= size;
			srcOffset += size;
			destOffset += size;
		}
	}

	/**
	 * 
	 * @param address
	 * @param bytes
	 * @param value
	 */
	@SuppressWarnings("restriction")
	static void setMemory(long address, long bytes, byte value) {
		UNSAFE.setMemory(address, bytes, value);
	}

	/**
	 * 
	 * @param o
	 * @param offset
	 * @param bytes
	 * @param value
	 */
	@SuppressWarnings("restriction")
	static void setMemory(Object o, long offset, long bytes, byte value) {
		UNSAFE.setMemory(o, offset, bytes, value);
	}

	/**
	 * 
	 * @param bytes1
	 * @param startPos1
	 * @param bytes2
	 * @param startPos2
	 * @param length
	 * @return
	 */
	@SuppressWarnings("restriction")
	static boolean equals(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
		if (length == 0) {
			return true;
		}

		final long baseOffset1 = BYTE_ARRAY_BASE_OFFSET + startPos1;
		final long baseOffset2 = BYTE_ARRAY_BASE_OFFSET + startPos2;
		// 按位与运算符"&"是双目运算符。其功能是参与运算的两数各对应的二进位相与。只有对应的两个二进位均为1时，结果位才为1，否则为0。参与运算的数以补码方式出现。
		// 例如：9&5可写算式如下：
		// 00001001 (9的二进制补码)
		// &00000101 (5的二进制补码)
		// 00000001 (1的二进制补码)
		// 可见9&5=1。
		// 按位与运算通常用来对某些位清0或保留某些位。例如把a 的高八位清 0 ，保留低八位，可作a&255运算（255
		// 的二进制数为0000000011111111）。
		int remainingBytes = length & 7;// 保留低三位
		final long end = baseOffset1 + remainingBytes;

		for (long i = baseOffset1 - 8 + length, j = baseOffset2 - 8 + length; i >= end; i -= 8, j -= 8) {
			if (UNSAFE.getLong(bytes1, i) != UNSAFE.getLong(bytes2, j)) {
				return false;
			}
		}

		if (remainingBytes >= 4) {
			remainingBytes -= 4;
			if (UNSAFE.getInt(bytes1, baseOffset1 + remainingBytes) != UNSAFE.getInt(bytes2, baseOffset2 + remainingBytes)) {
				return false;
			}
		}

		if (remainingBytes >= 2) {
			return UNSAFE.getChar(bytes1, baseOffset1) == UNSAFE.getChar(bytes2, baseOffset2) && (remainingBytes == 2 || bytes1[startPos1 + 2] == bytes2[startPos2 + 2]);
		}
		return bytes1[startPos1] == bytes2[startPos2];
	}

	static int equalsConstantTime(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
		long result = 0;
		final long baseOffset1 = BYTE_ARRAY_BASE_OFFSET + startPos1;
		final long baseOffset2 = BYTE_ARRAY_BASE_OFFSET + startPos2;
		final int remainingBytes = length & 7;
		final long end = baseOffset1 + remainingBytes;
		for (long i = baseOffset1 - 8 + length, j = baseOffset2 - 8 + length; i >= end; i -= 8, j -= 8) {
			result |= UNSAFE.getLong(bytes1, i) ^ UNSAFE.getLong(bytes2, j);
		}
		switch (remainingBytes) {
		case 7:
			return ConstantTimeUtils.equalsConstantTime(result | (UNSAFE.getInt(bytes1, baseOffset1 + 3) ^ UNSAFE.getInt(bytes2, baseOffset2 + 3))
					| (UNSAFE.getChar(bytes1, baseOffset1 + 1) ^ UNSAFE.getChar(bytes2, baseOffset2 + 1)) | (UNSAFE.getByte(bytes1, baseOffset1) ^ UNSAFE.getByte(bytes2, baseOffset2)), 0);
		case 6:
			return ConstantTimeUtils.equalsConstantTime(
					result | (UNSAFE.getInt(bytes1, baseOffset1 + 2) ^ UNSAFE.getInt(bytes2, baseOffset2 + 2)) | (UNSAFE.getChar(bytes1, baseOffset1) ^ UNSAFE.getChar(bytes2, baseOffset2)), 0);
		case 5:
			return ConstantTimeUtils.equalsConstantTime(
					result | (UNSAFE.getInt(bytes1, baseOffset1 + 1) ^ UNSAFE.getInt(bytes2, baseOffset2 + 1)) | (UNSAFE.getByte(bytes1, baseOffset1) ^ UNSAFE.getByte(bytes2, baseOffset2)), 0);
		case 4:
			return ConstantTimeUtils.equalsConstantTime(result | (UNSAFE.getInt(bytes1, baseOffset1) ^ UNSAFE.getInt(bytes2, baseOffset2)), 0);
		case 3:
			return ConstantTimeUtils.equalsConstantTime(
					result | (UNSAFE.getChar(bytes1, baseOffset1 + 1) ^ UNSAFE.getChar(bytes2, baseOffset2 + 1)) | (UNSAFE.getByte(bytes1, baseOffset1) ^ UNSAFE.getByte(bytes2, baseOffset2)), 0);
		case 2:
			return ConstantTimeUtils.equalsConstantTime(result | (UNSAFE.getChar(bytes1, baseOffset1) ^ UNSAFE.getChar(bytes2, baseOffset2)), 0);
		case 1:
			return ConstantTimeUtils.equalsConstantTime(result | (UNSAFE.getByte(bytes1, baseOffset1) ^ UNSAFE.getByte(bytes2, baseOffset2)), 0);
		default:
			return ConstantTimeUtils.equalsConstantTime(result, 0);
		}
	}

	static int hashCodeAscii(byte[] bytes, int startPos, int length) {
		int hash = HASH_CODE_ASCII_SEED;
		final long baseOffset = BYTE_ARRAY_BASE_OFFSET + startPos;
		final int remainingBytes = length & 7;
		final long end = baseOffset + remainingBytes;
		for (long i = baseOffset - 8 + length; i >= end; i -= 8) {
			hash = hashCodeAsciiCompute(UNSAFE.getLong(bytes, i), hash);
		}
		switch (remainingBytes) {
		case 7:
			return ((hash * HASH_CODE_C1 + hashCodeAsciiSanitize(UNSAFE.getByte(bytes, baseOffset))) * HASH_CODE_C2 + hashCodeAsciiSanitize(UNSAFE.getShort(bytes, baseOffset + 1))) * HASH_CODE_C1
					+ hashCodeAsciiSanitize(UNSAFE.getInt(bytes, baseOffset + 3));
		case 6:
			return (hash * HASH_CODE_C1 + hashCodeAsciiSanitize(UNSAFE.getShort(bytes, baseOffset))) * HASH_CODE_C2 + hashCodeAsciiSanitize(UNSAFE.getInt(bytes, baseOffset + 2));
		case 5:
			return (hash * HASH_CODE_C1 + hashCodeAsciiSanitize(UNSAFE.getByte(bytes, baseOffset))) * HASH_CODE_C2 + hashCodeAsciiSanitize(UNSAFE.getInt(bytes, baseOffset + 1));
		case 4:
			return hash * HASH_CODE_C1 + hashCodeAsciiSanitize(UNSAFE.getInt(bytes, baseOffset));
		case 3:
			return (hash * HASH_CODE_C1 + hashCodeAsciiSanitize(UNSAFE.getByte(bytes, baseOffset))) * HASH_CODE_C2 + hashCodeAsciiSanitize(UNSAFE.getShort(bytes, baseOffset + 1));
		case 2:
			return hash * HASH_CODE_C1 + hashCodeAsciiSanitize(UNSAFE.getShort(bytes, baseOffset));
		case 1:
			return hash * HASH_CODE_C1 + hashCodeAsciiSanitize(UNSAFE.getByte(bytes, baseOffset));
		default:
			return hash;
		}
	}

	static int hashCodeAsciiCompute(long value, int hash) {
		// masking with 0x1f reduces the number of overall bits that impact the
		// hash code but makes the hash
		// code the same regardless of character case (upper case or lower case
		// hash is the same).
		return hash * HASH_CODE_C1 +
		// Low order int
				hashCodeAsciiSanitize((int) value) * HASH_CODE_C2 +
				// High order int
				(int) ((value & 0x1f1f1f1f00000000L) >>> 32);
	}

	static int hashCodeAsciiSanitize(int value) {
		return value & 0x1f1f1f1f;
	}

	static int hashCodeAsciiSanitize(short value) {
		return value & 0x1f1f;
	}

	static int hashCodeAsciiSanitize(byte value) {
		return value & 0x1f;
	}

	static ClassLoader getClassLoader(final Class<?> clazz) {
		if (System.getSecurityManager() == null) {
			return clazz.getClassLoader();
		} else {
			return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
				public ClassLoader run() {
					return clazz.getClassLoader();
				}
			});
		}
	}

	static ClassLoader getContextClassLoader() {
		if (System.getSecurityManager() == null) {
			return Thread.currentThread().getContextClassLoader();
		} else {
			return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
				public ClassLoader run() {
					return Thread.currentThread().getContextClassLoader();
				}
			});
		}
	}

	@SuppressWarnings("restriction")
	static int addressSize() {
		return UNSAFE.addressSize();
	}

	@SuppressWarnings("restriction")
	static long allocateMemory(long size) {
		return UNSAFE.allocateMemory(size);
	}

	@SuppressWarnings("restriction")
	static void freeMemory(long address) {
		UNSAFE.freeMemory(address);
	}
}
