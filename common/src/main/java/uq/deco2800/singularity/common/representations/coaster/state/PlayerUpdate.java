package uq.deco2800.singularity.common.representations.coaster.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by ryanj on 19/10/2016.
 */
public class PlayerUpdate extends Update {

	@JsonProperty
	@NotEmpty
	private float posX;

	@JsonProperty
	@NotEmpty
	private float posY;

	@JsonProperty
	@NotEmpty
	private int health;

	@JsonProperty
	@NotEmpty
	private String name;

	public String getName() {
		return name;
	}

	public PlayerUpdate() {
	}

	public float getPosX() {
		return posX;
	}

	public float getPosY() {
		return posY;
	}

	public int getHealth() {
		return health;
	}


	//Note if health gets reduced remove the renderable bullet closest LOL
	public PlayerUpdate(String name, float posX, float posY, int health) {
		this.name = name;
		this.posX = posX;
		this.posY = posY;
		this.health = health;
	}

}
