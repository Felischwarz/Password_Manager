import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * 
 * @author Felician Schwarz
 * @version 1.0.0
 * 
 */

public class DeleteItemGUI extends JDialog {

	private JPasswordField passwordField;
	private JTextField textField;
	private JLabel confirmLbl;
	private JLabel resultLbl;


	/**
	 * @wbp.parser.constructor
	 */
	public DeleteItemGUI(Password pw, JFrame parent) {
		initialize(pw.getTitle());
		JButton deleteButton = new JButton("delete");
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				resultLbl.setVisible(true);

				if((Manager.getPlainTextPassword(pw.getTitle(), new String(passwordField.getPassword()), pw.getMasterKeyId())) == null)
				{
					
					resultLbl.setText("invalid password.");
				}
				else if(!textField.getText().equals(pw.getTitle())) 
				{
					resultLbl.setText("invalid name.");
				}
				else
				{
					Boolean success = Manager.removePassword(pw, new String(passwordField.getPassword()));
					
					if(success)
					{
						dispose();
					}
					else {
						resultLbl.setText("Could not remove the password.");
					}
					
				}
			}
		});
		deleteButton.setBounds(167, 175, 89, 23);
		getContentPane().add(deleteButton);
	}

	public DeleteItemGUI(MasterKey mKey, JFrame parent) {
		initialize(mKey.title);
		JButton deleteButton = new JButton("delete");
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				resultLbl.setVisible(true);
				
				String hash = MasterKey.sha256(new String(passwordField.getPassword()));
				if(!mKey.getHashedKey().equals(hash))
				{
					resultLbl.setText("invalid password.");
				}
				else if((!textField.getText().equals(mKey.title)))
				{
					resultLbl.setText("invalid name.");
				}
				else
				{
					Boolean success = Manager.removeMasterKey(mKey.getId(), new String(passwordField.getPassword()));	
							
					if(success)
					{
						dispose();
					}
					else {
						UserData data = SaveSystem.getInstance().loadUserData();
						
						int refCount = 0;
						for(Password pw: data.passwords)
						{
							if(pw.getMasterKeyId() == mKey.getId())
							{
								refCount++;
							}
						}
						
						if(refCount > 0)
						{
							resultLbl.setText("Could not remove the masterkey because it's connected to (" + Integer.toString(refCount) + ") Passwords. "
									+ "Remove all referenced passwords before removing the masterkey");
						}
						else {
							resultLbl.setText("Could not remove the password.");
						}
						
					}
				}
			}
		});
		deleteButton.setBounds(167, 179, 89, 23);
		getContentPane().add(deleteButton);
	}
	
	/**
	 * Initialize the contents of the frame.
	 * @param the title of the Item to delete
	 */
	private void initialize(String title) {
		UserData data = SaveSystem.getInstance().loadUserData();
		setBounds(100, 100, 434, 274);
		setResizable(false);
		setModal(true);
		getContentPane().setLayout(null);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(189, 74, 163, 20);
		getContentPane().add(passwordField);
		
		JLabel lblNewLabel = new JLabel("Password");
		lblNewLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		lblNewLabel.setBounds(70, 77, 109, 14);
		getContentPane().add(lblNewLabel);
		
		textField = new JTextField();
		textField.setBounds(189, 115, 163, 20);
		getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("retype the name: " + title);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.TRAILING);
		lblNewLabel_1.setBounds(10, 118, 169, 14);
		getContentPane().add(lblNewLabel_1);
		
		confirmLbl = new JLabel("Confirm delete");
		confirmLbl.setHorizontalAlignment(SwingConstants.CENTER);
		confirmLbl.setBounds(153, 25, 116, 14);
		getContentPane().add(confirmLbl);
		
		resultLbl = new JLabel("result");
		resultLbl.setHorizontalAlignment(SwingConstants.CENTER);
		resultLbl.setBounds(38, 209, 346, 14);
		getContentPane().add(resultLbl);
		resultLbl.setVisible(false);
	}
	
	/**
	 * shows the dialog window to the user.
	 */
	public void showDialog() {
		setVisible(true);
		return;
	}
}
