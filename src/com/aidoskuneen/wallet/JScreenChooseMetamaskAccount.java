package com.aidoskuneen.wallet;

import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.ListSelectionModel;
import javax.swing.JList;

public class JScreenChooseMetamaskAccount extends JPanel {
	public JList<String> listAccounts;

	/**
	 * Create the panel.
	 */
	HashMap<String,String> mmList = null; 
	
	public JScreenChooseMetamaskAccount( HashMap<String,String> list, boolean useLocal, String password) {
		mmList = list;
		setLayout(new BorderLayout(0, 0));

		
		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnImportSelected = new JButton(useLocal?"Import Selected Account":"Connect to Selected Account");
		btnImportSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String account = listAccounts.getSelectedValue();
				if (account==null || !account.startsWith("0x")) {
					Flow.ShowErrorMessage("Please select a valid account to "+(useLocal?"import":"connect to"));
					return;
				} 
				account = account.split(" ")[0];
				if (useLocal) {
					Flow.DoAction("CREATEWALLET-MN", null, new String[] {account,list.get(account)});
				} else {
					Wallet.walletFilename = "tmplink_"+ Math.abs(account.hashCode())+".json";
					(new File (Wallet.walletFilename)).deleteOnExit();
						
					Flow.DoAction("CONNECTWALLET-MN", null, new String[] {account,list.get(account),password});
				}
			}
		});
		panel_1.add(btnImportSelected);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(new Color(34, 118, 72));
		add(panel_4, BorderLayout.NORTH);
		
		JLabel lblSelectMetamaskAccount = new JLabel("Select Account to "+(useLocal?"import":"connect to"));
		lblSelectMetamaskAccount.setHorizontalAlignment(SwingConstants.CENTER);
		lblSelectMetamaskAccount.setForeground(Color.WHITE);
		lblSelectMetamaskAccount.setFont(new Font("Arial", Font.BOLD, 16));
		panel_4.add(lblSelectMetamaskAccount);
		
		JPanel panelC = new JPanel();
		add(panelC, BorderLayout.CENTER);
		
		DefaultListModel<String> lmodel = new DefaultListModel<>();
		
		listAccounts = new JList<String>(lmodel);
		
		listAccounts.setFont(new Font("Courier New", Font.BOLD, 16));
		listAccounts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		panelC.setBorder(BorderFactory.createEmptyBorder(30,30, 30, 30));
		
		DefaultListCellRenderer renderer =  (DefaultListCellRenderer)listAccounts.getCellRenderer();  
		renderer.setHorizontalAlignment(JLabel.CENTER);  
		
		 for (String account : mmList.keySet()) {
			 lmodel.addElement(account + " and sub-accounts");
		}
		 panelC.setLayout(new BorderLayout(0, 0));
		
		 panelC.add(listAccounts);
		 
		 JLabel lblNewLabel = new JLabel("<html><b>List of Metamask Accounts found:</b><br>\r\n(only first account shown for each)<br>&nbsp;</html>");
		 lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		 panelC.add(lblNewLabel, BorderLayout.NORTH);
		
	}

	public JList getListAccounts() {
		return listAccounts;
	}
}
