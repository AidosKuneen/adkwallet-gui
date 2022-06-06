package com.aidoskuneen.wallet;

import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JPasswordField;

public class JScreenNewWallet extends JPanel {
	private JPasswordField passwordFieldMeta;

	/**
	 * Create the panel.
	 */
	public JScreenNewWallet() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));
		
		panel.add(tabbedPane, BorderLayout.CENTER);
		JPanel panel_0 = new JPanel();
		tabbedPane.addTab("Info", null, panel_0, null);
		panel_0.setLayout(new BorderLayout(0, 0));
		
		JTextArea txtrPleaseSelectFrom = new JTextArea();
		
		txtrPleaseSelectFrom.setWrapStyleWord(true);
		txtrPleaseSelectFrom.setLineWrap(true);
		txtrPleaseSelectFrom.setText("Please select from the menu on the top:\r\n\r\n\"Import from Metamask\" lets you import an existing wallet from your local Metamask (Chrome/Edge/Firefox)\r\n\r\n\"Create new Wallet\" lets you create a brand new local wallet (stored in local encrypted wallet.json file)\r\n\r\n\"Recover from Seed/Mnemonic\" lets you recover an exiting wallet by entering your 12 or 24 word ADK seed/mnemonic words.");
		txtrPleaseSelectFrom.setFont(new Font("Arial", Font.PLAIN, 13));
		txtrPleaseSelectFrom.setEditable(false);
		txtrPleaseSelectFrom.setBorder(BorderFactory.createCompoundBorder(
				txtrPleaseSelectFrom.getBorder(), 
		        BorderFactory.createEmptyBorder(15, 15, 15, 15)));
		
		JScrollPane scrollPane = new JScrollPane(txtrPleaseSelectFrom);
		panel_0.add(scrollPane);
		panel_0.add(scrollPane, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		tabbedPane.addTab("Import from Metamask", null, panel_1, null);
		panel_1.setLayout(new BorderLayout(0, 0));
	
		
		/// icon
		BufferedImage logo;
		try {
			logo = ImageIO.read(new File("images/metamask.png"));
		} catch (IOException e) {
			try {
				logo = ImageIO.read(getClass().getResourceAsStream("/images/metamask.png"));
			} catch (IOException e1) {
				logo = null;
			}
				
		}
		
		JLabel lblMetaIcon = new JLabel("");
		lblMetaIcon.setHorizontalAlignment(SwingConstants.CENTER);
		lblMetaIcon.setIcon(new ImageIcon(logo));
		panel_1.add(lblMetaIcon, BorderLayout.NORTH);
		
		JPanel panel_5 = new JPanel();
		panel_5.setBackground(Color.WHITE);
		panel_1.add(panel_5, BorderLayout.CENTER);
		panel_5.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblNewLabel = new JLabel("Enter Metamask Password:");
		panel_5.add(lblNewLabel);
		
		passwordFieldMeta = new JPasswordField();
		passwordFieldMeta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // Enter pressed
				Flow.DoAction("IMPORTMETA", JScreenNewWallet.this, new String[] {"(**)"+new String(passwordFieldMeta.getPassword())});
				
			}
		});
		passwordFieldMeta.setColumns(20);
		panel_5.add(passwordFieldMeta);
		
		JButton btnMetaConnect = new JButton("Connect/Import");
		btnMetaConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.DoAction("IMPORTMETA", JScreenNewWallet.this, new String[] {"(**)"+new String(passwordFieldMeta.getPassword())});
			}
		});
		panel_5.add(btnMetaConnect);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Create new Wallet", null, panel_2, null);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel_1 = new JLabel("<html>&nbsp;<br>Create a NEW LOCAL Wallet<br>&nbsp;</html>");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Arial", Font.BOLD, 15));
		panel_2.add(lblNewLabel_1, BorderLayout.NORTH);
		
		JPanel panel_7 = new JPanel();
		
		JButton btnCreateBrandNewWallet = new JButton("Create a new local wallet (wallet.json)");
		btnCreateBrandNewWallet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.DoAction("CREATENEWWALLET", JScreenNewWallet.this, null);
			}
		});
		panel_7.add(btnCreateBrandNewWallet);
		
		panel_2.add(panel_7, BorderLayout.CENTER);
		
		/// icon
				BufferedImage logo2;
				try {
					logo2 = ImageIO.read(new File("images/wallet_sml.png"));
				} catch (IOException e) {
					try {
						logo2 = ImageIO.read(getClass().getResourceAsStream("/images/wallet_sml.png"));
					} catch (IOException e1) {
						logo2 = null;
					}
						
				}
		
		JLabel lblNewLabel_walletimg = new JLabel("");
		lblNewLabel_walletimg.setBackground(Color.WHITE);
		lblNewLabel_walletimg.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_walletimg.setIcon(new ImageIcon(logo2));
		
		JPanel panel_8 = new JPanel();
		panel_8.add(lblNewLabel_walletimg);
		
		panel_8.setBackground(Color.WHITE);
		panel_2.add(panel_8, BorderLayout.SOUTH);
		
		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("Recover from Seed/Mnemonic", null, panel_3, null);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel_1_1 = new JLabel("<html>&nbsp;<br>Recover Wallet from Seed/Mnemonic Words<br>&nbsp;</html>");
		lblNewLabel_1_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1_1.setFont(new Font("Arial", Font.BOLD, 15));
		panel_3.add(lblNewLabel_1_1, BorderLayout.NORTH);
		
		
		JPanel panel_6 = new JPanel();
		panel_3.add(panel_6, BorderLayout.CENTER);
		panel_6.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnRecover = new JButton("Recover Wallet");
		btnRecover.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.DoAction("RECOVERWALLET", JScreenNewWallet.this, null);
			}
		});
		panel_6.add(btnRecover);
		
		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(new Color(34,118,72));
		panel.add(panel_4, BorderLayout.NORTH);
		
		JLabel lbltitle = new JLabel("New Wallet");
		lbltitle.setForeground(Color.WHITE);
		lbltitle.setHorizontalAlignment(SwingConstants.CENTER);
		lbltitle.setFont(new Font("Arial", Font.BOLD, 16));
		panel_4.add(lbltitle);

		tabbedPane.addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent e) {
	        	if (selectedTab!=1 &&tabbedPane.getSelectedIndex()==1) {
	        		passwordFieldMeta.requestFocusInWindow();
	        	}
	        	selectedTab = tabbedPane.getSelectedIndex();
	        }
	    });
		
	}

	int selectedTab = -1;
	
	public JPasswordField getPasswordFieldMeta() {
		return passwordFieldMeta;
	}
}
