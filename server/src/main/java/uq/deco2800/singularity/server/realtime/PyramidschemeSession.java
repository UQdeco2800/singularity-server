package uq.deco2800.singularity.server.realtime;

import uq.deco2800.singularity.common.SessionType;
import uq.deco2800.singularity.common.representations.realtime.RealTimeSessionConfiguration;
import uq.deco2800.singularity.server.pyramidscheme.MultiplayerListener;

public class PyramidschemeSession extends RealTimeService {

	public PyramidschemeSession(RealTimeSessionConfiguration configuration) {
		super(configuration);
		if (configuration.getSession() != SessionType.COASTER) {
			throw new IllegalArgumentException("The configuration should be Pyramidscheme configuration");
		}
	}

	@Override
	public boolean start() {
		boolean success = super.start();
		server.addListener(new MultiplayerListener());
		return success;
	}
}
