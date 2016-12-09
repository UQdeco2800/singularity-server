package uq.deco2800.singularity.clients.duxcom;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uq.deco2800.singularity.common.representations.duxcom.gamestate.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Listens for changes on gameplay.
 *
 * Created by liamdm on 9/10/2016.
 */
public class GameplayListener extends Listener {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameplayListener.class);

    private List<GameplayEventListener> listeners;

    @Override
    public void received(Connection connection, Object object) {
        if(!(object instanceof AbstractGameStateMessage)){
            LOGGER.info("Received unexpected object: [{}]", object);
            return;
        }

        AbstractGameStateMessage gsm = (AbstractGameStateMessage)object;

        LOGGER.info("Recieved message type [{}]... Delegating to [{}].", ((AbstractGameStateMessage) object).getMessageType(), listeners);
        for(GameplayEventListener eventListener : listeners){
            switch(gsm.getMessageType()){
                case GAME_METADATA_QUERY:
                    LOGGER.info("Notified listener of metadata message [{}]", ((GameMetadata) object).getInnerMessageType());
                    eventListener.recievedGameMetadataQuery((GameMetadata) gsm);
                    break;
                case GAME_REGISTRATION_MESSAGE:
                    LOGGER.info("Notified listener of registration message [{}]", ((GameRegistration) object).getInnerMessageType());
                    eventListener.recievedGameRegistrationMessage((GameRegistration) gsm);
                    break;
                case CONTROL_MESSAGE:
                    LOGGER.info("Notified listener of control message [{}]", ((ControlMessage) object).getInnerMessageType());
                    eventListener.recievedControlMessage((ControlMessage) gsm);
                    break;
                case STATE_CHANGE:
                    LOGGER.info("Notified listener of state change [{}]", ((StateChange) object).getState());
                    eventListener.recievedStateMessage((StateChange) gsm);
                    break;
                case GAME_UPDATE:
                    LOGGER.info("Notified listener of game update [{}]", ((GameUpdate) object).getInnerMessageType());
                    eventListener.recievedUpdateMessage((GameUpdate) gsm);
                    break;
                case PLAYER_ACTION:
                    LOGGER.info("Notified listener of player action [{}]", ((PlayerAction) object).getInnerMessageType());
                    eventListener.recievedPlayerActionMessage((PlayerAction) gsm);
                    break;
                case SQUAD_STATE:
                    LOGGER.info("Notified listener of squad state change [{}]", ((SquadState)object).getInnerMessageType());
                    eventListener.recievedSquadStateMessage((SquadState) gsm);
                    break;
                default:
                    LOGGER.warn("Recieved unexpected message type [{}]!", gsm.getMessageType());
            }
        }
    }

    /**
     * @param listener
     */
    public void addListener(GameplayEventListener listener) {
        listeners.add(listener);
    }

    /**
     * @param listener
     */
    public void removeListener(GameplayEventListener listener) {
        listeners.remove(listener);
    }

    /**
     *
     */
    public GameplayListener() {
        listeners = new LinkedList<>();
    }

    @Override
    public void connected(Connection connection) {
        super.connected(connection);
    }

    @Override
    public void disconnected(Connection connection) {
        super.disconnected(connection);
    }

    @Override
    public void idle(Connection connection) {
        super.idle(connection);
    }
}
