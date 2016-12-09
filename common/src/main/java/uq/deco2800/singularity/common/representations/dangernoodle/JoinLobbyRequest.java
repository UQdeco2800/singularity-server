package uq.deco2800.singularity.common.representations.dangernoodle;

/**
 * Created by khoi_truong on 2016/10/18.
 * <p>
 * This class is used as a message which is sent from the client to the
 * server to request to join a lobby.
 */
public class JoinLobbyRequest extends GameStateMessage {
    private String playerID;
    private String lobbyID;

    public JoinLobbyRequest(String playerID, String lobbyID) {
        this.playerID = playerID;
        this.lobbyID = lobbyID;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getLobbyID() {
        return lobbyID;
    }
}
