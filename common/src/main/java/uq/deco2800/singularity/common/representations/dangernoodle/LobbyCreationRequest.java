package uq.deco2800.singularity.common.representations.dangernoodle;

/**
 * Created by khoi_truong on 2016/10/18.
 *
 * This class is used by the client to request the server to create a new
 * lobby. Upon success, the server will response with a LobbyCreated message
 * indicating that the request is done.
 */
public class LobbyCreationRequest extends GameStateMessage {
    private String creator;

    public LobbyCreationRequest(String creator) {
        this.creator = creator;
    }

    public String getCreator() {
        return creator;
    }
}
