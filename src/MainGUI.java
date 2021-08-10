import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import javax.swing.JTabbedPane;
import javax.swing.JPanel;

import javax.swing.JButton;

import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JCheckBox;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.awt.Color;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * 
 * @author Felician Schwarz
 * @version 1.0.0
 * 
 */

public class MainGUI {

	private JFrame frame;
	private JPasswordField mKeyPwField;
	private JPasswordField pwField;

	private UserData data = null;
	private JTextField titleField;
	private JTextField mKeyTitleField;
	private JPasswordField passwordField;
	private JPasswordField repeatPasswordField;

	private String masterKeyRandomGeneratedPw;

	private Box passwordVBox = null;
	private Box masterkeysVBox = null;

	
	/**
	 * Launch the application.
	 * @param args additional arguments
	 */
	public static void main(String[] args) {
		try {
			//source: https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGUI window = new MainGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainGUI() {
		data = SaveSystem.getInstance().loadUserData();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setBounds(100, 100, 699, 464);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		frame.setResizable(false);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		tabbedPane.setBackground(Color.WHITE);
		tabbedPane.setBounds(0, 0, 683, 424);
		frame.getContentPane().add(tabbedPane);

		JPanel passwordsPanel = createPasswordsPanel();
		tabbedPane.addTab("Passwords", null, passwordsPanel, null);
		tabbedPane.setBackgroundAt(0, Color.WHITE);

		JPanel AddPasswordPanel = createAddPwPanel();
		tabbedPane.addTab("Add Password", null, AddPasswordPanel, null);

		JPanel masterkeysPanel = createMasterkeysPanel();
		tabbedPane.addTab("Maskerkeys ", null, masterkeysPanel, null);

		JPanel AddMasterKeyPanel = CreateAddMasterKeyPanel();
		tabbedPane.addTab("Add Masterkey", null, AddMasterKeyPanel, null);
	}
	
	/**
	 * creates visual Password Panel of the provided password
	 * @param pw the password to get the data from
	 * @return Box object of the created panel
	 */
	private Box createPasswordItem(Password pw) {
		Box panel = Box.createHorizontalBox();
		panel.setBorder(BorderFactory.createLineBorder(Color.black, 1)); 
		
		JLabel titleLbl = new JLabel(CSS("Title: " + pw.getTitle(), "padding: 10px;"));
		panel.add(titleLbl);
		
		panel.add(Box.createHorizontalStrut(50));
		
		JLabel masterkeyLbl = new JLabel("Masterkey: " + Manager.findMasterkeyById(pw.getMasterKeyId()).title + " (" + pw.getMasterKeyId() + ")");
		panel.add(masterkeyLbl);
		
		panel.add(Box.createHorizontalStrut(50));
		
		JButton revealBtn = new JButton("unlock");
		revealBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PwGUI revealPwGUi = new PwGUI(pw, frame);
				revealPwGUi.setLocationRelativeTo(frame);
				revealPwGUi.setVisible(true);
			}
		});
		panel.add(revealBtn);
		
		panel.add(Box.createHorizontalStrut(10));
		JButton deleteBtn = new JButton("delete");
		deleteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DeleteItemGUI dialog = new DeleteItemGUI(pw, frame);
				dialog.setLocationRelativeTo(frame);
				dialog.showDialog();
				refreshPasswordVBox();
			}
		});
		panel.add(deleteBtn);
		panel.add(Box.createHorizontalStrut(10));
		return panel;
	}

	/**
	 * creates visual Masterkey Panel of the provided MasterKey
	 * @param mKey the MasterKey to get the data from
	 * @return Box object of the created panel
	 */
	private Box createMasterkeyItem(MasterKey mKey) {
		Box panel = Box.createHorizontalBox();
		panel.setBorder(BorderFactory.createLineBorder(Color.black, 1)); 
		
		JLabel masterkeyTitle = new JLabel(CSS("Title: " + mKey.title.toString(), "padding: 10px;"));
		panel.add(masterkeyTitle);
		
		panel.add(Box.createHorizontalStrut(50));
		
		JLabel masterkeyId = new JLabel(CSS("ID: " + Integer.toString(mKey.getId()), "padding: 10px;"));
		masterkeyId.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(masterkeyId);
		
		panel.add(Box.createHorizontalStrut(50));
		
		JButton deleteBtn = new JButton("delete");
		deleteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DeleteItemGUI dialog = new DeleteItemGUI(mKey, frame);
				dialog.setLocationRelativeTo(frame);
				dialog.showDialog();
				refreshMasterkeysVBox();
			}
		});
		panel.add(deleteBtn);
		panel.add(Box.createHorizontalStrut(10));
		return panel;
	}
	
	/**
	 * creates a visual Password Panel which displays a list of all passwords of the user.
	 * @return JPanel object of the created panel
	 */
	private JPanel createPasswordsPanel() {
		JPanel passwordsPanel = new JPanel();
		passwordsPanel.setBackground(Color.WHITE);
		
		passwordsPanel.setLayout(new BoxLayout(passwordsPanel, BoxLayout.X_AXIS));

		JScrollPane scrollPane_1 = new JScrollPane();
		passwordsPanel.add(scrollPane_1);

		JPanel panel = new JPanel();
		
		scrollPane_1.setViewportView(panel);
		
		passwordVBox = Box.createVerticalBox();
		panel.add(passwordVBox);

		for (Password pw : data.passwords) {
			passwordVBox.add(createPasswordItem(pw));
			passwordVBox.add(Box.createVerticalStrut(5));
		}

		// This listener is used to refresh the page content whenever the user opens the
		// page
		passwordsPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				refreshPasswordVBox();
			}
		});

		return passwordsPanel;
	}
	
	/**
	 * creates a visual masterkey Panel which displays a list of all masterKeys of the user.
	 * @return JPanel object of the created panel
	 */
	private JPanel createMasterkeysPanel() {
		JPanel masterkeysPanel = new JPanel();
		masterkeysPanel.setLayout(new BoxLayout(masterkeysPanel, BoxLayout.X_AXIS));

		JScrollPane scrollPane_2 = new JScrollPane();
		masterkeysPanel.add(scrollPane_2);

		JPanel panel_1 = new JPanel();
		scrollPane_2.setViewportView(panel_1);

		masterkeysVBox = Box.createVerticalBox();
		panel_1.add(masterkeysVBox);

		for (MasterKey mKey : data.masterKeys) {
			masterkeysVBox.add(createMasterkeyItem(mKey));
			masterkeysVBox.add(Box.createVerticalStrut(5));
		}

		// This listener is used to refresh the page content whenever the user opens the
		// page
		masterkeysPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				refreshMasterkeysVBox();
			}
		});

		return masterkeysPanel;
	}
	
	/**
	 * refreshes the password list on the GUI
	 */
	public void refreshPasswordVBox() {
		if (passwordVBox == null) {
			return;
		} else {
			data = SaveSystem.getInstance().loadUserData();

			passwordVBox.removeAll();

			for (Password pw : data.passwords) {
				passwordVBox.add(createPasswordItem(pw));
				passwordVBox.add(Box.createVerticalStrut(5));
			}
		}
		frame.repaint();
		frame.revalidate();
	}

	/**
	 * refreshes the masterkey list on the GUI
	 */
	public void refreshMasterkeysVBox() {
		if (masterkeysVBox == null) {
			return;
		} else {
			data = SaveSystem.getInstance().loadUserData();

			masterkeysVBox.removeAll();

			for (MasterKey mKey : data.masterKeys) {
				masterkeysVBox.add(createMasterkeyItem(mKey));
				masterkeysVBox.add(Box.createVerticalStrut(5));
			}

		}
		frame.repaint();
		frame.revalidate();
	}
	
	/**
	 * creates a visual Add password Panel where the user can add new passwords.
	 * @return JPanel object of the created panel
	 */
	private JPanel createAddPwPanel() {
		JPanel AddPasswordPanel = new JPanel();
		JLabel lblNewLabel = new JLabel("Add a new password");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(214, 32, 156, 22);
		AddPasswordPanel.add(lblNewLabel);

		JComboBox<String> MasterKeyDD = new JComboBox<String>();
		for (MasterKey mKey : data.masterKeys) {
			MasterKeyDD.addItem(mKey.title);
		}

		AddPasswordPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				MasterKeyDD.removeAllItems();

				data = SaveSystem.getInstance().loadUserData();

				for (MasterKey mKey : data.masterKeys) {
					MasterKeyDD.addItem(mKey.title);
				}
			}
		});

		MasterKeyDD.setBounds(214, 74, 156, 22);
		AddPasswordPanel.add(MasterKeyDD);

		JLabel lblNewLabel_1 = new JLabel("Masterkey");
		lblNewLabel_1.setBounds(140, 78, 83, 14);
		AddPasswordPanel.add(lblNewLabel_1);

		mKeyPwField = new JPasswordField();
		mKeyPwField.setBounds(214, 123, 156, 20);
		AddPasswordPanel.add(mKeyPwField);

		JLabel lblNewLabel_2 = new JLabel("masterkey password");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBounds(61, 125, 144, 17);
		AddPasswordPanel.add(lblNewLabel_2);

		titleField = new JTextField();
		titleField.setBounds(214, 196, 156, 20);
		AddPasswordPanel.add(titleField);
		titleField.setColumns(10);

		JLabel lblNewLabel_4 = new JLabel("title");
		lblNewLabel_4.setBounds(141, 199, 46, 14);
		AddPasswordPanel.add(lblNewLabel_4);

		pwField = new JPasswordField();
		pwField.setBounds(214, 265, 156, 22);
		AddPasswordPanel.add(pwField);

		JCheckBox autoGenPwToggle = new JCheckBox("auto generate password");
		autoGenPwToggle.setHorizontalAlignment(SwingConstants.CENTER);
		autoGenPwToggle.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				pwField.setText("");
				if (e.getStateChange() == ItemEvent.SELECTED) {
					pwField.setEnabled(false);
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					pwField.setEnabled(true);
				}
			}
		});

		autoGenPwToggle.setBounds(203, 235, 178, 23);
		AddPasswordPanel.add(autoGenPwToggle);

		JLabel lblNewLabel_3 = new JLabel("password");
		lblNewLabel_3.setBounds(141, 269, 46, 14);
		AddPasswordPanel.add(lblNewLabel_3);

		JLabel resultLbl = new JLabel("result");
		resultLbl.setHorizontalAlignment(SwingConstants.CENTER);
		resultLbl.setBounds(73, 394, 440, 14);
		AddPasswordPanel.add(resultLbl);
		resultLbl.setVisible(false);
		
		JButton addBtn = new JButton("submit");
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resultLbl.setVisible(true);
				
				String hashedKey = MasterKey.sha256(new String(mKeyPwField.getPassword()));

				Boolean found = false;
				int masterKeyId = -1;
				for (MasterKey master : data.masterKeys) {
					if (master.title.equals(MasterKeyDD.getSelectedItem()) && master.getHashedKey().equals(hashedKey)) {
						masterKeyId = master.getId();
						found = true;
					}
				}
				try {
					if (!found || masterKeyId == -1) {
						resultLbl.setText("Invalid Masterkey password.");
					} else if (Manager.passwordTitleExistsForMasterkey(titleField.getText(), masterKeyId)) {
						// don't add the password if one with the same title does already exist
						resultLbl.setText("A password with the title '" + titleField.getText()
								+ "' does already exist for the Masterkey with the id '" + Integer.toString(masterKeyId)
								+ "'");
					} else if (new String(pwField.getPassword()).replaceAll(" ", "").equals("")
							&& !autoGenPwToggle.isSelected()) {
						resultLbl.setText("ERROR: password field empty.");
					} else {
						Password pw = null;
						if (autoGenPwToggle.isSelected()) {
							pw = new AutoPassword(masterKeyId, new String(mKeyPwField.getPassword()),
									titleField.getText());
						} else if (!new String(pwField.getPassword()).replaceAll(" ", "").equals("")) {
							pw = new Password(masterKeyId, new String(mKeyPwField.getPassword()), titleField.getText(),
									new String(pwField.getPassword()));
						}

						data.passwords.add(pw);
						SaveSystem.getInstance().saveUserData(data);
						resultLbl.setText("Successfully added '" + titleField.getText() + "' to "
								+ MasterKeyDD.getSelectedItem() + ".");
						mKeyPwField.setText("");
						titleField.setText("");
						autoGenPwToggle.setSelected(false);
						pwField.setText("");
					}

				} catch (Exception e2) {
					resultLbl.setText(e2.getMessage());
					e2.printStackTrace();
				}
			}
		});
		addBtn.setBounds(251, 360, 89, 23);
		AddPasswordPanel.add(addBtn);

		AddPasswordPanel.setLayout(null);
		
		AddPasswordPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				resultLbl.setVisible(false);
				mKeyPwField.setText("");
				titleField.setText("");
				autoGenPwToggle.setSelected(false);
				pwField.setText("");
			}
		});

		return AddPasswordPanel;
	}

	/**
	 * creates a visual Add masterkey Panel where the user can add new masterkeys.
	 * @return JPanel object of the created panel
	 */
	private JPanel CreateAddMasterKeyPanel() {
		JPanel AddMasterKeyPanel = new JPanel();
		AddMasterKeyPanel.setLayout(null);

		mKeyTitleField = new JTextField();
		mKeyTitleField.setBounds(224, 87, 130, 20);
		AddMasterKeyPanel.add(mKeyTitleField);
		mKeyTitleField.setColumns(10);

		JLabel lblNewLabel_5 = new JLabel("title");
		lblNewLabel_5.setHorizontalAlignment(SwingConstants.TRAILING);
		lblNewLabel_5.setBounds(160, 93, 46, 14);
		AddMasterKeyPanel.add(lblNewLabel_5);

		passwordField = new JPasswordField();
		passwordField.setBounds(224, 168, 130, 20);
		AddMasterKeyPanel.add(passwordField);

		repeatPasswordField = new JPasswordField();
		repeatPasswordField.setBounds(224, 214, 130, 20);
		AddMasterKeyPanel.add(repeatPasswordField);

		JLabel lblNewLabel_6 = new JLabel("password");
		lblNewLabel_6.setHorizontalAlignment(SwingConstants.TRAILING);
		lblNewLabel_6.setBounds(99, 171, 107, 14);
		AddMasterKeyPanel.add(lblNewLabel_6);

		JLabel lblNewLabel_7 = new JLabel("repeat password");
		lblNewLabel_7.setHorizontalAlignment(SwingConstants.TRAILING);
		lblNewLabel_7.setBounds(57, 217, 149, 14);
		AddMasterKeyPanel.add(lblNewLabel_7);

		JLabel resLbl = new JLabel("result ");
		resLbl.setHorizontalAlignment(SwingConstants.CENTER);
		resLbl.setBounds(57, 394, 467, 14);
		AddMasterKeyPanel.add(resLbl);
		resLbl.setVisible(false);
		
		JButton submitBtn = new JButton("submit");
		submitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resLbl.setVisible(true);
				if (mKeyTitleField.getText().replaceAll(" ", "").equals("")) {
					resLbl.setText("Invalid masterkey title.");
				} else if (new String(passwordField.getPassword()).equals("")) {
					resLbl.setText("Invalid password.");
				} else if (!new String(passwordField.getPassword())
						.equals(new String(repeatPasswordField.getPassword()))) {
					resLbl.setText("Entered passwords don't match.");
				} else {
					Manager.createMasterKey(mKeyTitleField.getText(), new String(passwordField.getPassword()));
					resLbl.setText("The masterkey '" + mKeyTitleField.getText() + "' has successfully been added.");

					mKeyTitleField.setText("");
					passwordField.setText("");
					repeatPasswordField.setText("");
				}
			}
		});
		submitBtn.setBounds(249, 360, 89, 23);
		AddMasterKeyPanel.add(submitBtn);

		JLabel GenPwResLbl = new JLabel("password res");
		GenPwResLbl.setHorizontalAlignment(SwingConstants.CENTER);
		GenPwResLbl.setBounds(397, 121, 164, 14);
		AddMasterKeyPanel.add(GenPwResLbl);
		GenPwResLbl.setVisible(false);

		JButton copyToClipboardBtn = new JButton("copy to clipboard");
		copyToClipboardBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringSelection stringSelection = new StringSelection(masterKeyRandomGeneratedPw);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);
			}
		});
		copyToClipboardBtn.setBounds(412, 146, 149, 23);
		AddMasterKeyPanel.add(copyToClipboardBtn);
		copyToClipboardBtn.setVisible(false);

		JButton generatePasswordBtn = new JButton("generate password");
		generatePasswordBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				masterKeyRandomGeneratedPw = new RandomString(16).toString();
				GenPwResLbl.setText(masterKeyRandomGeneratedPw);
				GenPwResLbl.setVisible(true);
				copyToClipboardBtn.setVisible(true);

			}
		});
		generatePasswordBtn.setBounds(412, 87, 149, 23);
		AddMasterKeyPanel.add(generatePasswordBtn);
		
		JLabel lblNewLabel_8 = new JLabel("Add a new masterkey ");
		lblNewLabel_8.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_8.setBounds(224, 42, 130, 14);
		AddMasterKeyPanel.add(lblNewLabel_8);
		
		
		AddMasterKeyPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				resLbl.setVisible(false);
				
				mKeyTitleField.setText("");
				passwordField.setText("");
				repeatPasswordField.setText("");
				
				GenPwResLbl.setVisible(false);
				copyToClipboardBtn.setVisible(false);
			}
		});

		return AddMasterKeyPanel;
	}

	/**
	 * creates an HTML String with a CSS String
	 * 
	 * @param text the text to represent with the provided css
	 * @param css  the css to use
	 * @return the HTML result String
	 */
	public String CSS(String text, String css) {
		//source: https://stackoverflow.com/questions/35372288/use-css-in-java-applications
		String start = "<html><body style = '" + css + "'>";
		String end = "</body></html>";

		return start + text + end;
	}
}
