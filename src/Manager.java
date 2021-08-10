import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 
 * @author Felician Schwarz
 * @version 1.0.0
 * 
 */

public class Manager {

	public static void main(String[] args) {
		SaveSystem.getInstance().loadUserData();

		if (args.length == 0) {
			ShowHelp();
			CLI();
		} else {
			executeCommand(args);
		}
	}

	/**
	 * Creates a command-line interface where the user can execute different
	 * commands. The interface is running in an infinite loop, until the program
	 * gets terminated.
	 */
	public static void CLI() {
		while (true) {
			Scanner scanner = new Scanner(System.in);
			String i = scanner.nextLine();
			String[] args = i.split(" ");
			executeCommand(args);
		}
	}

	/**
	 * Executes a command for the password-manager. Type "-help" for information
	 * about the different commands.
	 * 
	 * @param args arguments for the command.
	 */
	public static void executeCommand(String[] args) {
		if (args.length == 0) {
			return;
		} else if (args[0].length() == 0) {
			return;
		}

		switch (args[0]) {
		case "-addmkey":
			if (args.length == 3) {
				createMasterKey(args[1], args[2]);
				SaveSystem.getInstance().loadUserData().logData();
			} else {
				System.out.println("Invalid arguments. Correct pattern is: -addmasterkey [key title] [keypw]");
			}
			break;

		case "-addpw":
			if (args.length == 4 || args.length == 5) {
				UserData data = SaveSystem.getInstance().loadUserData();

				String title = args[2];
				int masterKeyId;

				try {
					masterKeyId = Integer.parseInt(args[1]);
				} catch (Exception e) {
					System.out.println("Masterkey id needs to be a number!");
					break;
				}

				String masterKeyKey = args[3];

				String hashedKey = MasterKey.sha256(masterKeyKey);

				Boolean found = false;
				for (MasterKey master : data.masterKeys) {
					if (master.getId() == masterKeyId && master.getHashedKey().equals(hashedKey)) {
						found = true;
					}
				}

				if (!found) {
					System.out.println("Invalid Masterkey credentials!");
					break;
				}

				// don't add the password if one with the same title does already exist
				try {
					if (passwordTitleExistsForMasterkey(title, masterKeyId)) {
						System.out.println("A password with the title '" + title
								+ "' does already exist for the Masterkey with the id '" + Integer.toString(masterKeyId)
								+ "'");
						break;
					}

				} catch (Exception e) {
					e.printStackTrace();
					break;
				}

				Password pw = null;

				if (args.length == 4) {
					pw = new AutoPassword(masterKeyId, masterKeyKey, title);
				} else if (args.length == 5) {
					pw = new Password(masterKeyId, masterKeyKey, title, args[4]);
				}

				data.passwords.add(pw);
				SaveSystem.getInstance().saveUserData(data);
				data.logData();
			}

			else {
				System.out.println(
						"Invalid arguments. Correct pattern is: -addpassword [masterkey id] [password title] [masterkey key] [password(optional)]");
			}
			break;

		case "-show":
			if (args.length == 4) {
				String result;
				try {
					result = getPlainTextPassword(args[2], args[3], Integer.parseInt(args[1]));
				} catch (NumberFormatException e) {
					System.out.println("Masterkey id needs to be a number!");
					break;
				}
				if (result == null) {
					System.out.println("Invalid password title or masterkey!");
				} else {
					System.out.println("Password for " + args[2] + ": " + result);
				}
			} else {
				System.out.println("Invalid arguments. Correct pattern is: -show [masterkey id] [pwtitle] [keypw]");
			}
			break;

		case "-rmmkey":

			if (args.length == 3) {
				UserData data = SaveSystem.getInstance().loadUserData();

				int refCount = 0;
				Boolean success;

				// by id
				try {
					int arg1 = Integer.parseInt(args[1]);
					success = removeMasterKey(arg1, args[2]);

					if (!success) {
						for (Password pw : data.passwords) {
							if (pw.getMasterKeyId() == arg1) {
								refCount++;
							}
						}
					}
				}
				// by title
				catch (NumberFormatException e) {
					success = removeMasterKey(args[1], args[2]);

					if (!success) {
						int mkeyId = -1;
						for (MasterKey mKey : data.masterKeys) {
							if (mKey.title.equals(args[1])) {
								mkeyId = mKey.getId();
								break;
							}
						}
						for (Password pw : data.passwords) {
							if (pw.getMasterKeyId() == mkeyId) {
								refCount++;
							}
						}
					}
				}

				if (success) {
					SaveSystem.getInstance().loadUserData().logData();
				} else if (refCount > 0) {
					System.out.println("Could not remove the masterkey because it's connected to ("
							+ Integer.toString(refCount) + ") Passwords. "
							+ "Remove all referenced passwords before removing the masterkey");
				} else {
					System.out.println(
							"Operation failed. Could not remove the masterkey. Do you have entered an invalid password?");
				}
			} else {
				System.out.println(
						"Invalid arguments. Correct pattern is: -rmmkey [masterkey id | masterkey title] [keypw]");
			}
			break;

		case "-rmpw":
			if (args.length == 4) {
				try {
					Boolean success = removePassword(args[2], args[3], Integer.parseInt(args[1]));
					if (success) {

						SaveSystem.getInstance().loadUserData().logData();
					} else {
						System.out.println(
								"Operation failed. Could not remove the password. Do you have entered an invalid password?");
					}

				} catch (NumberFormatException e) {
					System.out.println("Masterkey id needs to be a number!");
				}
			} else {
				System.out.println("Invalid arguments. Correct pattern is: -rmpw [masterkey id] [pwtitle] [keypw]");
			}
			break;

		case "-help":
			ShowHelp();
			break;

		case "-display":
			SaveSystem.getInstance().loadUserData().logData();
			break;

		default:
			System.out.println("Unknown command: '" + args[0] + "'");
			break;
		}
	}

