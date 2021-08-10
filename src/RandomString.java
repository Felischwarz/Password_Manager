import java.util.concurrent.ThreadLocalRandom;

/**
 * 
 * @author Felician Schwarz
 * @version 1.0.0
 * 
 */

public class RandomString {
	private final String value;
	
	//source: python library: string 
	public final String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public final String lowerAlphabet = upperAlphabet.toLowerCase();
	public final String punctuation = "!\"#$%&\\'()*+,-./:;<=>?@[\\\\]^_`{|}~";
	public final String digits = "0123456789";
	
	public final String characters = upperAlphabet + lowerAlphabet + punctuation + digits;
	
	
	RandomString(int length) {
		String random = "";
		for(int i = 0; i < length; i++)
		{
			random += randomCharacter();
		}
		value = random;
	}
	
	/**
	 * gets the RandomString value 
	 */
	public String toString() {
		return value;
	}
	
	/**
	 * creates a random character, which can be in upperAlphabet, lowerAlphabet, punctuation and digits
	 * @return a random character
	 */
	private char randomCharacter() {
		//source: https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
		int index = ThreadLocalRandom.current().nextInt(0, characters.length());
		return characters.charAt(index); 
	}	
}
