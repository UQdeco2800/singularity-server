package uq.deco2800.singularity.common.representations.duxcom.gamestate;

/**
 * Stores data regarding the game messager.
 *
 * Created by liamdm on 9/10/2016.
 */
public abstract class AbstractGameStateMessage implements GameStateMessage {
    private String username;
    private String userID;
    private String tokenID;

    public void attachInformation(String username, String userID, String tokenID){
        this.username = username;
        this.userID = userID;
        this.tokenID = tokenID;
    }


    public String getUsername() {
        return username;
    }

    public String getUserID() {
        return userID;
    }

    public String getTokenID() {
        return tokenID;
    }


}
