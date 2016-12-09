package uq.deco2800.singularity.common.representations;

import java.util.UUID;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author dloetscher
 *
 */
// If properties are unknown when (de)serialising JSON, ignore, don't error.
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

	@JsonProperty
	// The ID of a user. Generated via UUID.randomUUID().toString().
	// Should only be generated when getting a new User from a registration.
	private String userId = null;

	@JsonProperty
	@NotEmpty
	// Username of a user. Must be unique system wide.
	private String username = null;

	@JsonProperty
	// A user's first name.
	private String firstName = null;

	@JsonProperty
	// A user's middle name.
	private String middleName = null;

	@JsonProperty
	// A user's last name
	private String lastName = null;

	@JsonProperty
	// Cannot have an empty password. Used to authenticate a user.
	private String password = null;

	// A salt randomly generated on first create of a user. Used on password
	// hashing.
	private String salt = null;

	/**
	 * Constructor for a user class. Used for Jackson deserialising to object.
	 * Will automatically generate a new UUID for the userID.
	 */
	public User() {
		// Constructor for Jackson Serialising
	}

	/**
	 * Constructor to make a user class from an existing UUID and username.
	 * 
	 * @param userId
	 *            The user's ID. A string representation of a UUID
	 * @param username
	 *            The username of the user. Must not be null or empty.
	 */
	public User(String userId, String username) {
		this.userId = userId;
		this.username = username;
	}

	/**
	 * Overloaded constructor
	 * @param username
	 * @param firstName
	 * @param middleName
	 * @param lastName
	 * @param password
	 */
	public User(String username, String firstName, String middleName, String lastName, String password){
		this(username, firstName, middleName, lastName, password, "", "");
	}

	/**
	 * Creates a new user instance auto filling details.
	 * 
	 * @param username
	 *            The user's requested username. Must not be empty.
	 * @param firstName
	 *            The user's first name
	 * @param middleName
	 *            The user's middle name
	 * @param lastName
	 *            The user's last name
	 */
	public User(String username, String firstName, String middleName, String lastName, String password) {
		this.username = username;
		this.lastName = lastName;
		this.middleName = middleName;
		this.firstName = firstName;
		this.password = password;
	}

	/**
	 * Retrieves the user's UUID in String form.
	 * 
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Retrieves the user's username
	 * 
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Retrieves the user's first name
	 * 
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Retrieves the user's middle name
	 * 
	 * @return the middleName
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * Retrieves the user's last name
	 * 
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Retrieves the salt used to hash the password
	 * 
	 * @return A HEX based String represenation of the salt.
	 */
	public String getSalt() {
		return salt;
	}

	/**
	 * Retrieves the user's password or hashed password, whichever is stored
	 * should be clear from the context this method is used. As an example, if
	 * retrieved from the database, it will be the hashed password as the
	 * database stores the hash. If the object is created from a request, then
	 * it will be the actual password, not a hash. Can also return null if the
	 * {@link #clearPasswords()} method was called.
	 * 
	 * @return The stored password
	 */
	public String getPassword() {
		return password;
	}
	
	public User setUserId(String id) {
		this.userId = id;
		return this;
	}

	/**
	 * Updates the user's first name to the given first name.
	 * 
	 * @param firstName
	 * @return The updated user object.
	 */
	public User setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	/**
	 * Updates the user's last name to the given last name.
	 * 
	 * @param lastName
	 * @return The updated user object.
	 */
	public User setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	/**
	 * Sets the middle name of the user.
	 * 
	 * @param middleName
	 *            The updated middle name.
	 * @return The updated user object.
	 */
	public User setMiddleName(String middleName) {
		this.middleName = middleName;
		return this;
	}

	/**
	 * Sets the salt that is used to hash the password
	 * 
	 * @param salt
	 *            A HEX based String representation of the salt
	 * @return The updated user object.
	 */
	public User setSalt(String salt) {
		this.salt = salt;
		return this;
	}

	/**
	 * Sets the password stored in the Object
	 * 
	 * @param password
	 *            The password to store
	 * @return The updated user object.
	 */
	public User setPassword(String password) {
		this.password = password;
		return this;
	}

	/**
	 * Clears all password related fields by setting them to null.
	 */
	public void clearPasswords() {
		this.password = null;
		this.salt = null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String passwordPrinted = null;
		if (password != null) {
			passwordPrinted = "<redacted>";
		}
		String saltPrinted = null;
		if (salt != null) {
			saltPrinted = "<redacted>";
		}
		return "User [userId=" + userId + ", username=" + username
				+ ", firstName=" + firstName + ", middleName=" + middleName
				+ ", lastName=" + lastName + ", password=" + passwordPrinted
				+ ", salt=" + saltPrinted + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((middleName == null) ? 0 : middleName.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((salt == null) ? 0 : salt.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		if (!(obj instanceof User))
			return false;
		User other = (User) obj;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (middleName == null) {
			if (other.middleName != null)
				return false;
		} else if (!middleName.equals(other.middleName))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (salt == null) {
			if (other.salt != null)
				return false;
		} else if (!salt.equals(other.salt))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}
