package uq.deco2800.singularity.server.duxcom.multiplayer;

import com.esotericsoftware.kryonet.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uq.deco2800.singularity.common.representations.duxcom.gamestate.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Controls distributing the game state to a host.
 * <p>
 * Created by liamdm on 11/10/2016.
 */
public class DistributedGameManager {
    /**
     * {@link Logger} from SL4J used to log at different levels.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedGameManager.class);

    /**
     * The game session for this distributed game
     */
    GameSession session = new GameSession();

    /**
     * Returns the current game session
     *
     * @return the game session
     */
    protected GameSession getSession() {
        return session;
    }

    /**
     * The number of out of time game sessions
     */
    private int outOfTimeSessions = 0;

    /**
     * Handles a game state message. Returns true if the game state message should
     * be forwarded to all other connecting clients.
     *
     * @param gameStateMessage
     * @return
     */
    public boolean handle(AbstractGameStateMessage gameStateMessage, Connection connection) {
        // session is no longer running
        if (session.isDitched()) {
            connection.sendTCP(StateChange.adminDitched());
            return false;
        }

        switch (gameStateMessage.getMessageType()) {
            case GAME_REGISTRATION_MESSAGE:
                // handle the registration of a new game
                GameRegistration gameRegistration = (GameRegistration) gameStateMessage;

                if (!gameRegistration.isRequest()) {
                    // cannot send a response to the server.
                    LOGGER.error("Tried to send response to server! Invalid!");
                    return false;
                }

                LOGGER.info("Join request by [{}]", gameRegistration.getUsername());

                boolean state = handleSessionRequest(gameRegistration, connection);
                List<String> joinedUsers = new LinkedList<String>(session.getJoinedUsers());
                outOfTimeSessions = joinedUsers.size();

                LOGGER.info("Broadcasting joined users [{}]", joinedUsers);
                broadcast(null, GameMetadata.userDataUpdate(joinedUsers));
                return state;

            case CONTROL_MESSAGE:
                // handle the registration of a new game
                ControlMessage controlMessage = (ControlMessage) gameStateMessage;

                // the game has loaded
                if(controlMessage.getInnerMessageType() == ControlMessage.MessageType.JIT_FLOW_PERMIT){
                    --outOfTimeSessions;
                    LOGGER.info("Got just in time notifier from [{}]... [{}] remaining!", controlMessage.getUsername(), outOfTimeSessions);

                    if(outOfTimeSessions == 0){
                        // notify of the JIT
                        broadcast(null, ControlMessage.gameManagerReady());
                    }

                    return false;
                }

                // handle control messages sent by an admin
                if (!session.isAdmin(connection)) {
                    // only admins can send
                    connection.sendTCP(ControlMessage.fail("You are not an admin, and only admins can send control messages!"));
                    return false;
                }

                return handleControlMessage(controlMessage, connection);
            case PLAYER_ACTION:

                PlayerAction playerAction = (PlayerAction) gameStateMessage;
                return handlePlayerAction(playerAction, connection);

        }

        return true;
    }

    /**
     * Handles a player action message
     * @param playerAction  the action of the player
     * @param connection    the connection of origin
     */
    private boolean handlePlayerAction(PlayerAction playerAction, Connection connection) {
        if(playerAction.getInnerMessageType() == PlayerAction.MessageType.PLAYER_MOVE){
            // try and tell everone about the move
            LOGGER.info("Broadcasting player move...");
            broadcast(null, playerAction);
        } else if(playerAction.getInnerMessageType() == PlayerAction.MessageType.END_TURN){
            // try and tell everyone about the turn end
            LOGGER.info("Broadcasting turn end...");
            broadcast(null, playerAction);
            broadcast(null, GameUpdate.notifyPlayerChange(session.nextPlayer()));
        } else {

            LOGGER.info("Unrecognised player action type: " + String.valueOf(playerAction.getInnerMessageType()));
        }
        return false;
    }

