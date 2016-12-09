package uq.deco2800.singularity.clients.dangernoodle;

/**
 * Created by khoi_truong on 2016/10/18.
 * <p>
 * This class is used the client internally to update information about the
 * current noodle who is in turn.
 */
public class GameStateUpdateMessageFormat {
    private String inTurnPlayer;
    private double positionX;
    private double positionY;

    public GameStateUpdateMessageFormat(String inTurnPlayer, double
            positionX, double positionY) {
        this.inTurnPlayer = inTurnPlayer;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public double getPositionY() {
        return positionY;
    }

    public double getPositionX() {
        return positionX;
    }

    public String getInTurnPlayer() {
        return inTurnPlayer;
    }
}
