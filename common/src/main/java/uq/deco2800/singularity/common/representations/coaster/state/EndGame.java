package uq.deco2800.singularity.common.representations.coaster.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by ryanj on 19/10/2016.
 */
public class EndGame extends Update {

	@JsonProperty
	@NotEmpty
	private String name;

	public EndGame(String winner) {
		name = winner;
	}

	public String getName() {
		return name;
	}

	public EndGame() {
	}
}
