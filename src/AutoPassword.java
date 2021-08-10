/**
 * 
 * @author Felician Schwarz
 * @version 1.0.0
 * 
 */

public class AutoPassword extends Password {
	AutoPassword(int masterKeyId, String masterKey, String title) {
		super(masterKeyId, masterKey, title, generate());
	}
	
	/**
	 * generates a random password of length 16 including upperAlphabet, lowerAlphabet, punctuation and digits
	 * @return the random password
	 */
	private static String generate() {
		return new RandomString(16).toString();
	}
}
