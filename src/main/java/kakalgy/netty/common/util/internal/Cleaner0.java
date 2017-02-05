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

import java.nio.ByteBuffer;

/**
 * Allows to free direct {@link ByteBuffer} by using Cleaner. This is
 * encapsulated in an extra class to be able to use {@link PlatformDependent0}
 * on Android without problems.
 *
 * For more details see
 * <a href="https://github.com/netty/netty/issues/2604">#2604</a>.
 */
public class Cleaner0 {

	
}
