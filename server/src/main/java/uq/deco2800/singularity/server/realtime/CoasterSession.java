package uq.deco2800.singularity.server.realtime;

import uq.deco2800.singularity.common.SessionType;
import uq.deco2800.singularity.common.representations.realtime.RealTimeSessionConfiguration;
import uq.deco2800.singularity.common.util.KryoUtils;
import uq.deco2800.singularity.server.coaster.CoasterServerGameStateListener;

public class CoasterSession extends RealTimeService {

	public CoasterSession(RealTimeSessionConfiguration configuration) {
		super(configuration);
		if (configuration.getSession() != SessionType.COASTER) {
			throw new IllegalArgumentException("The configuration should be Coaster configuration");
		}
	}

	@Override
	public boolean start() {
		boolean success = super.start();
		server.addListener(new CoasterServerGameStateListener(this));
		KryoUtils.registerCommonClasses(server.getKryo());
		return success;
	}
}