    /**
     * Handles an admin control message
     *
     * @param controlMessage the controll message to handle
     * @param connection     the connection sending it
     */
    private boolean handleControlMessage(ControlMessage controlMessage, Connection connection) {
        switch (controlMessage.getInnerMessageType()) {
            case SWITCH_LOBBY:
                // try and switch game to lobby mode
                LOGGER.debug("Switching to lobby game state...");
                if (session.getGameState() == GameState.UNINITIALISED) {
                    // set to lobby
                    session.openLobby();
                    broadcast(null, new StateChange(session.getGameState()));
                } else {
                    // game state was invalid
                    connection.sendTCP(ControlMessage.fail(String.format("Game must be UNINITIALISED when moving to lobby, actual state: %s", String.valueOf(session.getGameState()))));
                }
                return false;
            case START_GAME:
                // try and start the game
                String mapName = controlMessage.getMap();
                LOGGER.debug("Switching to map " + mapName + "...");
                if(session.getGameState() != GameState.LOBBY){
                    // cannot do game
                    connection.sendTCP(ControlMessage.fail("Cannot open a map when not in lobby state!"));
                    return false;
                }

                String gameIV = session.startGame(mapName);

                // broadcast the gamestate change
                broadcast(null, new StateChange(session.getGameState()));
                broadcast(null, GameUpdate.notifyPlayerOrder(session.getJoinedUsers()));

                // wait 3 seconds before game start
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // broadcast the game IV
                broadcast(null, GameUpdate.initMap(mapName, gameIV));
                broadcast(null, GameUpdate.notifyPlayerChange(session.getCurrentPlayerTurn()));
        }
        return false;
    }

    /**
     * Sends a message to everyone except the specified connection, unless this is null.
     * If the specified connection is null send to all.
     */
    private void broadcast(Connection origin, AbstractGameStateMessage message) {
        LOGGER.info(String.format("Broadcasting message type: %s", String.valueOf(message.getMessageType())));
        List<Connection> connections = session.getConnections();
        if (origin != null) {
            connections.remove(origin);
        }
        for(Connection connection : connections ){
            connection.sendTCP(message);
        }
    }

    /**
     * Handles a request for game registration
     *
     * @param gameRegistration the game registration recieved
     * @param connection       the connection to use for error responses
     */
    private boolean handleSessionRequest(GameRegistration gameRegistration, Connection connection) {
        // check the request is valid
        if (!checkSessionRequest(gameRegistration, connection)) {
            // request invalid, error already sent
            LOGGER.debug("Recieved invalid session request...");
        } else if (gameRegistration.isCreationRequest()) {
            // trying to create
            session.create(gameRegistration.getRequesterUsername(), connection);
            connection.sendTCP(GameRegistration.success());
        } else {
            // trying to join
            session.join(gameRegistration.getRequesterUsername(), connection);
            connection.sendTCP(GameRegistration.success());
        }
        return false;
    }

    /**
     * Checks the request for inconsistencies and returns false on fail.
     *
     * @param registration the registration attempted
     * @param connection   the connection that originated the request
     * @return true on success
     */
    private boolean checkSessionRequest(GameRegistration registration, Connection connection) {
        // check if can create
        if (registration.isCreationRequest() && !session.canCreate()) {
            // tried to create but could not
            connection.sendTCP(
                    GameRegistration.failure(GameRegistration.FailureReason.INVALID_GAME_STATE,
                            String.format("The game was not in a state that permitted creation. State: %s", String.valueOf(session.getGameState()))
                    ));
            return false;
        }

        // check if can join
        if (!registration.isCreationRequest() && !session.canJoin()) {
            // tried to join but could not
            connection.sendTCP(
                    GameRegistration.failure(GameRegistration.FailureReason.INVALID_GAME_STATE,
                            String.format("The game was not in a state that permitted joining. State: %s", String.valueOf(session.getGameState()))
                    ));
            return false;
        }

        return true;
    }

}
