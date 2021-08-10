import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.io.Serializable;
import java.util.Base64;

/**
 * 
 * @author Felician Schwarz
 * @version 1.0.0
 * 
 */

public class Password implements Serializable {
	// Source used to learning about encryption in general: https://www.youtube.com/user/Computerphile
	
	private int masterKeyId;
	private String title;
	private String encrypted;
	private final String masterKeySalt;
	
	//only these lengths are excepted for the Key in the AES encryption.
	public static final int[] validEncryptionKeyLenghts = {16, 24, 32};
	
	
	Password(int masterKeyId, String masterKey, String title, String password) {
		this.masterKeyId = masterKeyId;
		this.title = title;
		masterKeySalt = createMasterKeySalt(masterKey);

		try {
			encrypted = encrypt(password, masterKey + masterKeySalt);
			
		} catch (Exception e) {
			encrypted = null;
		} 
	}
	
	/**
	 * gets the masterkey id
	 * @return the masterkey id
	 */
	public int getMasterKeyId() {
		return masterKeyId;
	}
	
	/**
	 * gets the masterkey title
	 * @return the masterkey title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * gets the encrypted masterkey password
	 * @return the encrypted masterkey password
	 */
	public String getEncrypted() {
		return encrypted;
	}
	
	/**
	 * gets the masterkey salt
	 * @return the masterkey salt
	 */
	public String getMasterKeySalt() {
		return masterKeySalt;
	}
	
	/**
	 * used to encrypt passwords with the related masterkey using AES (Advanced-Encryption-Standard)
	 * Source for the concept: https://stackoverflow.com/questions/3451670/java-aes-and-using-my-own-key
	 * @param original the original plain-text password 
	 * @param key the related masterkey
	 * @return the encrypted password 
	 * @throws Exception thrown if the encryption failed
	 */
	public String encrypt(String original, String key) throws Exception {
		byte[] keyBytes = key.getBytes();
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
		
	    Cipher cipher;
	    cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		
		byte[] encrypted = cipher.doFinal((original).getBytes());
	    String encryptedString = Base64.getEncoder().encodeToString(encrypted);
	    return encryptedString;    
	}
	
	/**
	 * used to decrypt passwords with the related masterkey using AES (Advanced-Encryption-Standard)
	 * @param encrypted the encrypted password 
	 * @param key the related masterkey
	 * @return the original plain-text password 
	 */
	public String decrypt(String encrypted, String key) {
		byte[] keyBytes = key.getBytes();
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
	
		Cipher cipher;
		try 
		{
			cipher = Cipher.getInstance("AES");
		    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
		    
	    	byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
	    	String originalString = new String(original);	    
		    return originalString;
		}
	    catch (Exception e) {
	    	//if an exception occurs here, if decryption failed (an invalid key was provided).
	    	System.out.println("Decryption failed. Try Again!");
	    	return null;
	    }    
	}
	
	/**
	 * AES encryption requires a Key with a length of 16 / 24 / 32 bytes. For the key we use the masterkey which is provided by the user. 
	 * This function is used to create a salt like padding for the master key to match a suitable length if the masterkey alone doesn't match.
	 * @param masterKey the masterkey to create the salt for.
	 * @return a salt or an empty string in case of the masterkey being suitable alone. 
	 */
	private String createMasterKeySalt(String masterKey) {
		int saltLength = 0;
		for(int length: validEncryptionKeyLenghts)
		{
			if(masterKey.length() == length)
			{
				//the salt is empty because the masterkey already has the correct length
				return "";
			}
			
			if(masterKey.length() < length)
			{
				saltLength = length - masterKey.length();
				break;
			}
		}
		if (saltLength == 0)
		{
			//the masterkey is longer then the biggest valid length, so it's invalid!
			return null;
		}
		else
		{
			return new RandomString(saltLength).toString();
		}
		
	}
}
