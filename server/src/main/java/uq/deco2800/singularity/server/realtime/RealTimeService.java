package uq.deco2800.singularity.server.realtime;

import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uq.deco2800.singularity.common.representations.realtime.RealTimeSessionConfiguration;
import uq.deco2800.singularity.common.util.KryoUtils;

import java.io.IOException;

public class RealTimeService {

	private static final String CLASS = RealTimeService.class.getName();
	private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);
	protected final RealTimeSessionConfiguration configuration;
	protected Server server;

	protected boolean started;

	public RealTimeService(RealTimeSessionConfiguration configuration) {
		LOGGER.info("Initiating new RealTimeService with configuration: [{}]", configuration);
		if (!configuration.isValid()) {
			throw new IllegalArgumentException(
					String.format("Configuration: %s must be valid", configuration.toString()));
		}
		this.configuration = configuration;
		Log.set(Log.LEVEL_TRACE);
		server = new Server();
	}

	public boolean start() {
		if (!started) {
			LOGGER.info("Starting [{}]", configuration);
			KryoUtils.registerCommonClasses(server.getKryo());
			server.start();
			try {
				server.bind(configuration.getPort());
				started = true;
				LOGGER.info("Started [{}]", configuration);
			} catch (IOException e) {
				Log.error("Could not bind to port " + configuration.getPort(), e);
				started = false;
			}
		} else {
			LOGGER.debug("Already started. Skipping extra start()");
		}
		return started;
	}
	
	public void stop() {
		if (started) {
			LOGGER.info("Stopping [{}]", configuration);
			server.stop();
			LOGGER.info("Stopped [{}]", configuration);
		}
	}
	
	public RealTimeSessionConfiguration getConfiguration() {
		return configuration;
	}
	
	public boolean isStarted() {
		return started;
	}

}
