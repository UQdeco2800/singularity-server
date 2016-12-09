package uq.deco2800.singularity.common.representations.coaster.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by ryanj on 19/10/2016.
 */
public abstract class GenericPlayerUpdate extends Update {
	@JsonProperty
	@NotEmpty
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}
