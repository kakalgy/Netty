/*
* Copyright 2014 The Netty Project
*
* The Netty Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
package kakalgy.netty.common.util.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import kakalgy.netty.common.util.internal.logging.InternalLogger;
import kakalgy.netty.common.util.internal.logging.InternalLoggerFactory;

/**
 * Allows to free direct {@link ByteBuffer} by using Cleaner. This is
 * encapsulated in an extra class to be able to use {@link PlatformDependent0}
 * on Android without problems.
 *
 * For more details see
 * <a href="https://github.com/netty/netty/issues/2604">#2604</a>.
 */
public class Cleaner0 {

	private static final long CLEANER_FIELD_OFFSET;
	private static final Method CLEAN_METHOD;
	private static final boolean CLEANER_IS_RUNNABLE;

	private static final InternalLogger logger = InternalLoggerFactory.getInstance(Cleaner0.class);

	static {
		ByteBuffer direct = ByteBuffer.allocate(1);
		Field cleanerField;
		long fieldOffset = -1;
		Method clean = null;
		boolean cleanerIsRunnable = false;

		if (PlatformDependent0.hasUnsafe()) {
			try {
				// 需要看一下java的反射机制
				cleanerField = direct.getClass().getDeclaredField("cleaner");
				cleanerField.setAccessible(true);
				fieldOffset = PlatformDependent0.objectFieldOffset(cleanerField);
				Object cleaner = cleanerField.get(direct);
				try {
					// Cleaner implements Runnable from JDK9 onwards.
					Runnable runnable = (Runnable) cleaner;
					runnable.run();
					cleanerIsRunnable = true;
				} catch (ClassCastException e) {
					// TODO: handle exception
					clean = cleaner.getClass().getDeclaredMethod("clean");
					clean.invoke(cleaner);
				}
			} catch (Throwable t) {
				// TODO: handle exception
				// We don't have ByteBuffer.cleaner().
				fieldOffset = -1;
				clean = null;
				cleanerIsRunnable = false;
			}
		}

		logger.debug("java.nio.ByteBuffer.cleaner(): {}", fieldOffset != -1 ? "available" : "unavailable");

		CLEANER_FIELD_OFFSET = fieldOffset;
		CLEAN_METHOD = clean;
		CLEANER_IS_RUNNABLE = cleanerIsRunnable;

		// free buffer if possible
		freeDirectBuffer(direct);
	}

	/**
	 * 释放直接缓冲区
	 * 
	 * @param buffer
	 */
	static void freeDirectBuffer(ByteBuffer buffer) {
		// 当CLEANER_FIELD_OFFSET=-1或buffer不是Direct时
		if (CLEANER_FIELD_OFFSET == -1 || !buffer.isDirect()) {
			return;
		}

		assert CLEAN_METHOD != null || CLEANER_IS_RUNNABLE : "CLEANER_FIELD_OFFSET != -1 implies CLEAN_METHOD != null or CLEANER_IS_RUNNABLE == true";

		try {
			Object cleaner = PlatformDependent0.getObject(buffer, CLEANER_FIELD_OFFSET);
			if (cleaner != null) {
				if (CLEANER_IS_RUNNABLE) {
					((Runnable) cleaner).run();
				} else {
					CLEAN_METHOD.invoke(cleaner);
				}
			}
		} catch (Throwable t) {
			// TODO: handle exception
			// Nothing we can do here.
		}
	}

	/**
	 * 构造函数
	 */
	private Cleaner0() {

	}
}
