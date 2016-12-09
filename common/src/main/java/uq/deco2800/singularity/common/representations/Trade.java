package uq.deco2800.singularity.common.representations;

import java.util.UUID;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//If properties are unknown when (de)serialising JSON, ignore, don't error.
@JsonIgnoreProperties(ignoreUnknown = true)

public class Trade {

	@JsonProperty
	// The ID of a Trade. Generated via UUID.randomUUID().toString().
	// Should only be generated when getting a new Trade from a registration.
	private String collectionId = null;

	@JsonProperty
	@NotEmpty
	// itemId of a Trade. Must be unique system wide.
	private String itemId = null;

	@JsonProperty
	// A Trade's amount for an item.
	private String itemAmount = null;

	
	/**
	 * Constructor for a Trade class. Used for Jackson deserialising to object.
	 * Will automatically generate a new UUID for the collectionId. 
	 */
	public Trade() {
		// Constructor for Jackson Serialising
	}
	
	/**
	 * Constructor to make a Trade class from an existing UUID and itemId.
	 * 
	 * @param collectionId
	 *            The Trade's ID. A string representation of a UUID
	 * @param itemId
	 *            The itemId of the Trade. Must not be null or empty.
	 */
	public Trade(String collectionId, String itemId) {
		this.collectionId = collectionId;
		this.itemId = itemId;
		
	}
	
	

	/**
	 * Constructor to make a Trade class from an existing UUID and itemId.
	 * 
	 * @param collectionId
	 *            The Trade's ID. A string representation of a UUID
	 * @param itemId
	 *            The itemId of the Trade. Must not be null or empty.
	 *            
	 *@param itemAmount
	 *            The Item amount of the Trade. Must not be null or empty.
	 */
	public Trade(String collectionId, String itemId, String itemAmount) {
		this.collectionId = collectionId;
		this.itemId = itemId;
		this.itemAmount = itemAmount;
	}

	

	/**
	 * Retrieves the Trade's UUID in String form.
	 * 
	 * @return the collectionId
	 */
	public String getcollectionId() {
		return collectionId;
	}

	/**
	 * Retrieves the Trade's itemId
	 * 
	 * @return the itemId
	 */
	public String getitemId() {
		return itemId;
	}

	/**
	 * Retrieves the Trade's item amount
	 * 
	 * @return the itemAmount
	 */
	public String getitemAmount() {
		return itemAmount;
	}

	
	
	public Trade setcollectionId(String id) {
		this.collectionId = id;
		return this;
	}

	/**
	 * Updates the Trade's item amount to the given Item amount by players. 
	 * 
	 * @param itemAmount
	 * @return The updated Trade object.
	 */
	public Trade setItemAmount(String itemAmount) {
		this.itemAmount = itemAmount;
		return this;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
	
		return "Trade [collectionId=" + collectionId + ", itemId=" + itemId
				+ ", itemAmount=" + itemAmount + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((collectionId == null) ? 0 : collectionId.hashCode());
		result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
		result = prime * result + ((itemAmount == null) ? 0 : itemAmount.hashCode());
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
		if (!(obj instanceof Trade))
			return false;
		Trade other = (Trade) obj;
		if (itemAmount == null) {
			if (other.itemAmount != null)
				return false;
		} else if (!itemAmount.equals(other.itemAmount))
			return false;
		
		if (collectionId == null) {
			if (other.collectionId != null)
				return false;
		} else if (!collectionId.equals(other.collectionId))
			return false;
		if (itemId == null) {
			if (other.itemId != null)
				return false;
		} else if (!itemId.equals(other.itemId))
			return false;
		return true;
	}

}

