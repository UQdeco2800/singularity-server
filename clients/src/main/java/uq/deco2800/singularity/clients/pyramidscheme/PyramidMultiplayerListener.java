package uq.deco2800.singularity.clients.pyramidscheme;

import uq.deco2800.singularity.common.representations.pyramidscheme.multiplayer.*;

import java.util.EventListener;

/**
 * Created by nick on 22/10/16.
 */
public abstract class PyramidMultiplayerListener implements EventListener {

    public abstract void startGame();

    public abstract void lobbyFull();

    public abstract void lobbyFound();

    public abstract void pyramidRefill(PyramidRefill object);

    public abstract void playerForfeited(PlayerForfeited object);

    public abstract void playCard(PlayCard object);

    public abstract void passTurn(PassTurn object);

    public abstract void grindCard(GrindCard object);

    public abstract void championAbility(ChampionAbility object);

    public abstract void deckReceived(PlayerDeck object);

    public abstract void sendPlayerToken();

    public abstract void sendDeck();

    public abstract void getTurn(Turn object);

    public abstract void attack(Attack object);

    public abstract void nowGameOver(GameOver object);
}
