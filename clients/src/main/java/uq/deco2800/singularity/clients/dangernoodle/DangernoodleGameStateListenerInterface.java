package uq.deco2800.singularity.clients.dangernoodle;

import java.util.EventListener;

/**
 * Created by khoi_truong on 2016/10/17.
 * <p>
 * This class serves as a contract between clients and the server. More
 * specifically, it is used to broadcast game state update to all clients.
 */
public abstract class DangernoodleGameStateListenerInterface implements EventListener {
    public abstract void receivedGameStateUpdate();

    /**
     * This class is used to keep a record of what is inclunded inside the
     * update message that is just received from the server.
     */
    public class GameStateRecord {

    }
}
