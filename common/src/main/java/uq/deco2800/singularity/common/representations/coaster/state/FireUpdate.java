package uq.deco2800.singularity.common.representations.coaster.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by ryanj on 19/10/2016.
 */
public class FireUpdate extends Update {


	@JsonProperty
	@NotEmpty
	private double posX;

	@JsonProperty
	@NotEmpty
	private double posY;

	@JsonProperty
	@NotEmpty
	private String name;


	public FireUpdate() {
	}

	public String getName() {
		return name;
	}


	public double getPosX() {
		return posX;
	}

	public double getPosY() {
		return posY;
	}


	public FireUpdate(String name, double posX, double posY) {
		this.name = name;
		this.posX = posX;
		this.posY = posY;
	}
}
