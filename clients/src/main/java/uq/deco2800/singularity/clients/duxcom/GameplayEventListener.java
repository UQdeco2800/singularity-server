package uq.deco2800.singularity.clients.duxcom;

import uq.deco2800.singularity.common.representations.duxcom.gamestate.*;

/**
 * Listens for a gameplay event
 * Created by liamdm on 9/10/2016.
 */
public abstract class GameplayEventListener {

    /**
     * Recieved a game metadata query
     * @param query the query recieved
     */
    public abstract void recievedGameMetadataQuery(GameMetadata query);

    /**
     * Recieved a game registration message
     * @param registration the object recieved
     */
    public abstract void recievedGameRegistrationMessage(GameRegistration registration);

    /**
     * Recieved a control message
     * @param gsm the object recieved
     */
    public abstract void recievedControlMessage(ControlMessage gsm);

    /**
     * Received a state change message
     * @param gsm the object received
     */
    public abstract void recievedStateMessage(StateChange gsm);

    /**
     * Recieved a game update message
     * @param gsm the object recieved
     */
    public abstract void recievedUpdateMessage(GameUpdate gsm);

    /**
     * Recieved a player action message
     * @param gsm the object recieved
     */
    public abstract void recievedPlayerActionMessage(PlayerAction gsm);

    /**
     * Recieved a squad state message
     * @param gsm the object recieved
     */
    public abstract void recievedSquadStateMessage(SquadState gsm);
}
