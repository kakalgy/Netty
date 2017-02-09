package kakalgy.netty.common.util;

/**
 * 返回一个使人可以容易阅读的信息来追踪泄露的地方 </br></br> A hint object that provides human-readable
 * message for easier resource leak tracking.
 */
public interface ResourceLeakHint {
	/**
	 * 返回一个使人可以容易阅读的信息来追踪泄露的地方 </br></br> Returns a human-readable message that
	 * potentially enables easier resource leak tracking.
	 */
	String toHintString();
}
