import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

/**
 * 
 * @author Felician Schwarz
 * @version 1.0.0
 * 
 */

public class PwGUI extends JDialog {

	public JFrame frame;
	private JPasswordField passwordField;
	
	private String masterKeyTitle = "";
	//needs to be stored here, to copy to clipboard
	private String plainTextPw;

	
	public PwGUI(Password pw, JFrame parent) {
		initialize(pw);
	}


	/**
	 * Initialize the contents of the frame.
	 * @param the password to create the PwGUI for 
	 */
	private void initialize(Password pw) {
		setBounds(100, 100, 381, 259);
		setResizable(false);
		getContentPane().setLayout(null);
		
		
		JLabel pwTitleLbl = new JLabel(pw.getTitle());
		pwTitleLbl.setHorizontalAlignment(SwingConstants.CENTER);
		pwTitleLbl.setBounds(99, 11, 190, 14);
		getContentPane().add(pwTitleLbl);
		
		UserData data = SaveSystem.getInstance().loadUserData();
		
		for(MasterKey mKey: data.masterKeys)
		{
			if(mKey.getId() == pw.getMasterKeyId())
			{
				masterKeyTitle = mKey.title;
			}
		}
		JLabel masterkeyLbl = new JLabel("Enter Password for " + masterKeyTitle);
		masterkeyLbl.setHorizontalAlignment(SwingConstants.CENTER);
		masterkeyLbl.setBounds(59, 36, 269, 14);
		getContentPane().add(masterkeyLbl);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(88, 63, 206, 20);
		getContentPane().add(passwordField);
		
		JLabel resultLbl = new JLabel("");
		resultLbl.setHorizontalAlignment(SwingConstants.CENTER);
		resultLbl.setBounds(51, 166, 277, 14);
		getContentPane().add(resultLbl);
		
		JButton btnNewButton = new JButton("reveal");
		
		JButton copyToClipBtn = new JButton("copy to clipboard");
		copyToClipBtn.setVisible(false);
		copyToClipBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringSelection stringSelection = new StringSelection(plainTextPw);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);
			}
		});
		copyToClipBtn.setBounds(104, 191, 174, 23);
		getContentPane().add(copyToClipBtn);
		
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String result = Manager.getPlainTextPassword(pw.getTitle(), new String(passwordField.getPassword()), pw.getMasterKeyId());
				if(result == null) 
				{
					resultLbl.setText("invalid password.");
					copyToClipBtn.setVisible(false);
				}
				else
				{
					plainTextPw = result;
					resultLbl.setText("Password for " + pw.getTitle() + ": " + plainTextPw);
					copyToClipBtn.setVisible(true);
				}
				passwordField.setText("");
			}
		});
		btnNewButton.setBounds(142, 94, 104, 28);
		getContentPane().add(btnNewButton);
	}
}
