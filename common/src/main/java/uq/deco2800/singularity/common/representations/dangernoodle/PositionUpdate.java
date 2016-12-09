package uq.deco2800.singularity.common.representations.dangernoodle;

/**
 * Created by khoi_truong on 2016/10/17.
 * <p>
 * This class is used to wrap around the message that is sent to the server
 * which will be later broadcast to other players.
 */
public class PositionUpdate {
    private String playerId;
    private double positionX;
    private double positionY;

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    /**
     * Return the player ID of the noodle who is in turn. This can be used
     * for limiting inputs from noodles who are not in turn.
     *
     * @return the player ID of the noodle who is in turn
     *
     * @ensure the player ID of the noodle who is in turn
     */
    public String getPlayerId() {
        return playerId;
    }

    /**
     * Return the position x of the noodle, which can be used to draw the
     * noodle at the new position in the game.
     *
     * @return the position x of the noodle
     *
     * @ensure the position x of the noodle
     */
    public double getPositionX() {
        return positionX;
    }

    /**
     * Return the position y of the noodle, which can be used to draw the
     * noodle at the new position in the game.
     *
     * @return the position y of the noodle
     *
     * @ensure the position y of the noodle
     */
    public double getPositionY() {
        return positionY;
    }
}