	/**
	 * prints a guide to the console, which displays all available commands.
	 */
	public static void ShowHelp() {
		System.out.println("_______________________GUIDE_______________________");
		System.out.println("-addmkey [key title] [keypw] ---> creates a new masterkey");
		System.out.println(
				"-addpw [masterkey id] [password title] [masterkey key] [password(optional)] ---> creates a new password");
		System.out.println("-show [masterkey id] [pwtitle] [keypw] ---> displays a specific password in the console");
		System.out.println("-display ---> displays the data of the Password Manager");
		System.out.println("-rmmkey [masterkey id | masterkey title] [keypw] ---> removes a specific masterkey");
		System.out.println("-rmpw [masterkey id] [pwtitle] [keypw] ---> removes a specific password");

	}

	public static void createMasterKey(String title, String keyPW) {
		UserData data = SaveSystem.getInstance().loadUserData();
		MasterKey key = new MasterKey(title, keyPW);

		data.masterKeys.add(key);

		SaveSystem.getInstance().saveUserData(data);
	}

	/**
	 * Gets the plain-text password of the entered password title
	 * 
	 * @param pwTitle     the title of the password which should be displayed
	 * @param masterPw    the password which should match the pwtitle
	 * @param masterKeyId the masterKeyId of the password
	 * @return the plain-text password or null if the operation failed
	 */
	public static String getPlainTextPassword(String pwTitle, String masterPw, int masterKeyId) {
		String hashedKey = MasterKey.sha256(masterPw);
		Password pw = null;

		UserData data = SaveSystem.getInstance().loadUserData();

		for (Password password : data.passwords) {
			if (password.getTitle().toLowerCase().equals(pwTitle.toLowerCase())
					&& password.getMasterKeyId() == masterKeyId) {
				pw = password;
				break;
			}
		}

		if (pw != null) {
			for (MasterKey master : data.masterKeys) {
				if (master.getId() == masterKeyId && master.getHashedKey().equals(hashedKey)) {
					if (masterPw != null) {
						return pw.decrypt(pw.getEncrypted(), masterPw + pw.getMasterKeySalt());
					}
				}
			}
		}

		return null;
	}

	/**
	 * Gets a Password object by the password title and the password's masterkey id
	 * 
	 * @param title       the title of the password
	 * @param masterKeyId the id of the password's masterkey
	 * @return the Password object or null if it was not found
	 */
	public static Password getPwByTitleAndMasterKeyId(String title, int masterKeyId) {
		UserData data = SaveSystem.getInstance().loadUserData();

		Password pw = null;

		for (Password password : data.passwords) {
			if (password.getMasterKeyId() == masterKeyId && password.getTitle().equals(title)) {
				pw = password;
			}
		}

		return pw;
	}

