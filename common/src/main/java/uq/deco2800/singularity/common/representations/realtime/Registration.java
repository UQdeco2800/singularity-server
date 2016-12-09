package uq.deco2800.singularity.common.representations.realtime;

import uq.deco2800.singularity.common.SessionType;

public class Registration {
	
	private String userId;
	private String tokenId;
	private SessionType session;
	
	/**
	 * @return the session
	 */
	public SessionType getSession() {
		return session;
	}
	
	/**
	 * @param session
	 *            the session to set
	 */
	public void setSession(SessionType session) {
		this.session = session;
	}
	
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	
	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	/**
	 * @return the tokenId
	 */
	public String getTokenId() {
		return tokenId;
	}
	
	/**
	 * @param tokenId
	 *            the tokenId to set
	 */
	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Registration [session=" + session + ", userId=" + userId + ", tokenId=" + tokenId + "]";
	}
	
}
