package uq.deco2800.singularity.common.representations.duxcom.gamestate;

/**
 * A signal only message for a game state change.
 *
 * Created by liamdm on 11/10/2016.
 */
public class StateChange extends AbstractGameStateMessage {

    /**
     * The games new state
     */
    private GameState state;

    /**
     * Generates a state changed message with the new state
     * @param newState the new games state
     */
    public StateChange(GameState newState) {
        this.state = newState;
    }

    /**
     * Get the current state
     * @return current state
     */
    public GameState getState() {
        return state;
    }

    /**
     * Returns true if the current game session is aborted and should be ditched
     * @return iff ditching
     */
    public boolean shouldDitch(){
        return state == GameState.FINISHED
                || state == GameState.DITCHED
                || state == GameState.CLOSED;
    }

    /**
     * Generates an admin ditched state change
     * @return the admin ditched state
     */
    public static StateChange adminDitched(){
        return new StateChange(GameState.DITCHED);
    }

    @Override
    public GameStateMessageType getMessageType() {
        return GameStateMessageType.STATE_CHANGE;
    }

    /**
     * Deserializer constructor
     */
    public StateChange(){

    }
}
