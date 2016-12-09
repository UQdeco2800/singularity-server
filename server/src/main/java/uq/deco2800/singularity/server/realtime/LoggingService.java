/**
 * 
 */
package uq.deco2800.singularity.server.realtime;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Semaphore;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;
import com.esotericsoftware.kryonet.Listener;

import uq.deco2800.singularity.common.representations.realtime.RealTimeSessionConfiguration;

/**
 * @author dloetscher
 * 		
 */
public class LoggingService extends RealTimeService {
	
	/**
	 * @param configuration
	 */
	public LoggingService(RealTimeSessionConfiguration configuration) {
		super(configuration);
		this.server.addListener(new LoggingListener());
		
	}
	
	private class LoggingListener extends Listener {
		
		private String CLASS = LoggingListener.class.getName();
		private Logger LOGGER = LoggerFactory.getLogger(CLASS);
		
		private ConcurrentHashSet<Connection> connections;
		private BufferedReader fileReader = null;
		private Semaphore semaphore = new Semaphore(0);
		
		/**
		 * 
		 */
		public LoggingListener() {
			super();
			connections = new ConcurrentHashSet<Connection>();
			setupFileStream();
		}
		
		private void setupFileStream() {
			try {
				FileInputStream stream = new FileInputStream("server.log");
				fileReader = new BufferedReader(new InputStreamReader(stream));
				while (fileReader.readLine() != null); // empty out buffer. 
			} catch (IOException exception) {
				LOGGER.warn("Could not open server.log", exception);
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see com.esotericsoftware.kryonet.Listener#connected(com.esotericsoftware.kryonet.Connection)
		 */
		@Override
		public void connected(Connection connection) {
			super.connected(connection);
			connections.add(connection);
		}
		
		/*
		 * (non-Javadoc)
		 * @see com.esotericsoftware.kryonet.Listener#disconnected(com.esotericsoftware.kryonet.Connection)
		 */
		@Override
		public void disconnected(Connection connection) {
			super.disconnected(connection);
			connections.remove(connection);
		}
		
		/*
		 * (non-Javadoc)
		 * @see com.esotericsoftware.kryonet.Listener#received(com.esotericsoftware.kryonet.Connection,
		 * java.lang.Object)
		 */
		@Override
		public void received(Connection connection, Object object) {
			super.received(connection, object);
			if (object instanceof String) {
				LOGGER.info("Received a Keep alive from [{}]", connection);
				if (semaphore.tryAcquire()) {
					if (fileReader == null) {
						setupFileStream();
						if (fileReader == null) {
							// Still couldn't get file, so exit.
							semaphore.release();
							return;
						}
					}
					
					String line = getNextLine();
					while (line != null) {
						for (Connection conn : connections) {
							conn.sendTCP(line);
						}
						line = getNextLine();
					}
					
					semaphore.release();
				}
			}
			
		}
		
		private String getNextLine() {
			String line = null;
			try {
				line = fileReader.readLine();
			} catch (IOException exception) {
				LOGGER.warn("Error reading line out of log file", exception);
				try {
					fileReader.close();
				} catch (IOException closeException) {
					LOGGER.warn("Could not close file after read exception", closeException);
					fileReader = null;
				}
			}
			return line;
		}
		
	}
	
}
