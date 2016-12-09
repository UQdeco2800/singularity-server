package uq.deco2800.singularity.common.representations.realtime;

public class IncomingMessage {

	private String message;
	private String toThreadId;
	private String fromUserName;
	private String fromUserId;
	private String fromTokenId;
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
	/**
	 * @return the toThreadId
	 */
	public String getToMessageChannelId() {
		return toThreadId;
	}
	/**
	 * @param toThreadId the toThreadId to set
	 */
	public void setToThreadId(String toThreadId) {
		this.toThreadId = toThreadId;
	}
	/**
	 * @return the fromUserName
	 */
	public String getFromUserName() {
		return fromUserName;
	}
	/**
	 * @param fromUserName the fromUserName to set
	 */
	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}
	/**
	 * @return the fromUserId
	 */
	public String getFromUserId() {
		return fromUserId;
	}
	/**
	 * @param fromUserId the fromUserId to set
	 */
	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}
	/**
	 * @return the fromTokenId
	 */
	public String getFromTokenId() {
		return fromTokenId;
	}
	/**
	 * @param fromTokenId the fromTokenId to set
	 */
	public void setFromTokenId(String fromTokenId) {
		this.fromTokenId = fromTokenId;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "IncomingMessage [message=" + message + ", toThreadId="
				+ toThreadId + ", fromUserName=" + fromUserName
				+ ", fromUserId=" + fromUserId + ", fromTokenId=" + fromTokenId
				+ "]";
	}
	
	
}
