import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 
 * @author Felician Schwarz
 * @version 1.0.0
 * 
 */


public class MasterKey implements Serializable {	
	public String title;
	
	private final int id;
	private String hashedKey;
	
	MasterKey(String title, String key) {
		UserData data = SaveSystem.getInstance().loadUserData();
		id = data.masterKeys.size() + 1;
		
		this.title = title;
		hashedKey = sha256(key);
	}
	
	MasterKey(String key) {
		UserData data = SaveSystem.getInstance().loadUserData();
		id = data.masterKeys.size() + 1; 
		
		this.title = "MasterKey" + Integer.toString(id);
		hashedKey = sha256(key);
	}
	
	/**
	 * hashes a String with the sha256 algorithm
	 * @param input the String to hash
	 * @return the hashed string in a base64 format
	 */
	public static String sha256(String input) {
		try 
		{
			//Source: https://stackoverflow.com/questions/5531455/how-to-hash-some-string-with-sha256-in-java
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashedBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
			String base64Hash = Base64.getEncoder().encodeToString(hashedBytes);
			return base64Hash;
		} 
		catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();
			return input;
		}
	}
	
	/**
	 * @return the unique id of the MasterKey
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return the hashed MasterKey in a base64 format
	 */
	public String getHashedKey() {
		return hashedKey;
	}
}
