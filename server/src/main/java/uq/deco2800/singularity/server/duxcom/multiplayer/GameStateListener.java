package uq.deco2800.singularity.server.duxcom.multiplayer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import uq.deco2800.singularity.common.representations.duxcom.gamestate.AbstractGameStateMessage;
import uq.deco2800.singularity.common.representations.duxcom.gamestate.StateChange;

import java.util.LinkedList;

/**
 * Game state listener.
 *
 * Created by liamdm on 9/10/2016.
 */
public class GameStateListener extends Listener {
    private LinkedList<Connection> connections = new LinkedList<Connection>();
    private DistributedGameManager manager = new DistributedGameManager();

    @Override
    public void connected(Connection connection) {
        super.connected(connection);
        connections.add(connection);
    }

    @Override
    public void disconnected(Connection connection) {
        super.disconnected(connection);

        connections.remove(connection);

        try {
            manager.getSession().leave(connection);
        } catch(AdminLossException ex){

            // tell everyone to be gone
            for (Connection existingConnection : connections) {
                existingConnection.sendTCP(StateChange.adminDitched());
            }
        }
    }

    @Override
    public void received(Connection incoming, Object object) {
        super.received(incoming, object);

        if(!(object instanceof AbstractGameStateMessage)){
            return;
        }

        AbstractGameStateMessage agsm = (AbstractGameStateMessage) object;
        if(manager.handle(agsm, incoming)) {
            for (Connection connection : connections) {
                connection.sendTCP(object);
            }
        }
    }

    @Override
    public void idle(Connection connection) {
        super.idle(connection);
    }
}
