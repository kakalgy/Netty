package kakalgy.netty.example.worldclock;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * Sends a list of continent/city pairs to a {@link WorldClockServer} to get the
 * local times of the specified cities.
 *
 * @author Administrator
 *
 */
public final class WorldClockClient {

	static final boolean SSL = System.getProperty("ssl") != null;
	static final String HOST = System.getProperty("host", "127.0.0.1");
	static final int PORT = Integer.parseInt(System.getProperty("port", "8463"));
	static final List<String> CITIES = Arrays.asList(System.getProperty("cities", "Asia/Seoul,Europe/Berlin,America/Los_Angeles").split(","));

	public static void main(String[] args) throws Exception {
		// Configure SSL.
		final SslContext sslCtx;
	}

}
