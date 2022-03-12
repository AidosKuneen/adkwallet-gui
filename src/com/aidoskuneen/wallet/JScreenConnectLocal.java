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

public class JScreenConnectLocal extends JPanel {

	/**
	 * Create the panel.
	 */
	public JScreenConnectLocal() {
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
			logo = ImageIO.read(new File("images/wallet_sml.png"));
		} catch (IOException e) {
			try {
				logo = ImageIO.read(getClass().getResourceAsStream("/images/wallet_sml.png"));
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
		
		
		JButton btnLocalOpen = new JButton("Open Local Wallet");
		btnLocalOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.DoAction("OPENLOCAL", JScreenConnectLocal.this, null);
			}
		});
		
		JPanel panel_form = new JPanel();
		JPanel panel_buttons = new JPanel();
		panel_5.setLayout(new BorderLayout(0, 0));
		
		panel_5.add(panel_form, BorderLayout.CENTER);
		
		JLabel lblNewLabel = new JLabel("<html><center>Use this option if you want to <br>open (or create) a LOCAL wallet</center></html>");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel_form.add(lblNewLabel);
		panel_5.add(panel_buttons, BorderLayout.SOUTH);
		panel_buttons.add(btnLocalOpen);

		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(new Color(34,118,72));
		panel.add(panel_4, BorderLayout.NORTH);
		
		JLabel lbltitle = new JLabel("Local Wallet (wallet.json)");
		lbltitle.setForeground(Color.WHITE);
		lbltitle.setHorizontalAlignment(SwingConstants.CENTER);
		lbltitle.setFont(new Font("Arial", Font.BOLD, 16));
		panel_4.add(lbltitle);
		
		new Thread(new Runnable() {
			@Override
				public void run() {
				    try {
						Thread.sleep(250);
					} catch (InterruptedException e) {}
					if (JScreenConnectLocal.this.isVisible() && ! btnLocalOpen.hasFocus())
						btnLocalOpen.requestFocus();				
				}}).start();
		
		
	}	
}
