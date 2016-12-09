package uq.deco2800.singularity.common.representations.dangernoodle;

/**
 * Created by khoi_truong on 2016/10/18.
 * <p>
 * This class is used to inform all clients about some player who has
 * disconnected from the game.
 */
public class DisconnectedPlayer extends GameStateMessage {
    private String disconnectedPlayer;

    public void setDisconnectedPlayer(String disconnectedPlayer) {
        if (disconnectedPlayer == null) {
            throw new NullPointerException("Player ID cannot be null.");
        }
        this.disconnectedPlayer = disconnectedPlayer;
    }

    public String getDisconnectedPlayer() {
        return disconnectedPlayer;
    }
}
