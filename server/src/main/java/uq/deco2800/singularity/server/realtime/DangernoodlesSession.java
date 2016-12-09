package uq.deco2800.singularity.server.realtime;

import uq.deco2800.singularity.common.SessionType;
import uq.deco2800.singularity.common.representations.realtime.RealTimeSessionConfiguration;
import uq.deco2800.singularity.server.dangernoodle.DangernoodleRealTimeServerListener;

public class DangernoodlesSession extends RealTimeService {

    public DangernoodlesSession(RealTimeSessionConfiguration configuration) {
        super(configuration);
        if (configuration.getSession() != SessionType.DANGER_NOODLES) {
            throw new IllegalArgumentException("The configuration should be Dangernoodles configuration");
        }
    }

    @Override
    public boolean start() {
        boolean success = super.start();
        server.addListener(new DangernoodleRealTimeServerListener());
        return success;
    }
}
