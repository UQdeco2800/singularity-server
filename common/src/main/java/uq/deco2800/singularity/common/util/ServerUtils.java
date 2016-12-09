package uq.deco2800.singularity.common.util;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerUtils {

	public static int getAvailablePort() {
		int port = -1;
		try {
			ServerSocket server = new ServerSocket(0);
			port = server.getLocalPort();
			server.close();
		} catch (IOException exception) {
			port = -1;
		}
		return port;
	}

}
