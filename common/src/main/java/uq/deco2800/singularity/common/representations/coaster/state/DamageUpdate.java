package uq.deco2800.singularity.common.representations.coaster.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by ryanj on 19/10/2016.
 */
public class DamageUpdate extends Update {

	@JsonProperty
	@NotEmpty
	private int health;

	@JsonProperty
	@NotEmpty
	private String name;

	public DamageUpdate() {
	}

	public int getHealth() {
		return health;
	}

	public String getName() {
		return name;
	}

	public DamageUpdate(String name, int health) {
		this.health = health;
		this.name = name;
	}
}
