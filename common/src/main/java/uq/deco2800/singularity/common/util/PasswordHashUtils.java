package uq.deco2800.singularity.common.util;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Based on example for good password storing given at:
 * <a href="http://howtodoinjava.com/security/how-to-generate-secure-password-
 * hash-md5-sha-pbkdf2-bcrypt-examples/"> http://howtodoinjava.com/security/how-
 * to-generate-secure-password- hash-md5-sha-pbkdf2-bcrypt-examples/</a>}
 *
 * @author dloetscher
 */
public class PasswordHashUtils {

	/**
	 * The number of times to apply the hashing algorithm to the plain text.
	 */
	public static final int ITERATIONS = 100;

	/**
	 * The algorithm used to hash the password with the salt.
	 * <em>PBKDF2WithHmacSHA1</em> is the recommended strong hashing algorithm
	 * used to store password as informed by the website referenced in {@link #PasswordHashUtils}.
	 */
	public static final String ALGORITHM = "PBKDF2WithHmacSHA1";

	/**
	 * Creates a hash of a given password given a salt using the
	 * {@link #ALGORITHM} with the set number of {@link #ITERATIONS}.
	 * 
	 * @param password
	 *            The password to be hashed. Must not be empty.
	 * @param salt
	 *            The salt used to salt the password. Must not be empty
	 * @return The hashed password in HEX based String representation.
	 * @throws NoSuchAlgorithmException
	 *             If the algorithm used to generate the hash does not exist.
	 * @throws InvalidKeySpecException
	 *             If the generated hash specifications are invalid for the key
	 *             factory to generate a hash.
	 */
	public static String hash(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		char[] passwordChars = password.toCharArray();
		PBEKeySpec spec = new PBEKeySpec(passwordChars, salt, ITERATIONS, 64 * 8);
		SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
		byte[] hash = skf.generateSecret(spec).getEncoded();
		return toHexString(hash);
	}

	/**
	 * Retrieves a random salt using the secure random generator SHA1PRNG.
	 * 
	 * @return An array of bytes used to salt a password.
	 * @throws NoSuchAlgorithmException
	 *             If SHA1PRNG does not exist in the library of secure random
	 *             generators.
	 */
	public static byte[] getSalt() throws NoSuchAlgorithmException {
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[16];
		sr.nextBytes(salt);
		return salt;
	}

	/**
	 * Given an original password, a stored password and a salt used on the
	 * original password, validates that the original password and the salt
	 * together hash to the stored password.
	 * 
	 * @param originalPassword
	 *            The password to verify. Must not be empty.
	 * @param storedPassword
	 *            The password to be verified with. Must be a HEX based string
	 *            representation of a password with a salt that has been hashed
	 *            with this class.
	 * @param salt
	 *            A salt used to salt the original password before being hashed.
	 * @return True if the original password and salt hash to the stored
	 *         password.
	 * @throws NoSuchAlgorithmException
	 *             If the {@link #ALGORITHM} does not exist in the library of
	 *             key factories.
	 * @throws InvalidKeySpecException
	 *             If the given information does not form a valid key
	 *             specification for the key factory to hash the password.
	 */
	public static boolean validatePassword(String originalPassword, String storedPassword, byte[] salt)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] hash = fromHexString(storedPassword);

		PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, ITERATIONS, hash.length * 8);
		SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
		byte[] testHash = skf.generateSecret(spec).getEncoded();

		int diff = hash.length ^ testHash.length;
		for (int i = 0; i < hash.length && i < testHash.length; i++) {
			diff |= hash[i] ^ testHash[i];
		}
		return diff == 0;
	}

	public static String toHexString(byte[] array) throws NoSuchAlgorithmException {
		BigInteger bi = new BigInteger(1, array);
		String hex = bi.toString(16);
		int paddingLength = (array.length * 2) - hex.length();
		if (paddingLength > 0) {
			return String.format("%0" + paddingLength + "d", 0) + hex;
		} else {
			return hex;
		}
	}

	public static byte[] fromHexString(String hex) throws NoSuchAlgorithmException {
		byte[] bytes = new byte[hex.length() / 2];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return bytes;
	}

	public static boolean validatePassword(String originalPassword,
			String storedPassword, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return validatePassword(originalPassword, storedPassword, fromHexString(salt));
		
	}

}
