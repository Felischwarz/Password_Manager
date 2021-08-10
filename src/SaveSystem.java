import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;


/**
 * This class manages saving and loading Password_Manager files
 * @author Felician Schwarz
 * @version 1.0.0
 * 
 */

public class SaveSystem {

	private static SaveSystem Instance = null;
	//source for storing persistent data in java: https://stackoverflow.com/questions/14107442/location-to-store-save-files-on-local-machine-in-java
	
	//This is the directory, where the user data is stored in.
	private static File saveDir = new File(System.getProperty("user.home"), "Password_Manager");
	private static String databasePath = new File(saveDir.getPath(), "Userdata.pwm").getPath();
	
	SaveSystem() {}
	/**
	 * creates a Singleton instance if it doesn't exist already
	 * @return the Singleton instance of the Database
	 */
	public static SaveSystem getInstance() {
		if (Instance == null) {
			Instance = new SaveSystem();
		}

		return Instance;
	}

	/**
	 * Used to save user data. The data will be stored in the "databasePath" location as serialized UserData class.
	 * @param data the data to save
	 */
	public void saveUserData(UserData data) {
		FileOutputStream fout;
		ObjectOutputStream oos;
		
		//create save directory if it doesn't exists
		if (!saveDir.exists()){
			saveDir.mkdirs();
		}		
		
		try {
			fout = new FileOutputStream(databasePath);
			oos = new ObjectOutputStream(fout);
			oos.writeObject(data);

		}
		catch (IOException e) {
			System.out.println("Failed to save UserData.");
			e.printStackTrace();
		}
	}

	/**
	 *  Used to load user data. The data will be loaded from the serialized UserData class in the "databasePath" location.
	 *  If there exists no such file, a new one will be created.
	 * @return UserData the loaded user data
	 */
	public UserData loadUserData() {
		FileInputStream fin;
		ObjectInputStream ois = null;
		
		//create save directory if it doesn't exists
		if (!saveDir.exists()){
			saveDir.mkdirs();
		}

		try {
			fin = new FileInputStream(databasePath);
			ois = new ObjectInputStream(fin);
			
		} catch (FileNotFoundException e) {
			//if no userdata file exists, create one
			System.out.println(databasePath + " file not found. Creating a new one...");
			UserData newData = new UserData();
			saveUserData(newData);
			return newData;
			
		} catch (IOException e) {
			System.out.println("Failed to load UserData.");
			e.printStackTrace();
		}
		

		UserData data = null;
		try {
			data = (UserData) ois.readObject();
			
		} catch (ClassNotFoundException e) {
			System.out.println("Failed to load UserData.");
			e.printStackTrace();
			
		} catch (IOException e) {
			System.out.println("Failed to load UserData.");
			e.printStackTrace();
		}

		return data;
	}
}

