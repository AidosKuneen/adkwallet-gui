package com.aidoskuneen.wallet;

import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import java.awt.GridLayout;

public class JScreenOpen extends JPanel {

	/**
	 * Create the panel.
	 */
	public JScreenOpen() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JButton btnOpenLocal = new JButton("Open LOCAL Wallet");
		btnOpenLocal.setHorizontalAlignment(SwingConstants.RIGHT);
		btnOpenLocal.setFont(new Font("Arial", Font.PLAIN, 15));
		
		JButton btnOpenMetamask = new JButton("Connect to METAMASK");
		btnOpenMetamask.setHorizontalAlignment(SwingConstants.LEFT);
		btnOpenMetamask.setFont(new Font("Arial", Font.PLAIN, 15));
		
		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(Color.GRAY);
		panel.add(panel_4, BorderLayout.NORTH);
		
		JLabel lblLetsGetStarted = new JLabel("Please Choose:");
		lblLetsGetStarted.setHorizontalAlignment(SwingConstants.CENTER);
		lblLetsGetStarted.setForeground(Color.WHITE);
		lblLetsGetStarted.setFont(new Font("Arial", Font.BOLD, 16));
		panel_4.add(lblLetsGetStarted);
		
		JPanel panel_center = new JPanel();
		panel.add(panel_center, BorderLayout.CENTER);
		panel_center.setLayout(new GridLayout(0, 2, 0, 0));
		
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(BorderFactory.createEmptyBorder(10,10, 10, 10));
		panel_center.add(panel_1);
		//panel_1.add(btnOpenLocal);
		
		JPanel panel_2 = new JPanel();
		panel_center.add(panel_2);
		panel_2.setBorder(BorderFactory.createEmptyBorder(10,10, 10, 10));
		//panel_center.add(btnOpenMetamask);
		
		JPanel connectMeta = new JScreenConnectMetamask();
		panel_2.add(connectMeta);
		
		JPanel connectLocal = new JScreenConnectLocal();
		panel_1.add(connectLocal);
		
		
		btnOpenMetamask.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.DoAction("OPENMETA", JScreenOpen.this, null);
			}
		});
		
		btnOpenLocal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.DoAction("OPENLOCAL", JScreenOpen.this, null);
			}
		});

	}

}
