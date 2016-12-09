package uq.deco2800.singularity.clients.dangernoodle;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uq.deco2800.singularity.clients.realtime.RealTimeClient;
import uq.deco2800.singularity.common.ServerConstants;
import uq.deco2800.singularity.common.SessionType;
import uq.deco2800.singularity.common.representations.dangernoodle.GameState;
import uq.deco2800.singularity.common.representations.dangernoodle.PlayersInCurrentLobby;
import uq.deco2800.singularity.common.representations.dangernoodle.PositionUpdate;
import uq.deco2800.singularity.common.representations.dangernoodle.SimpleMessage;
import uq.deco2800.singularity.common.representations.realtime.RealTimeSessionConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by khoi_truong on 2016/10/16.
 * <p>
 * This class is used by the client to talk to the server. More specifically,
 * it's called inside the game's ClientManager to retrieve an instance of
 * this class. With it the client can send updates to the server.
 */
public class DangerNoodleRealTimeClient extends RealTimeClient {
    // Setting up loggers to be used throughout the file.
    private static final String CLASS = DangerNoodleRealTimeClient.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);

    private RealTimeSessionConfiguration config;
    private DangernoodleRestClient restClient;
    private SessionType sessionType;

    private GameState currentState;
    // ID of this particular client
    private String clientID;
    // IDs of all clients in the lobby
    private ArrayList<String> playerIDs = new ArrayList<>();
    // List of all listeners that are listening to newly received message
    // about players in the lobby.
    private List<DangernoodleEventListener> playerIDsListeners = new ArrayList<>();
    // List of all listeners that are listening to newly received message
    // about play game indicator.
    private List<DangernoodleEventListener> playGameListeners = new ArrayList<>();
    // Listeners for noodles position of noodle at turn
    private List<DangernoodleEventListener> noodlePositionListeners = new ArrayList<>();
    // Listeners for pausing signal
    private List<DangernoodleEventListener> pauseListeners = new ArrayList<>();
    // Listeners for releasing signal when the game starts. This happens when
    // all players have read the instructions and ready to play game.
    private List<DangernoodleEventListener> instructionReleasedListeners = new ArrayList<>();
    // Listeners for disconnected players
    private List<DangernoodleEventListener> disconnectPlayerListeners = new ArrayList<>();

    /**
     * Default constructor for the class.
     *
     * @param config
     *         real time configuration of the lobby which the client wants to
     *         talk to
     * @param restClient
     *         restful client which is used in the parent class
     *
     * @throws NullPointerException
     *         if config, restClient is null
     * @throws IOException
     *         if some I/O operation has failed
     * @require config != null && restClient != null && sesstionType != null
     * @ensure new instance of this class
     */
    public DangerNoodleRealTimeClient(RealTimeSessionConfiguration config,
                                      DangernoodleRestClient restClient)
            throws IOException {
        // Relay back the arguments to the parent class
        super(config, restClient, ServerConstants.PRODUCTION_SERVER);
        LOGGER.info("Created new lobby with: [{}]", config);
        // Set private fields.
        this.config = config;
        this.restClient = restClient;
        this.sessionType = SessionType.DANGER_NOODLES;
        this.clientID = restClient.getUsername();
        // Listener
        realTimeClient.addListener(new DangernoodleRealTimeClientListener());
        // Always keep the players inside the lobby.
        realTimeClient.setTimeout(11000);
        realTimeClient.setKeepAliveTCP(5000);
        realTimeClient.sendTCP(clientID);
    }

    /**
     * Return the current configuration of this lobby.
     *
     * @return the current configuration of this lobby
     *
     * @ensure the current configuration of this lobby
     */
    public RealTimeSessionConfiguration getConfig() {
        return config;
    }

    /**
     * Send an registered object to the server.
     *
     * @param obj
     *         the object to be send to the server
     *
     * @throws NullPointerException
     *         if obj is null
     * @require obj != null and obj is registered
     * @ensure obj will be sent to the server
     */
    public void sendToServer(Object obj) {
        realTimeClient.sendTCP(obj);
    }

    /**
     * This function is used by the client when they leave the lobby
     * (visually).
     */
    public void requestLeaveLobby() {
        sendToServer(GameState.LEAVE_LOBBY);
    }

    /**
     * Add listener to this class, which is triggered when the server has
     * sent some new information.
     *
     * @param listener
     *         the new listener to be added
     *
     * @throws NullPointerException
     *         if listener is null
     * @require listener != null
     * @ensure listener will be added to playerIDsListeners
     */
    public void addPlayersNumberListener(DangernoodleEventListener listener) {
        if (listener == null) {
            throw new NullPointerException("Listener cannot be null.");
        }
        playerIDsListeners.add(listener);
    }

    /**
     * Remove listener from this class, meaning the listener will no longer
     * be triggered when the server has sent some new information.
     *
     * @param listener
     *         the listener to be removed.
     *
     * @throws NullPointerException
     *         if listener is null
     * @require listener != null
     * @ensure listener will be removed from list if it contains the listener
     */
    public void removePlayersNumberListener(DangernoodleEventListener listener) {
        playerIDsListeners.remove(listener);
    }

    /**
     * Add listener to this class, which is triggered when the server has
     * sent some new information.
     *
     * @param listener
     *         the new listener to be added
     *
     * @throws NullPointerException
     *         if listener is null
     * @require listener != null
     * @ensure listener will be added to playGameListeners
     */
    public void addPlayGameListener(DangernoodleEventListener listener) {
        if (listener == null) {
            throw new NullPointerException("Listener cannot be null.");
        }
        playGameListeners.add(listener);
    }

    /**
     * Remove listener from this class, meaning the listener will no longer
     * be triggered when the server has sent some new information.
     *
     * @param listener
     *         the listener to be removed.
     *
     * @throws NullPointerException
     *         if listener is null
     * @require listener != null
     * @ensure listener will be removed from list if it contains the listener
     */
    public void removePlayGameListener(DangernoodleEventListener listener) {
        playGameListeners.remove(listener);
    }

    /**
     * Add listeners to the noodle's position who is at turn.
     *
     * @param listener
     *         listener to be added to the list
     *
     * @throws NullPointerException
     *         if listener is null
     * @require listener != null
     * @ensure listener will be added to the list
     */
    public void addNoodlePositionListener(DangernoodleEventListener listener) {
        if (listener == null) {
            throw new NullPointerException("Listener cannot be null.");
        }
        noodlePositionListeners.add(listener);
    }

    /**
     * Remove listener from this class, meaning the listener will no longer
     * be triggered when the server has sent some new information.
     *
     * @param listener
     *         the listener to be removed.
     *
     * @throws NullPointerException
     *         if listener is null
     * @require listener != null
     * @ensure listener will be removed from list if it contains the listener
     */
    public void removeNoodlePositionListener(DangernoodleEventListener listener) {
        noodlePositionListeners.remove(listener);
    }

    /**
     * Add listeners to pause signal.
     *
     * @param listener
     *         listener to be added to the list
     *
     * @throws NullPointerException
     *         if listener is null
     * @require listener != null
     * @ensure listener will be added to the list
     */
    public void addPauseListener(DangernoodleEventListener listener) {
        if (listener == null) {
            throw new NullPointerException("Listener cannot be null.");
        }
        pauseListeners.add(listener);
    }

    /**
     * Remove listener from this class, meaning the listener will no longer
     * be triggered when the server has sent some new information.
     *
     * @param listener
     *         the listener to be removed.
     *
     * @throws NullPointerException
     *         if listener is null
     * @require listener != null
     * @ensure listener will be removed from list if it contains the listener
     */
    public void removePauseListener(DangernoodleEventListener listener) {
        pauseListeners.remove(listener);
    }

    /**
     * Add listener for signal when the server says that all players have
     * finished reading instruction. Time to play.
     *
     * @param listener
     *         listener to be added to the list
     *
     * @throws NullPointerException
     *         if listener is null
     * @require listener != null
     * @ensure listener will be added to the list
     */
    public void addInstructionReleasedListener(DangernoodleEventListener listener) {
        if (listener == null) {
            throw new NullPointerException("Listener cannot be null.");
        }
        instructionReleasedListeners.add(listener);
    }

    /**
     * Remove listener from this class, meaning the listener will no longer
     * be triggered when the server has sent some new information.
     *
     * @param listener
     *         the listener to be removed.
     *
     * @throws NullPointerException
     *         if listener is null
     * @require listener != null
     * @ensure listener will be removed from list if it contains the listener
     */
    public void removeInstructionReleasedListener(DangernoodleEventListener listener) {
        instructionReleasedListeners.remove(listener);
    }

    /**
     * Add listener for signal when the server says that a player has
     * disconnected.
     *
     * @param listener
     *         listener to be added to the list
     *
     * @throws NullPointerException
     *         if listener is null
     * @require listener != null
     * @ensure listener will be added to the list
     */
    public void addDisconnectPlayerListener(DangernoodleEventListener listener) {
        if (listener == null) {
            throw new NullPointerException("Listener cannot be null.");
        }
        disconnectPlayerListeners.add(listener);
    }

    /**
     * Remove listener from this class, meaning the listener will no longer
     * be triggered when the server has sent some new information.
     *
     * @param listener
     *         the listener to be removed.
     *
     * @throws NullPointerException
     *         if listener is null
     * @require listener != null
     * @ensure listener will be removed from list if it contains the listener
     */
    public void removeDisconnectPlayerListener(DangernoodleEventListener listener) {
        disconnectPlayerListeners.remove(listener);
    }

    /**
     * Return the game type which this client is playing.
     *
     * @return the sessionType which this client is playing
     */
    @Override
    public SessionType getSessionType() {
        return sessionType;
    }

    /**
     * Return the rest client.
     *
     * @return the rest client
     *
     * @ensure the rest client
     */
    public DangernoodleRestClient getRestClient() {
        return restClient;
    }

    /**
     * Return player IDs that are currently inside this lobby.
     *
     * @return a list of string representing player IDs in this lobby.
     *
     * @ensure a list of string representing player IDs in this lobby.
     */
    public ArrayList<String> getPlayerIDs() {
        return playerIDs;
    }

    /**
     * Private internal class which is used to handle communication between
     * this client and the server.
     */
    private class DangernoodleRealTimeClientListener extends Listener {
        @Override
        public void received(Connection connection, Object object) {
            // If the message from the server is a game state message,
            // indicating which state this client and the game are in
            // currently, then update the global game state.
            if (object instanceof GameState) {
                GameState state = (GameState) object;
                currentState = state;
                switch (state) {
                    case LOBBY_FULL:
                        LOGGER.info("Lobby is full.");
                        break;
                    case START_GAME:
                        LOGGER.info("Game is started!");
                        break;
                    case PLAY_GAME:
                        LOGGER.info("Game is now rolling.");
                        for (DangernoodleEventListener listener : playGameListeners) {
                            listener.notifyListener();
                        }
                        break;
                }
            } else if (object instanceof PlayersInCurrentLobby) {
                // Retrieve information about number of players currently
                // inside the lobby.
                PlayersInCurrentLobby convertedObject = (PlayersInCurrentLobby) object;
                // Reset the list before adding.
                playerIDs.clear();
                playerIDs.addAll(convertedObject.getPlayers());
                // Tell any listener (potentially the screen controller) that
                // the player IDs have been updated.
                for (DangernoodleEventListener listener : playerIDsListeners) {
                    listener.notifyListener();
                }
            } else if (object instanceof SimpleMessage) {
                SimpleMessage message = (SimpleMessage) object;
                // If the client has received a pause signal. Pause the game.
                // If one player has left the game, close down all clients.
                // Otherwise, forward messages from one client to others.
                if (message == SimpleMessage.PAUSED) {
                    for (DangernoodleEventListener listener : pauseListeners) {
                        listener.notifyListener();
                    }
                } else if (message == SimpleMessage.PLAYER_LEFT) {
                    for (DangernoodleEventListener listener : disconnectPlayerListeners) {
                        listener.notifyListener();
                    }
                } else {
                    // Otherwise, the message is telling the client to start
                    // the game since all players have done reading
                    // instructions.
                    for (DangernoodleEventListener listener :
                            instructionReleasedListeners) {
                        listener.notifyListener();
                    }
                }
            } else if (object instanceof PositionUpdate) {
                PositionUpdate message = (PositionUpdate) object;
                for (DangernoodleEventListener listener : noodlePositionListeners) {
                    listener.notifyListener(message);
                }
            }
        }
    }
}
