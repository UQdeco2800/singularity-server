package uq.deco2800.singularity.server.dangernoodle;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by khoi_truong on 2016/10/18.
 * <p>
 * This class is used to mimic a game session that the players can join. Once
 * registered, the session will be accessible from the client. A client can
 * also choose between multiple sessions (which are game lobbies) to join.
 */
public class DangernoodleGameLobby {
    // Static field which is used to keep track of all lobbies currently in
    // the game. This is also used to ensure that no UUID is the same across
    // all lobies.
    private static List<UUID> currentLobbies = new ArrayList<>();
    // Uniquely identifier for this session. It must be ensured to be unique
    // across all sessions on the server.
    private UUID lobbyID;

    /**
     * Default constructor for the class. Upon initiation it will create a
     * new unique identifier which will be used to identify different
     * sessions inside a game.
     */
    public DangernoodleGameLobby() {
        UUID newUUID = UUID.randomUUID();
        // Check for duplication of lobby ID. If there is no duplication,
        // simply add the newly created ID to the list of ID.
        if (!currentLobbies.contains(newUUID)) {
            lobbyID = newUUID;
            currentLobbies.add(newUUID);
        }
    }

    /**
     * Return the list of all game sessions currently inside the game.
     *
     * @return a list of all game sessions currently inside the game
     *
     * @ensure list of all game sessions currently inside the game
     */
    public static List<UUID> getCurrentLobbies() {
        return currentLobbies;
    }

    /**
     * Return the current lobby's ID.
     *
     * @return an UUID representing the game's current lobby ID
     *
     * @ensure an UUID representing the game's current lobby ID
     */
    public UUID getLobbyID() {
        return lobbyID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            throw new NullPointerException("Object cannot be null.");
        } else if (!(obj instanceof DangernoodleGameLobby)) {
            return false;
        } else {
            return this.lobbyID.equals(((DangernoodleGameLobby) obj).lobbyID);
        }
    }

    @Override
    public int hashCode() {
        return lobbyID.hashCode();
    }

    @Override
    public String toString() {
        return "Current lobby ID: " + lobbyID.toString();
    }
}
