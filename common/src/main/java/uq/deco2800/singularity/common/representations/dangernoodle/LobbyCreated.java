package uq.deco2800.singularity.common.representations.dangernoodle;

/**
 * Created by khoi_truong on 2016/10/18.
 * <p>
 * This class is used as a response from the server to the client who has
 * requested to create a new lobby.
 */
public class LobbyCreated extends GameStateMessage {
    // Private field to store UUID string of a newly created lobby that the
    // client has just requested to create.
    private String newlyCreatedLobby;

    /**
     * Default constructor for the class.
     *
     * @param newlyCreatedLobby
     *         an UUID string of a new lobby
     *
     * @throws NullPointerException
     *         if newlyCreatedLobby is null
     * @require newlyCreatedLobby != null
     * @ensure new instance of this class
     */
    public LobbyCreated(String newlyCreatedLobby) {
        this.newlyCreatedLobby = newlyCreatedLobby;
    }

    /**
     * Return the newly created lobby in form of UUID string to the client.
     *
     * @return newly created lobby in form of UUID string to the client
     *
     * @ensure newly created lobby in form of UUID string to the client
     */
    public String getNewlyCreatedLobby() {
        return newlyCreatedLobby;
    }
}
