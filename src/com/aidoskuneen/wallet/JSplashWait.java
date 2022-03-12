package com.aidoskuneen.wallet;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.ActionEvent;

public class JSplashWait extends JDialog {

	private final JPanel contentPanel = new JPanel();

	private static boolean shouldfinish = false; 
	
	public static synchronized void StopWait() {
		shouldfinish = true;
	}
	
	static ArrayList<JSplashWait> allscreens = new  ArrayList<JSplashWait>();
	
	static Object sem = new Object();
	
	static String msg = "cli operation";
	
	public static void StartWait(String c) {
		msg = "running '"+c+"'...";
		shouldfinish = false;
		SplashMonitor();
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized(sem) {
					new JSplashWait(Flow.mainFrame);
				}
			}
		}).start();
	}
	
	private JSplashWait(JFrame frame) {
		synchronized(allscreens) {
			allscreens.add(this);
		}
		setModal(true);
		setBounds(100, 100, 240, 110);
		if (frame != null) {  // center on screen
			Rectangle r = frame.getBounds();
			r.x = frame.getX() + (frame.getWidth()/2) - 240/2;
			r.y = frame.getY() + (frame.getHeight()/2) - 110/2;
			setBounds(r.x, r.y, 240, 110);
		}
		getContentPane().setLayout(new BorderLayout());
		
		contentPanel.setBackground(new Color(255, 255, 195));
		
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		lblwaiting = new JLabel("please wait");
		lblwaiting.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblwaiting.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(lblwaiting, BorderLayout.CENTER);
		
		btnkill = new JButton("(long running job... kill?)");
		btnkill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 int result = JOptionPane.showConfirmDialog(JSplashWait.this,"Are you sure you want to kill the job and close the app?", "Kill Process?",
			               JOptionPane.YES_NO_OPTION,
			               JOptionPane.QUESTION_MESSAGE);
			            if(result == JOptionPane.YES_OPTION){
			            	shouldfinish = true;
			            	System.exit(1);
			            	try {
								Thread.sleep(2000);
							} catch (InterruptedException e1) {}
			            	Runtime.getRuntime().halt(1);
			            }
			}
		});
		btnkill.setVisible(false);
		contentPanel.add(btnkill, BorderLayout.SOUTH);
		
		lblop = new JLabel(msg);
		lblop.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(lblop, BorderLayout.NORTH);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setUndecorated(true);
		if (shouldfinish) {
			setVisible(false); // already finished?
		}
		else {
			new Thread(WaitJob).start();
			setVisible(true);
		}
		
		
	}
	
	Runnable WaitJob = new Runnable() {

		@Override
		public void run() {
			int r = 0; int rsign = 1;
			int g = 0; int gsign = 1;
			int b = 0; int bsign = 1;
			long startTime = System.currentTimeMillis();
			boolean showkill = false;
			Random rand = new Random();
			while (!shouldfinish) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {}
				r += rsign*rand.nextInt(10);
				g += gsign*rand.nextInt(20);
				b += bsign*rand.nextInt(30);
				if (r>255) { rsign = -1; r = 255; }
				if (r<0) { rsign = 1; r = 0; }
				if (g>255) { gsign = -1; g = 255; }
				if (g<0) { gsign = 1; g = 0; }
				if (b>255) { bsign = -1; b = 255; }
				if (b<0) { bsign = 1; b = 0; }
				
				lblwaiting.setForeground(new Color(r, g , b));
				lblwaiting.invalidate();
				//
				if (!showkill && startTime < System.currentTimeMillis() - 120000 ) {// running for > 2 min ?
					showkill = true;
					btnkill.setVisible(true);
				}
				
			}
			
			setVisible(false); // done
		}
		
	};
	private JLabel lblwaiting;
	private JButton btnkill;
	private JLabel lblop;

	public JLabel getLblwaiting() {
		return lblwaiting;
	}
	public JButton getBtnkill() {
		return btnkill;
	}
	
	static boolean splashMonitorRunning = false;
	
	static synchronized void SplashMonitor() { // extra safeguard
		if (splashMonitorRunning) return; //only one
		splashMonitorRunning = true;
		
		new Thread(new Runnable() {
             @Override
			public void run() {
            	try {
					while(true) {
						splashMonitorRunning = true;
						Thread.sleep(2000);
						if (!shouldfinish) continue;
						// closeall
						ArrayList<JSplashWait> temp = new ArrayList<JSplashWait>();
						synchronized(allscreens) {
							try {
								for (JSplashWait s : allscreens) {
									if (s.isActive() || s.isVisible()) {
										s.setVisible(false);
										s.dispose();
										temp.add(s);
									}
								}
								for (JSplashWait s : temp) {
									allscreens.remove(s);
								}
							} catch (Exception e) {
							}
						}
					}
				} catch (InterruptedException e) {}
			}}).start();;
	}
}
