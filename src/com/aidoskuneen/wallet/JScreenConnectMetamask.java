package com.aidoskuneen.wallet;

import javax.swing.JPanel;
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
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.JPasswordField;

public class JScreenConnectMetamask extends JPanel {
	private JPasswordField passwordFieldMeta;

	/**
	 * Create the panel.
	 */
	public JScreenConnectMetamask() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		
		panel_1.setLayout(new BorderLayout(0, 0));
		panel.add(panel_1, BorderLayout.CENTER);
		
		
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
		
		JLabel lblNewLabel = new JLabel("Enter Metamask Password:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		
		JButton btnMetaConnect = new JButton("Connect");
		btnMetaConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.DoAction("CONNECTMETA", JScreenConnectMetamask.this, new String[] {"(**)"+new String(passwordFieldMeta.getPassword())});
			}
		});
		
		JPanel panel_form = new JPanel();
		JPanel panel_buttons = new JPanel();
		panel_5.setLayout(new BorderLayout(0, 0));
		
		panel_5.add(panel_form, BorderLayout.CENTER);
		panel_5.add(panel_buttons, BorderLayout.SOUTH);
		panel_form.setLayout(new BorderLayout(0, 0));
		
		panel_form.add(lblNewLabel, BorderLayout.NORTH);
		
		JPanel panel_2 = new JPanel();
		panel_form.add(panel_2, BorderLayout.CENTER);
		
		
		passwordFieldMeta = new JPasswordField();
		panel_2.add(passwordFieldMeta);
		passwordFieldMeta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // Enter pressed
				Flow.DoAction("CONNECTMETA", JScreenConnectMetamask.this, new String[] {"(**)"+new String(passwordFieldMeta.getPassword())});
				
			}
		});
		passwordFieldMeta.setColumns(20);
		passwordFieldMeta.requestFocus();
		panel_buttons.add(btnMetaConnect);

		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(new Color(119,57,0)); // metamask color
		panel.add(panel_4, BorderLayout.NORTH);
		
		JLabel lbltitle = new JLabel("Connect to Metamask Wallet");
		lbltitle.setForeground(Color.WHITE);
		lbltitle.setHorizontalAlignment(SwingConstants.CENTER);
		lbltitle.setFont(new Font("Arial", Font.BOLD, 16));
		panel_4.add(lbltitle);
		
		
	}

	public JPasswordField getPasswordFieldMeta() {
		return passwordFieldMeta;
	}
}
