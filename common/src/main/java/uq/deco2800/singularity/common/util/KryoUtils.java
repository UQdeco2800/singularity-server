package uq.deco2800.singularity.common.util;

import com.esotericsoftware.kryo.Kryo;
import uq.deco2800.singularity.common.SessionType;
import uq.deco2800.singularity.common.representations.dangernoodle.*;
import uq.deco2800.singularity.common.representations.duxcom.gamestate.*;
import uq.deco2800.singularity.common.representations.duxcom.gamestate.GameStateMessage;
import uq.deco2800.singularity.common.representations.duxcom.gamestate.herospecific.HeroSpecifier;
import uq.deco2800.singularity.common.representations.duxcom.gamestate.herospecific.HeroTarget;
import uq.deco2800.singularity.common.representations.duxcom.gamestate.herospecific.SquadSpecifier;
import uq.deco2800.singularity.common.representations.pyramidscheme.multiplayer.*;
import uq.deco2800.singularity.common.representations.realtime.*;

import java.util.ArrayList;
import java.util.LinkedList;

public class KryoUtils {


	public static void coasterClasses(Kryo kryo) {

		kryo.register(uq.deco2800.singularity.common.representations.coaster.state.DamageUpdate.class);
		kryo.register(uq.deco2800.singularity.common.representations.coaster.state.DisconnectedPlayer.class);
		kryo.register(uq.deco2800.singularity.common.representations.coaster.state.EndGame.class);
		kryo.register(uq.deco2800.singularity.common.representations.coaster.state.FireUpdate.class);
		kryo.register(uq.deco2800.singularity.common.representations.coaster.state.GameStateUpdate.class);
		kryo.register(uq.deco2800.singularity.common.representations.coaster.state.NewPlayer.class);
		kryo.register(uq.deco2800.singularity.common.representations.coaster.state.NilUpdate.class);
		kryo.register(uq.deco2800.singularity.common.representations.coaster.state.PlayerUpdate.class);
		kryo.register(uq.deco2800.singularity.common.representations.coaster.state.Update.class);
		kryo.register(uq.deco2800.singularity.common.representations.coaster.GameState.class);
		kryo.register(uq.deco2800.singularity.common.representations.coaster.Score.class);
		kryo.register(uq.deco2800.singularity.common.representations.coaster.ScoreType.class);

	}


	public static void registerCommonClasses(Kryo kryo) {
		kryo.register(IncomingMessage.class);
		kryo.register(Registration.class);
		kryo.register(StatusUpdate.class);
		kryo.register(SessionType.class);
		kryo.register(BroadcastMessage.class);
		kryo.register(BroadcastMessage.MessageType.class);
		kryo.register(GameStateMessage.class);
		kryo.register(AbstractGameStateMessage.class);
		kryo.register(GameMetadata.class);
		kryo.register(GameMetadata.MessageType.class);
		kryo.register(uq.deco2800.singularity.common.representations.duxcom.gamestate.GameState.class);
		kryo.register(GameRegistration.class);
		kryo.register(GameRegistration.FailureReason.class);
		kryo.register(GameRegistration.MessageType.class);
		kryo.register(StateChange.class);
		kryo.register(ControlMessage.class);
		kryo.register(ControlMessage.MessageType.class);
		kryo.register(LinkedList.class);
		kryo.register(GameUpdate.class);
		kryo.register(GameUpdate.MessageType.class);
		kryo.register(String[].class);
		kryo.register(PlayerAction.class);
		kryo.register(SquadState.class);
		kryo.register(SquadState.MessageType.class);
		kryo.register(SquadSpecifier.class);
		kryo.register(HeroSpecifier.HeroType.class);
		kryo.register(PlayerAction.MessageType.class);
		kryo.register(HeroSpecifier.class);
		kryo.register(HeroTarget.class);
		kryo.register(uq.deco2800.singularity.common.representations.dangernoodle.GameState.class);
		kryo.register(int[].class);
		// Dangernoodle representation.
		kryo.register(PlayersInCurrentLobby.class);
		kryo.register(ArrayList.class);
		kryo.register(RealTimeSessionConfiguration.class);
		kryo.register(DisconnectedPlayer.class);
		kryo.register(NoodlePosition.class);
		kryo.register(Double.class);
		kryo.register(SimpleMessage.class);
		kryo.register(PositionUpdate.class);
		// Pyramid representations
		kryo.register(MultiplayerState.class);
		kryo.register(ChampionAbility.class);
		kryo.register(PassTurn.class);
		kryo.register(PlayerDeck.class);
		kryo.register(PlayerForfeited.class);
		kryo.register(GrindCard.class);
		kryo.register(PlayCard.class);
		kryo.register(PyramidRefill.class);
		kryo.register(PlayerToken.class);
		kryo.register(java.util.Map.class);
		kryo.register(java.util.ArrayList.class);
		kryo.register(java.util.HashMap.class);
		kryo.register(Turn.class);
		kryo.register(Attack.class);
		kryo.register(GameOver.class);
		coasterClasses(kryo);
	}
}
