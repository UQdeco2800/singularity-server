package uq.deco2800.singularity.common.representations;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Token {

	@JsonProperty
	@NotEmpty
	private String tokenId;

	@JsonProperty
	@Range(min = 0, max = Long.MAX_VALUE)
	private long expires;

	@JsonProperty
	@NotEmpty
	private String userId;

	// 1000ms * 60s * 60min * 6hr
	public static final int DEFAULT_TOKEN_EXPIRY = 1000 * 60 * 60 * 6;

	public Token() {

	}

	public Token(String userId) {
		this.userId = userId;
		expires = System.currentTimeMillis() + DEFAULT_TOKEN_EXPIRY;
		this.tokenId = UUID.randomUUID().toString();
	}
	
	public Token(String tokenId, long expires, String userId) {
		this.tokenId = tokenId;
		this.expires = expires;
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

	/**
	 * @return the expires
	 */
	public long getExpires() {
		return expires;
	}

	/**
	 * @param expires
	 *            the expires to set
	 */
	public void setExpires(long expires) {
		this.expires = expires;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Date dateExpires = new Date(expires);
		return "Token [tokenId=" + tokenId + ", expires=" + dateExpires.toString()
				+ ", userId=" + userId + "]";
	}
}
