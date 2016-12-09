package uq.deco2800.singularity.clients.dangernoodle;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * Created by khoi_truong on 2016/10/17.
 * <p>
 * This class is used to handle all game state events that were sent to this
 * client.
 */
public class DangernoodleGameStateListener extends Listener {



    @Override
    public void received(Connection serverConnection, Object serverResponse) {
        super.received(serverConnection, serverResponse);

    }

}
