package uq.deco2800.singularity.common.representations;

import java.util.UUID;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeResponse {

		@JsonProperty
		// The ID of a requested trade. Generated via UUID.randomUUID().toString().
		// Should only be generated when getting a new User from a registration.
		private String userId = null;

		@JsonProperty
		@NotEmpty
		// the trade request collection ID which has 
		private String itemId = null;
		
		@JsonProperty
		@NotEmpty
		// the trade request collection ID which has 
		private String collectionId = null;

		
		/**
		 * Constructor for a trade request class. Used for Jackson deserialising to object.
		 * Will automatically generate a new UUID for the userID.
		 */
		public TradeResponse() {
			// Constructor for Jackson Serialising
		}

		/**
		 * Constructor to make a Trade Response class 
		 * from an existing UUID and offered Collection Id
		 * 
		 * @param userId
		 *            The user Id. A string representation of a UUID
		 * @param collectionId
		 *            The offered Collection Id. Must not be null or empty.
		 * @param itemId
		 *            The offered item Id. Must not be null or empty.           
		 *            
		 */
		public TradeResponse(String userId, String itemId,String collectionId ) {
			this.userId = userId;
			this.itemId=itemId;
			this.collectionId = collectionId;
		}

		

		/**
		 * Retrieves the user Id  
		 * 
		 * @return the userId
		 */
		public String getUserId() {
			return userId;
		}

		/**
		 * Retrieves the collection Id
		 * 
		 * @return the collection Id
		 */
		public String getcollectionId() {
			return collectionId;
		}
		
		/**
		 * Retrieves the item Id
		 * 
		 * @return the item Id
		 */
		public String getitemId() {
			return itemId;
		}
		
		public TradeResponse setUserId(String id) {
			this.userId = id;
			return this;
		}
		
		public TradeResponse setcollectionId(String Cid) {
			this.collectionId = Cid;
			return this;
		}
		
		public TradeResponse setitemId(String itemId) {
			this.itemId = itemId;
			return this;
		}

		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {

			return "TradeResponse [userId=" + userId + ",itemId=" + itemId
				+ ", collectionId=" + collectionId + "]";
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((userId== null) ? 0 : userId.hashCode());
			result = prime * result + ((collectionId == null) ? 0 : 
				collectionId.hashCode());
			result = prime * result + ((itemId == null) ? 0 : 
				itemId.hashCode());
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
			if (!(obj instanceof TradeResponse))
				return false;
			TradeResponse other = (TradeResponse) obj;
			if (userId == null) {
				if (other.userId != null)
					return false;
			} else if (!userId.equals(other.userId))
				return false;
			if (itemId == null) {
				if (other.itemId != null)
					return false;
			} else if (!itemId.equals(other.itemId))
				return false;
			if (collectionId == null) {
				if (other.collectionId != null)
					return false;
			} else if (!collectionId.equals(other.collectionId))
				return false;
			return true;
		}

	}