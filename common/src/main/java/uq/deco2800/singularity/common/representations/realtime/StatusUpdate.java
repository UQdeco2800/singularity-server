package uq.deco2800.singularity.common.representations.realtime;

import javax.ws.rs.core.Response.Status;

public class StatusUpdate {
	
	private int status;
	private String message;
	
	public StatusUpdate(int status, String message) {
		this.status = status;
		this.message = message;
	}
	
	public StatusUpdate() {
		// Needed for Kryo deserialisation
	}
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Status statusType = Status.fromStatusCode(status);
		String status;
		if (statusType == null) {
			status = String.valueOf(this.status);
		} else {
			status = statusType.name();
		}
		return "StatusUpdate [status=" + status + ", message=" + message + "]";
	}
	
}
