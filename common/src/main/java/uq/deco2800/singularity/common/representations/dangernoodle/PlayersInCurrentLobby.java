package uq.deco2800.singularity.common.representations.dangernoodle;

import java.util.ArrayList;

/**
 * Created by khoi_truong on 2016/10/21.
 * <p>
 * Wrapper container that is used to tell the clients/players about the
 * number of players currently inside this lobby.
 */
public class PlayersInCurrentLobby extends GameStateMessage {
    // Private field to store information about all players.
    private ArrayList<String> players;

    /**
     * Setter method for the list of players.
     *
     * @param players
     *         the list of players to set to private field
     *
     * @throws NullPointerException
     *         if given list is null
     * @require given players != null
     * @ensure getPlayers() == players
     */
    public void setPlayers(ArrayList<String> players) {
        this.players = new ArrayList<>(players);
    }

    /**
     * Getter method for the list of players.
     *
     * @return return the list of string representation of player IDs
     *
     * @ensure return the list of string representation of player IDs
     */
    public ArrayList<String> getPlayers() {
        return players;
    }
}
