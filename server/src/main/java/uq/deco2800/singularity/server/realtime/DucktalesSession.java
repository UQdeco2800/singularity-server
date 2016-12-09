package uq.deco2800.singularity.server.realtime;

import uq.deco2800.singularity.common.SessionType;
import uq.deco2800.singularity.common.representations.realtime.RealTimeSessionConfiguration;

public class DucktalesSession extends RealTimeService {

	public DucktalesSession(RealTimeSessionConfiguration configuration) {
		super(configuration);
		if (configuration.getSession() != SessionType.COASTER) {
			throw new IllegalArgumentException("The configuration should be Ducktales configuration");
		}
	}

	// TODO: add game listeners to respond to events
}
