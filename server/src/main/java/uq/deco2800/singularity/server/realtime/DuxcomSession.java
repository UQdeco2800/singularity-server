package uq.deco2800.singularity.server.realtime;

import uq.deco2800.singularity.common.SessionType;
import uq.deco2800.singularity.common.representations.realtime.RealTimeSessionConfiguration;
import uq.deco2800.singularity.server.duxcom.multiplayer.GameStateListener;

public class DuxcomSession extends RealTimeService {

	public DuxcomSession(RealTimeSessionConfiguration configuration) {
		super(configuration);
		if (configuration.getSession() != SessionType.COASTER) {
			throw new IllegalArgumentException("The configuration should be Duxcom configuration");
		}
	}

	@Override
	public boolean start() {
		boolean success = super.start();
		server.addListener(new GameStateListener());
		return success;
	}
}
