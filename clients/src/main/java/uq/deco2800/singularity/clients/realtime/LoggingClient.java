/**
 * 
 */
package uq.deco2800.singularity.clients.realtime;

import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import uq.deco2800.singularity.common.SessionType;
import uq.deco2800.singularity.common.representations.realtime.RealTimeSessionConfiguration;

/**
 * @author dloetscher
 *		
 */
public class LoggingClient extends RealTimeClient {
	
	private static boolean shouldQuit = false;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		RealTimeSessionConfiguration configuration = new RealTimeSessionConfiguration();
		configuration.setPort(8090);
		configuration.setSession(SessionType.LOGGING);
		LoggingClient client = new LoggingClient(configuration);
		client.addListener(new ClientListener());
		while (!shouldQuit) {
			client.update();
			Thread.sleep(1000);
		}
		
		System.out.println("Server disconnected. Quitting...");
	}
	
	private static class ClientListener extends Listener {
		
		/* (non-Javadoc)
		 * @see com.esotericsoftware.kryonet.Listener#received(com.esotericsoftware.kryonet.Connection, java.lang.Object)
		 */
		@Override
		public void received(Connection connection, Object object) {
			if (object instanceof String) {
				String message = (String) object;
				System.out.println(message);
			}
		}
		
		/* (non-Javadoc)
		 * @see com.esotericsoftware.kryonet.Listener#disconnected(com.esotericsoftware.kryonet.Connection)
		 */
		@Override
		public void disconnected(Connection connection) {
			super.disconnected(connection);
			shouldQuit = true;
		}
	}
	
	/**
	 * @param configuration
	 * @param restClient
	 * @throws IOException
	 */
	public LoggingClient(RealTimeSessionConfiguration configuration) throws IOException {
		super(configuration, null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see uq.deco2800.singularity.clients.realtime.RealTimeClient#register()
	 */
	@Override
	public void register() {
		// no-op
	}

	/* (non-Javadoc)
	 * @see uq.deco2800.singularity.clients.realtime.RealTimeClient#getSessionType()
	 */
	@Override
	public SessionType getSessionType() {
		// TODO Auto-generated method stub
		return SessionType.LOGGING;
	}
	
	public void addListener(Listener listener) {
		realTimeClient.addListener(listener);
	}
	
	public void update() {
		realTimeClient.sendTCP("Hi");
	}
	
}
