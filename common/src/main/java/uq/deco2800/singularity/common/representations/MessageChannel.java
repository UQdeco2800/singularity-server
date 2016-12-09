package uq.deco2800.singularity.common.representations;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageChannel {
	
	@JsonProperty
	private String channelId;
	
	@JsonProperty 
	private String userId;

	/**
	 * @return the threadId
	 */
	public String getChannelId() {
		return channelId;
	}

	/**
	 * @param channelId the threadId to set
	 */
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessageChannel [channelId=" + channelId + ", userId=" + userId
				+ "]";
	}
	
	
}
