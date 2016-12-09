package uq.deco2800.singularity.common.tests;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.junit.Assert;
import org.junit.Test;

import uq.deco2800.singularity.common.util.PasswordHashUtils;

/**
 * @author dloetscher
 * 		
 */
public class PasswordHashUtilsTests {
	
	/**
	 * @throws NoSuchAlgorithmException
	 */
	@Test
	public void getSaltTest() throws NoSuchAlgorithmException {
		byte[] salt = PasswordHashUtils.getSalt();
		Assert.assertNotNull("Getting a salt should not return null", salt);
		Assert.assertEquals("A salt should be 16bytes long = 128 bits", 16, salt.length);
	}
	
	@Test
	public void hexStringUtilTest() throws NoSuchAlgorithmException {
		String[] texts = { "Some Text", "", "aaa", "0x00", "'[],.//```';!@#$%^&*()-_=+" };
		for (String text : texts) {
			byte[] expectedByteArray = text.getBytes();
			byte[] actualByteArray = PasswordHashUtils.fromHexString(PasswordHashUtils.toHexString(expectedByteArray));
			Assert.assertArrayEquals(
					"Converting from an array of bytes to a hex string and back should result in the same byte array",
					expectedByteArray, actualByteArray);
		}
	}
	
	@Test
	public void hashAndValidateTest() throws NoSuchAlgorithmException, InvalidKeySpecException {
		
		String[] passwords = { "Some Text", "", "aaa", "0x00", "'[],.//```';!@#$%^&*()-_=+" };
		for (String password : passwords) {
			byte[] salt = PasswordHashUtils.getSalt();
			String hash = PasswordHashUtils.hash(password, salt);
			Assert.assertTrue("The password hashed should return as valid",
					PasswordHashUtils.validatePassword(password, hash, salt));
			Assert.assertTrue("The password hashed should return as valid",
					PasswordHashUtils.validatePassword(password, hash, PasswordHashUtils.toHexString(salt)));
		}
		
	}
	
}
