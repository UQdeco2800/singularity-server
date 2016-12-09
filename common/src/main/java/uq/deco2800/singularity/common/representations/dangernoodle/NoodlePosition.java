package uq.deco2800.singularity.common.representations.dangernoodle;

/**
 * Created by khoi_truong on 2016/10/23.
 * <p>
 * This class is used to signal clients about the noodle's position who is in
 * its turn.
 */
public class NoodlePosition extends GameStateMessage {
    private String clientID;
    private double positionX;
    private double positionY;

    public void setClientID(String clientID) {
        if (clientID == null) {
            throw new NullPointerException("Client ID cannot be null.");
        }
        this.clientID = clientID;
    }

    public String getClientID() {
        return clientID;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public double getPositionX() {
        return positionX;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    public double getPositionY() {
        return positionY;
    }
}