	/**
	 * checks wether or not a Password object matches with a masterkey password
	 * 
	 * @param pw     the password to check
	 * @param mkeyPw the masterkey password to check
	 * @return wether they match or not
	 */
	public static Boolean checkPassword(Password pw, String mkeyPw) {
		UserData data = SaveSystem.getInstance().loadUserData();

		String hashedKey = MasterKey.sha256(mkeyPw);

		int mKeyId = pw.getMasterKeyId();

		for (MasterKey mKey : data.masterKeys) {
			if (mKey.getId() == mKeyId && mKey.getHashedKey().equals(hashedKey)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 
	 * @param title       the password title to check if it exists
	 * @param masterkeyId the id of the masterkey which holds the password
	 * @return whether the password title exists for the masterkey of the provided
	 *         masterkeyId
	 * @throws Exception if there is no masterkey existing with the provided
	 *                   masterkeyId
	 */
	public static Boolean passwordTitleExistsForMasterkey(String title, int masterkeyId) throws Exception {
		UserData data = SaveSystem.getInstance().loadUserData();

		int mKeyId = -1;

		for (MasterKey mKey : data.masterKeys) {
			if (mKey.getId() == masterkeyId) {
				mKeyId = mKey.getId();
				break;
			}
		}

		if (mKeyId == -1) {
			throw new Exception("ERROR: passwordTitleExistsForMasterkey() was called with an invalid masterkeyId");
		}

		for (Password pw : data.passwords) {
			if (pw.getMasterKeyId() == mKeyId && pw.getTitle().equals(title)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * removes a password from user data
	 * 
	 * @param pw     the password to remove
	 * @param mKeyPw the masterkey password of the matching masterkey
	 * @return whether or not the password was removed successfully
	 */
	public static Boolean removePassword(Password pw, String mKeyPw) {
		UserData data = SaveSystem.getInstance().loadUserData();

		if (!checkPassword(pw, mKeyPw)) {
			// invalid credentials
			return false;
		}

		Boolean removed = false;
		List<Password> newPws = new ArrayList<Password>();

		for (Password pass : data.passwords) {
			if (!pass.getTitle().equals(pw.getTitle())) {
				newPws.add(pass);
			} else {
				removed = true;
			}
		}
		data.passwords = newPws;
		SaveSystem.getInstance().saveUserData(data);
		return removed;
	}

	/**
	 * This is the second overload for removePassword. It's used to remove the
	 * password by title and mKeyId instead of the object.
	 * 
	 * @param pwTitle     the title of the password to remove
	 * @param mKeyPw      the masterkey password of the matching masterkey
	 * @param masterkeyId the id of the matching masterkey
	 * @return whether or not the password was removed successfully
	 */
	public static Boolean removePassword(String pwTitle, String mKeyPw, int masterkeyId) {
		Password pw = getPwByTitleAndMasterKeyId(pwTitle, masterkeyId);

		if (pw == null) {
			return false;
		}

		return removePassword(pw, mKeyPw);
	}

	/**
	 * Removes a specific masterkey if the correct password is provided and there
	 * aren't any password references for the key.
	 * 
	 * @param keyId the id of the masterkey to remove
	 * @param keyPw the password of the masterkey to remove
	 * @return whether the operation was successful or not
	 */
	public static Boolean removeMasterKey(int keyId, String keyPw) {
		UserData data = SaveSystem.getInstance().loadUserData();

		// check password references
		for (Password pw : data.passwords) {
			if (pw.getMasterKeyId() == keyId) {
				return false;
			}
		}

		String hashedKey = MasterKey.sha256(keyPw);
		Boolean removed = false;
		List<MasterKey> newKeys = new ArrayList<MasterKey>();

		for (MasterKey mKey : data.masterKeys) {
			if (mKey.getId() == keyId && mKey.getHashedKey().equals(hashedKey)) {
				removed = true;
			} else {
				newKeys.add(mKey);
			}
		}

		if (removed) {
			data.masterKeys = newKeys;
			SaveSystem.getInstance().saveUserData(data);
		}

		return removed;
	}

	/**
	 * This is the second overload of removeMasterKey. It is used to remove a
	 * masterkey py title instead of id.
	 * 
	 * @param keyTitle the title of the masterkey to remove
	 * @param keyPw    the password of the masterkey to remove
	 * @return whether the operation was successful or not
	 */
	public static Boolean removeMasterKey(String keyTitle, String keyPw) {
		UserData data = SaveSystem.getInstance().loadUserData();

		for (MasterKey mKey : data.masterKeys) {
			if (mKey.title.equals(keyTitle)) {
				return removeMasterKey(mKey.getId(), keyPw);
			}
		}

		return false;
	}

	/**
	 * finds a masterkey by it's id
	 * 
	 * @param id the id of the masterkey
	 * @return the masterkey or null if it was not found
	 */
	public static MasterKey findMasterkeyById(int id) {
		UserData data = SaveSystem.getInstance().loadUserData();

		MasterKey key = null;
		for (MasterKey mKey : data.masterKeys) {
			if (id == mKey.getId()) {
				key = mKey;
			}
		}

		return key;
	}
}
