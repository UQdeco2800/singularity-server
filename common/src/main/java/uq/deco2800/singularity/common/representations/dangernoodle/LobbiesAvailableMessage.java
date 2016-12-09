package uq.deco2800.singularity.common.representations.dangernoodle;

import java.util.List;

/**
 * Created by khoi_truong on 2016/10/18.
 * <p>
 * This class is used as a response from the server to the client to inform
 * it about currently available lobbies in the game.
 */
public class LobbiesAvailableMessage extends GameStateMessage {
    // List of available lobbies in form of UUID string which will be
    // returned back to the client if requested.
    private List<String> availableLobbies;

    /**
     * Default constructor for the class.
     *
     * @param availableLobbies
     *         a list of currently available lobbies in form of UUID string
     *         which will be used to return back to the client who's requesting
     *         it
     *
     * @throws NullPointerException
     *         if either the list or any of its element is null
     * @require availableLobbies != null && elements of availableLobbies !=
     * null
     * @ensure new instance of this class
     */
    public LobbiesAvailableMessage(List<String> availableLobbies) {
        if (availableLobbies == null) {
            throw new NullPointerException("List cannot be null.");
        }
        for (String lobby : availableLobbies) {
            if (lobby == null) {
                throw new NullPointerException("Lobby cannot be null.");
            }
        }
        // Use shallow copy here since no modification will be done later.
        this.availableLobbies = availableLobbies;
    }

    /**
     * Return the available lobbies. This method will be most likely called
     * inside the client to get the available information of the lobbies.
     *
     * @return a list of available lobbies in form of UUID string
     *
     * @ensure a list of available lobbies in form of UUID string
     */
    public List<String> getAvailableLobbies() {
        return availableLobbies;
    }
}
