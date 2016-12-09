package uq.deco2800.singularity.common.representations.coaster.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by khoi_truong on 2016/10/18.
 */
public class DisconnectedPlayer extends Update {


	@JsonProperty
	@NotEmpty
	private String name;

	public DisconnectedPlayer(NewPlayer p) {
		this.name = p.getName();
	}

	public DisconnectedPlayer() {
	}

	public String getName() {
		return name;
	}
}
