package uq.deco2800.singularity.common.representations;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeRequest {

		@JsonProperty
		// The ID of a requested trade. Generated via UUID.randomUUID().toString().
		// Should only be generated when getting a new User from a registration.
		private String requestId = null;

		@JsonProperty
		@NotEmpty
		// the trade request collection ID which has 
		private String offeredCollectionId = null;
		
		@JsonProperty
		@NotEmpty
		// The ID of a user. Generated via UUID.randomUUID().toString().
		// Should only be generated when getting a new User from a registration.
		private String userId = null;

		
		/**
		 * Constructor for a trade request class. Used for Jackson deserialising to object.
		 * Will automatically generate a new UUID for the userID.
		 */
		public TradeRequest() {
			// Constructor for Jackson Serialising
		}

		/**
		 * Constructor to make a Trade Request class 
		 * from an existing UUID and offered Collection Id
		 * 
		 * @param requestId
		 *            The trade request Id. A string representation of a UUID
		 * @param offeredCollectionId
		 *            The offered Collection Id. Must not be null or empty.
		 * @param userId
		 * 			  The id of user 
		 */
		public TradeRequest(String offeredCollectionId, 
				String userId) {
			this.offeredCollectionId = offeredCollectionId;
			this.userId = userId;
		}

		
		/**
		 * Retrieves the trade requested Id 
		 * 
		 * @return the requestId
		 */
		public String getRequestId() {
			return requestId;
		}

		/**
		 * Retrieves the offered collection Id
		 * 
		 * @return the offered collection Id
		 */
		public String getOfferedCollectionId() {
			return offeredCollectionId;
		}
		
		/**
		 * Retrieves the user Id
		 * @return
		 */
		public String getUserId() {
			return userId;
		}
		
		public TradeRequest setRequestId(String rid) {
			this.requestId = rid;
			return this;
		}
		
		public TradeRequest setOfferedCollectionId(String cid) {
			this.offeredCollectionId = cid;
			return this;
		}
		
		public TradeRequest setUserId(String uid) {
			this.userId = uid;
			return this;
		}

		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {

			return "TradeRequest [requestId=" + requestId + ", offeredCollectionId=" 
			+ offeredCollectionId + ", userId =" + userId + "]";
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((requestId== null) ? 0 : requestId.hashCode());
			result = prime * result + ((offeredCollectionId == null) ? 0 : 
				offeredCollectionId.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof TradeRequest))
				return false;
			TradeRequest other = (TradeRequest) obj;
			if (requestId == null) {
				if (other.requestId != null)
					return false;
			} else if (!requestId.equals(other.requestId))
				return false;
			if (offeredCollectionId == null) {
				if (other.offeredCollectionId != null)
					return false;
			} else if (!offeredCollectionId.equals(other.offeredCollectionId))
				return false;
			if (userId == null) {
				if (other.userId != null)
					return false;
			} else if (!userId.equals(other.userId))
				return false;
			return true;
		}

	}

	
