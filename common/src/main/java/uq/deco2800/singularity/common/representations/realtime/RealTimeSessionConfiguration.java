package uq.deco2800.singularity.common.representations.realtime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import uq.deco2800.singularity.common.SessionType;

public class RealTimeSessionConfiguration {

	@JsonProperty
	private String sessionID;

	@JsonProperty
	private int port;
	
	@JsonProperty
	private SessionType session;
	
	@JsonIgnore
	public boolean isValid() {
		if (port != 0 && (port < 1024 || port > 65535 || session == null)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the session
	 */
	public SessionType getSession() {
		return session;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(SessionType session) {
		this.session = session;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String namedString = (sessionID != null) ? " <NAME=" + String.valueOf(sessionID) + ">" : "";
		return "RealTimeSessionConfiguration [port=" + port
				+ ", session=" + session + "]" + namedString;
	}

	/**
	 * Gets the session ID of this session
	 */
	public String getSessionID() {
		return sessionID;
	}

	/**
	 * Sets the session ID the session ID to set
	 * @param sessionID
	 */
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
}
