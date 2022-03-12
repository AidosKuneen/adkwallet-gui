package com.aidoskuneen.wallet;

import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.GridLayout;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class JScreenSend extends JPanel {
	private JTextField tAddressFrom;
	private JTextField tAvailableADK;
	private JTextField txtSendTo;
	private JTextField tAmount;

	/**
	 * Create the panel.
	 */
	
	boolean useGas = false;
	synchronized void UpdateAvailable() {
		if (useGas && rdbtnUseGas.isSelected()) return;
		if (!useGas && !rdbtnUseGas.isSelected()) return;
		
		if (rdbtnUseGas.isSelected()) {
			availableWei_minusGas = availableWei.subtract(gas);
			if (availableWei_minusGas.compareTo(BigInteger.ZERO) < 0) {
				availableWei_minusGas = BigInteger.ZERO;
			}
			gasCostWEI = gas;
			useGas = true;
		} else {
			availableWei_minusGas = availableWei; // pow no gas
			gasCostWEI = BigInteger.ZERO;
			useGas = false;
		}
		tAvailableADK.setText(JScreenWallet.ConvertWeiToADK(availableWei_minusGas+"")+" ADK");
		lblfeeInfo.setText("ADK at address: "+JScreenWallet.ConvertWeiToADK(availableWei+"")+" ADK | Transaction fee: "+JScreenWallet.ConvertWeiToADK(gasCostWEI+"")+" ADK" );
		//tAmount.setText("");
		invalidate();
		this.repaint();
	}
	
	BigInteger availableWei = BigInteger.ZERO;
	BigInteger availableWei_minusGas = BigInteger.ZERO;
	BigInteger gas = new BigInteger("21000000000000000");
	BigInteger gasCostWEI  = BigInteger.ZERO;
	
	private JRadioButton rdbtnUseGas;
	private JLabel lblfeeInfo;
	public JScreenSend(String sendingFrom, String availableADKinWEI) {
		setLayout(new BorderLayout(0, 0));
		availableWei = new BigInteger(availableADKinWEI);
		availableWei_minusGas = new BigInteger(availableADKinWEI);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(new Color(34, 118, 72));
		panel.add(panel_4, BorderLayout.NORTH);
		panel_4.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblSending = new JLabel("Sending ADK");
		lblSending.setHorizontalAlignment(SwingConstants.CENTER);
		lblSending.setForeground(Color.WHITE);
		lblSending.setFont(new Font("Arial", Font.BOLD, 16));
		panel_4.add(lblSending);
		
		JPanel panel_center = new JPanel();
		panel.add(panel_center, BorderLayout.CENTER);
		panel_center.setLayout(new GridLayout(0, 1, 0, 0));
		panel_center.setBorder(BorderFactory.createEmptyBorder(10,30, 10, 30));
		
		JLabel lblNewLabel = new JLabel("Sending FROM:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		panel_center.add(lblNewLabel);
		
		tAddressFrom = new JTextField();
		tAddressFrom.setFont(new Font("Tahoma", Font.PLAIN, 15));
		tAddressFrom.setEditable(false);
		tAddressFrom.setBackground(new Color(255,255,200));
		panel_center.add(tAddressFrom);
		tAddressFrom.setColumns(10);
		tAddressFrom.setText(sendingFrom);
		
		JLabel lblNewLabel_1 = new JLabel("Available ADK:");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.LEFT);
		panel_center.add(lblNewLabel_1);
		
		tAvailableADK = new JTextField();
		tAvailableADK.setFont(new Font("Tahoma", Font.BOLD, 15));
		tAvailableADK.setEditable(false);
		tAvailableADK.setBackground(new Color(255,255,200));
		panel_center.add(tAvailableADK);
		tAvailableADK.setColumns(10);
		tAvailableADK.setText(JScreenWallet.ConvertWeiToADK(availableADKinWEI)+" ADK");
		
		lblfeeInfo = new JLabel("fee info");
		lblfeeInfo.setForeground(Color.GRAY);
		panel_center.add(lblfeeInfo);
		
		JLabel lblNewLabel_2 = new JLabel("Send To (0x address / Receiver):");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.LEFT);
		panel_center.add(lblNewLabel_2);
		
		txtSendTo = new JTextField();
		txtSendTo.setFont(new Font("Tahoma", Font.PLAIN, 15));
		panel_center.add(txtSendTo);
		txtSendTo.setColumns(10);
		
		JLabel lblNewLabel_3 = new JLabel("ADK Amount to Send:");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.LEFT);
		panel_center.add(lblNewLabel_3);
		
		JPanel panel_send = new JPanel();
		panel_center.add(panel_send);
		panel_send.setLayout(new BorderLayout(0, 0));
		
		tAmount = new JTextField();
		panel_send.add(tAmount, BorderLayout.CENTER);
		tAmount.setFont(new Font("Tahoma", Font.PLAIN, 15));
		tAmount.setColumns(10);
		
		JButton btnMaxButton = new JButton("(max)");
		btnMaxButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tAmount.setText(JScreenWallet.ConvertWeiToADK(availableWei_minusGas+""));
			}
		});
		panel_send.add(btnMaxButton, BorderLayout.EAST);
		
		
		
		rdbtnUseGas = new JRadioButton("Pay GAS (instant, 0.021 ADK fee)");
		rdbtnUseGas.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				UpdateAvailable();
			}
		});
		panel_center.add(rdbtnUseGas);
		rdbtnUseGas.setSelected(false);
		
		JRadioButton rdbtnDoPOW = new JRadioButton("Do Proof of Work ( 0 ADK fee )");
		rdbtnDoPOW.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				UpdateAvailable();
			}
		});
		panel_center.add(rdbtnDoPOW);
		rdbtnUseGas.setSelected(true);
		
		ButtonGroup group = new ButtonGroup();
	    group.add(rdbtnDoPOW);
	    group.add(rdbtnUseGas);
		
		JPanel panel_3 = new JPanel();
		panel_center.add(panel_3);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(BorderFactory.createEmptyBorder(0,30, 0, 30));
		panel_center.add(panel_1);
		panel_1.setLayout(new GridLayout(0, 3, 0, 0));
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.DoAction("CANCELSEND", JScreenSend.this, null);
			}
		});
		panel_1.add(btnCancel);
		
		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2);
	    
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// checks
				BigDecimal sendADK = BigDecimal.ZERO;
				String destinationAddress = "";
				try {
					sendADK = new BigDecimal(tAmount.getText().toUpperCase().replace("ADK", "").trim());
					tAmount.setText(sendADK.toPlainString());
					BigDecimal d18 = new BigDecimal("1000000000000000000");
					BigInteger sendADKinWEI = sendADK.multiply(d18).toBigInteger();
					if (sendADKinWEI.compareTo(BigInteger.ZERO) < 0  ||
							sendADKinWEI.compareTo(availableWei_minusGas) > 0 
							) {
						Flow.ShowErrorMessage("The amount entered is not between "+
								JScreenWallet.ConvertWeiToADK(BigInteger.ZERO+"")+" ADK and "+
								JScreenWallet.ConvertWeiToADK(availableWei_minusGas+"")+ " ADK");
						return;
					}
				} catch (Exception e1) {
					Flow.ShowErrorMessage("Invalid amount entered.");
					return;
				}
				
				try {
					destinationAddress = txtSendTo.getText().trim();
					if (!destinationAddress.matches("^0x[a-fA-F0-9]{40}$")){
						Flow.ShowErrorMessage("The destination address "+destinationAddress+" is not a valid ADK address (0x format)");
						return;
					}
					
				} catch (Exception e1) {
					Flow.ShowErrorMessage("Invalid destination address (0x format) entered.");
					return;
				}
				//
				// one last check
				if (useGas && sendADK.compareTo(BigDecimal.ZERO)==0) {
					Flow.ShowErrorMessage("You can only send a ZERO value transaction with PoW.");
					return;
				}
				// if we get here all is ok
				
				Flow.DoAction("DOSEND", JScreenSend.this, new String[] {
						useGas?"gas":"pow", // gas
						sendingFrom.trim(),
						destinationAddress,
						sendADK.toPlainString()
				});
				
			}
			
		});
		panel_1.add(btnSend);
		
		JPanel panel_5 = new JPanel();
		panel.add(panel_5, BorderLayout.EAST);
		panel_5.setLayout(new BorderLayout(0, 0));
		
		JTextArea txtrSendingAdkSending = new JTextArea();
		txtrSendingAdkSending.setFont(new Font("Arial", Font.PLAIN, 13));
		txtrSendingAdkSending.setEnabled(false);
		txtrSendingAdkSending.setEditable(false);
		txtrSendingAdkSending.setLineWrap(true);
		txtrSendingAdkSending.setWrapStyleWord(true);
		txtrSendingAdkSending.setText("Sending ADK:\r\n\r\nSending ADK with Proof of Work will be free, but it may take up to 2 minutes of local PoW.\r\n\r\nIf you want to send instant transactions, use the \"Pay GAS\" option.");
		txtrSendingAdkSending.setColumns(20);
		txtrSendingAdkSending.setBorder(BorderFactory.createEmptyBorder(10,10, 10, 10));
		JScrollPane scrollPane = new JScrollPane(txtrSendingAdkSending);
		panel_5.add(scrollPane);
		
		UpdateAvailable();
	}

	public JRadioButton getRdbtnUseGas() {
		return rdbtnUseGas;
	}
	public JLabel getLblfeeInfo() {
		return lblfeeInfo;
	}
	public JTextField getTxtSendTo() {
		return txtSendTo;
	}
}
