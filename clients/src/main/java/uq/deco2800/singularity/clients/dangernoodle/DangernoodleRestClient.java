package uq.deco2800.singularity.clients.dangernoodle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uq.deco2800.singularity.clients.restful.SingularityRestClient;
import uq.deco2800.singularity.common.SessionType;
import uq.deco2800.singularity.common.representations.dangernoodle.GameState;
import uq.deco2800.singularity.common.representations.realtime.RealTimeSessionConfiguration;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

/**
 * Created by khoi_truong on 2016/10/18.
 * <p>
 * This class is used to handle request and response between the client and
 * the server.
 */
public class DangernoodleRestClient extends SingularityRestClient {
    // Setting up loggers to be used throughout the file.
    private static final String CLASS = DangernoodleRestClient.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(CLASS);
    // Either the current client is in a lobby or not.
    private DangerNoodleRealTimeClient currentLobby = null;
    private List<String> lobbiesUUID = new ArrayList<>();
    private String currentUser = null;

    /**
     * Set the current user ID to the one who is logging into the client
     * machine at the moment.
     *
     * @param currentUser
     *         a string representation of current client's username
     *
     * @throws NullPointerException
     *         if currentUser is null
     * @require currentUser != null
     * @ensure getCurrentUser() == currentUser
     */
    public void setCurrentUser(String currentUser) {
        if (currentUser == null) {
            throw new NullPointerException("Current user ID cannot be null.");
        }
        this.currentUser = currentUser;
    }

    /**
     * Return the current user ID who is logging into the client at the moment.
     *
     * @return a string representation of the current client's username
     *
     * @ensure a string representation of the current client's username
     */
    public String getCurrentUser() {
        return currentUser;
    }

    /**
     * Return the current lobby that this client has connected to.
     *
     * @return the current lobby that this client has connected to
     *
     * @ensure the current lobby that this client has connected to
     */
    public DangerNoodleRealTimeClient getCurrentLobby() {
        return currentLobby;
    }

    /**
     * Return all available lobbies in the game.
     *
     * @return a list of real time session configurations of the game
     *
     * @throws IOException
     *         if some I/O error is found
     * @ensure a list of real time session configurations of the game
     */
    public ArrayList<RealTimeSessionConfiguration> requestAvailableLobbies()
            throws IOException {
        ArrayList<RealTimeSessionConfiguration> availableLobbies = new ArrayList<>();
        try {
            availableLobbies.addAll(getActiveGames(SessionType.DANGER_NOODLES));
        } catch (IOException e) {
            LOGGER.error("Requesting available lobbies has failed.");
        }
        return availableLobbies;
    }

    /**
     * Wrapper method which is used by the client to request for a new lobby.
     *
     * @return a real time session configuration of the new lobby
     *
     * @ensure a real time session configuration of the new lobby
     */
    public RealTimeSessionConfiguration requestNewLobby() {
        // Create a new lobby, but has not connected to it yet.
        RealTimeSessionConfiguration newLobby;
        newLobby = super.requestGameSession(SessionType.DANGER_NOODLES);
        // Assign UUID to the new lobby
        while (true) {
            String newLobbyID = UUID.randomUUID().toString();
            if (!lobbiesUUID.contains(newLobbyID)) {
                lobbiesUUID.add(newLobbyID);
                newLobby.setSessionID(newLobbyID);
                break;
            }
        }
        LOGGER.info("REST_CLIENT: Created new lobby: [{}].", newLobby);
        // Automatically join the lobby.
        requestJoinLobby(newLobby);
        return newLobby;
    }

    /**
     * Request by the client to join a lobby.
     *
     * @param config
     *         real time session configuration that is used to connect between
     *         the client and the lobby
     *
     * @throws NullPointerException
     *         if config is null
     * @require config != null
     * @ensure it may be possible to connect to the lobby
     */
    public void requestJoinLobby(RealTimeSessionConfiguration config) {
        try {
            LOGGER.info("REST_CLIENT: Join request to [{}] received.", config);
            // Create a link between this client and the new lobby
            currentLobby = new DangerNoodleRealTimeClient(config, this);
        } catch (IOException e) {
            LOGGER.error("Cannot create a connection between this client and " +
                    "the lobby.");
        }
    }

    /**
     * Request by the client to leave a lobby.
     *
     * @ensure client will be removed/disconnected from the lobby
     */
    public void requestLeaveLobby() {
        LOGGER.info("REST_CLIENT: Leave request from [{}] received.", currentLobby.getConfig());
        currentLobby.requestLeaveLobby();
        currentLobby = null;
    }
}
