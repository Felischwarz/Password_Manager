import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class manages all user data during runtime
 * @author Felician Schwarz
 * @version 1.0.0
 * 
 */

public class UserData implements Serializable {
	public List<MasterKey> masterKeys = new ArrayList<MasterKey>(); 
	public List<Password> passwords = new ArrayList<Password>();
	public int currentMasterKeyId;	

	/**
	 * prints a visual representation of the structure of masterKeys and passwords to the console
	 */
	public void logData() {		
		System.out.println("------------------------------------------------");
		System.out.println("masterkeys:");
		for(MasterKey mkey: masterKeys)
		{
			System.out.println("\t" + mkey.title + "(" +  mkey.getId() + "):" + "\n\t\tpasswords: ");
			for(Password pw: passwords)
			{
				if(pw.getMasterKeyId() == mkey.getId())
				{
					System.out.println("\t\t\t-" + pw.getTitle());
				}
			}
			System.out.println("\n");
		}
		System.out.println("------------------------------------------------");
	}
}

