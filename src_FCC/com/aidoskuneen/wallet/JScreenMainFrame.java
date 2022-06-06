package com.aidoskuneen.wallet;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class JScreenMainFrame extends JFrame {
    public static Color AidosGreen = new Color(34,118,72);
	private JPanel contentPane;
	public JTextField statusText;
	private JPanel panelCenter;
	public JTextArea textAreaLog;

	/**
	 * Create the frame.
	 */
	public JScreenMainFrame() {
		setTitle("Aidos Kuneen ADK Wallet [ ADK Mainnet v2.1 ]");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 827, 648);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnMainMenu = new JMenu("File");
		menuBar.add(mnMainMenu);
		
		JMenuItem mntmMainMenu_Exit = new JMenuItem("Exit");
		mntmMainMenu_Exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.DoAction("EXIT", null, null);
			}
		});
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Configuration");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				(new JConfig()).setVisible(true);
			}
		});
		mnMainMenu.add(mntmNewMenuItem);
		mnMainMenu.add(mntmMainMenu_Exit);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panelTop = new JPanel();
		panelTop.setBackground(new Color(34,118,72)); // aidos green
		
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
		
		contentPane.add(panelTop, BorderLayout.NORTH);
		panelTop.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel logoLabel = new JLabel("AidosKuneen");
		if (logo != null) {
			logoLabel.setText("");
		}
		logoLabel.setIcon(new ImageIcon(logo));
		panelTop.add(logoLabel);
		
		JLabel logoTitle = new JLabel("AidosKuneen Wallet ( ADK 2.1 )");
		logoTitle.setFont(new Font("Arial", Font.PLAIN, 20));
		logoTitle.setForeground(Color.WHITE);
		panelTop.add(logoTitle);
		
		panelCenter = new JPanel();
		contentPane.add(panelCenter, BorderLayout.CENTER);
		
		statusText = new JTextField();
		statusText.setBackground(Color.WHITE);
		statusText.setEditable(false);
		statusText.setColumns(80);
		
		JButton btnNewButton = new JButton("New button");
		panelCenter.add(btnNewButton);
		
		JPanel panelBottom = new JPanel();
		contentPane.add(panelBottom, BorderLayout.SOUTH);
		panelBottom.setLayout(new BorderLayout(0, 0));
		
		
		textAreaLog = new JTextArea();
		
		textAreaLog.setEditable(false);
		textAreaLog.getCaret().setVisible(true);
		textAreaLog.getCaret().setSelectionVisible(true);
		textAreaLog.setRows(5);
		
		JScrollPane scrollPaneOutput = new JScrollPane(textAreaLog);
		panelBottom.add(scrollPaneOutput, BorderLayout.NORTH);
		
		panelBottom.add(statusText, BorderLayout.SOUTH);
		
	}

	public JTextField getStatusText() {
		return statusText;
	}
	
	public JPanel getPanelCenter() {
		return panelCenter;
	}
	
	public static synchronized void replacePanelCenter(JPanel panelCenterNew) {
		if (Flow.mainFrame != null) {
			if (Flow.mainFrame.panelCenter != null) {
				Flow.mainFrame.contentPane.remove(Flow.mainFrame.panelCenter);
			}

			Flow.mainFrame.panelCenter = panelCenterNew;
			Flow.mainFrame.contentPane.add(panelCenterNew, BorderLayout.CENTER);
			Flow.mainFrame.invalidate();
			Flow.mainFrame.validate();
			Flow.mainFrame.repaint();
//			try{
//				SwingUtilities.updateComponentTreeUI(Flow.mainFrame);
//			} catch(Exception ex) {
//				//ex.printStackTrace();
//			}
		}
	}
	
	public JTextArea getTextAreaLog() {
		return textAreaLog;
	}
}
