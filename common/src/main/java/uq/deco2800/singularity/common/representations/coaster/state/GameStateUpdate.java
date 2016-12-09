package uq.deco2800.singularity.common.representations.coaster.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import uq.deco2800.singularity.common.representations.coaster.GameState;

public class GameStateUpdate extends Update {


	@JsonProperty
	@NotEmpty
	private GameState state;

	public GameStateUpdate(GameState state) {
		this.state = state;
	}

	public GameStateUpdate() {
	}

	public GameState getState() {
		return state;
	}
}
