package uq.deco2800.singularity.common.representations.coaster.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by rcarrier on 20/10/2016.
 */
public class NewPlayer extends Update {

	@JsonProperty
	@NotEmpty
	private String name;

	@JsonProperty
	private int tickrate;

	@JsonProperty
	@NotEmpty
	private boolean host;

	public NewPlayer() {
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {

		return name;
	}

	public NewPlayer(String name) {
		this.name = name;
		this.host = false;
	}


	public NewPlayer(String name, int tickrate) {
		this.tickrate = tickrate;
		this.name = name;
		this.host = true;
	}

	public void setTickrate(int tickrate) {
		this.tickrate = tickrate;
	}


	public int getTick() {
		return tickrate;
	}

	public boolean getHost() {
		return host;
	}

}

