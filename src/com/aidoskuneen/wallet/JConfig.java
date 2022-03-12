package com.aidoskuneen.wallet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class JConfig extends JDialog{

	public static String serverAPI = "http://api1.mainnet.aidoskuneen.com:9545";
	static String serverAPIDefault = serverAPI;
	private JTextField txtserverField;
	private static String config_filename = "wallet.config";
	private static File configFile = new File(config_filename);
	
	static void CheckConfigFile() {
		if (Flow.CLI!= null) {
			File parent = Flow.CLI.getParentFile();
			if (parent!=null) {
				configFile = new File(parent,config_filename);
			}
		}
		if (configFile==null) {
			configFile = new File(config_filename);
		}
	}
	
	static synchronized void LoadConfig() {
		
		Properties prop = new Properties();
		
		CheckConfigFile();
		
		try (FileInputStream fis = new FileInputStream(configFile)) {
		    prop.load(fis);
		    serverAPI = prop.getProperty("serverAPI", serverAPI);
		} catch (FileNotFoundException ex) {
			SaveConfig(); //create
		} catch (IOException ex) {
		    ex.printStackTrace();
		}
	}
	
	static synchronized void SaveConfig() {
		Properties prop = new Properties();
		
		CheckConfigFile();
		
		prop.setProperty("serverAPI", serverAPI);
		try (FileOutputStream fos = new FileOutputStream(configFile, false)) {
		    prop.store(fos,"ADK Wallet Properties");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public JConfig() {
		setTitle("Configuration");
		LoadConfig();
		
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		
		setModal(true);
		
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		BufferedImage logo;
		try {
			logo = ImageIO.read(new File("images/64x64.png"));
		} catch (IOException e) {
			try {
				logo = ImageIO.read(getClass().getResourceAsStream("/images/64x64.png"));
			} catch (IOException e1) {
				logo = null;
			}
				
		}
		
		this.setIconImage(new ImageIcon(logo).getImage());
		
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(10,10, 10, 10));
		getContentPane().add(panel, BorderLayout.NORTH);
		
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel lblNewLabel = new JLabel("Server URL");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("(e.g. "+serverAPIDefault+")");
		panel.add(lblNewLabel_1);
		
		txtserverField = new JTextField();
		panel.add(txtserverField);
		txtserverField.setColumns(10);
		txtserverField.setText(serverAPI);
		
		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JConfig.this.setVisible(false);
			}
		});
		panel_1.add(btnCancel);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newServerURL = txtserverField.getText().trim();
				if (!newServerURL.toLowerCase().startsWith("http")) {
					JOptionPane.showMessageDialog(JConfig.this, "Invalid server address (has to start with http/https).","Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					new URL(newServerURL);
				}
				catch(Exception ex) {
					JOptionPane.showMessageDialog(JConfig.this, "Invalid URL.","Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				serverAPI = newServerURL;
				SaveConfig();
				JOptionPane.showMessageDialog(JConfig.this, "Config saved.","Info", JOptionPane.INFORMATION_MESSAGE);
				JConfig.this.setVisible(false);
			}
		});
		panel_1.add(btnSave);

	}

	public JTextField getTxtserverField() {
		return txtserverField;
	}
}
