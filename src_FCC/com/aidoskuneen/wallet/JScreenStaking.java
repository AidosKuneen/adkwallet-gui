package com.aidoskuneen.wallet;

import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class JScreenStaking extends JPanel {
	private JTable table;
	//private JTable table;

	/**
	 * Create the panel.
	 */
	
	static Font tableDefaultFont = new Font("Courier New", Font.PLAIN, 15);
	private JButton btnStake;
	private JButton btnUnstakeAdk;
	
	public JScreenStaking(String data[][]) {
		setLayout(new BorderLayout(0, 0));
		
		String headers[] = { "Id", "Address", " ADK Staked ", " ADK Available ", " Locked until Milestone " , " Current Milestone " };
        
        String data_withTotal[][] = new String[data.length+1][headers.length];
        
        BigInteger btotal = BigInteger.ZERO;
        BigInteger btotal2 = BigInteger.ZERO;
        for (int row = 0; row < data.length; row++) {
        	for (int col = 0; col < headers.length; col++) {
        		data_withTotal[row+1][col] = data[row][col];
        		if (col==2) {
        			btotal = btotal.add(new BigInteger(data[row][col]));
        			btotal2 = btotal2.add(new BigInteger(data[row][col+1]));
        		}
            }
        }
        data_withTotal[0][0] = "";
        data_withTotal[0][1] = "Staked ADK (shown)";
        data_withTotal[0][2] = btotal.toString(10);
        data_withTotal[0][3] = btotal2.toString(10);
        data_withTotal[0][4] = "";
        data_withTotal[0][5] = "";
        
        
		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_left = new JPanel();
		panel.add(panel_left, BorderLayout.WEST);
		panel_left.setLayout(new GridLayout(0, 1, 0, 0));
//		//btnRefresh.setBorderPainted(false);
//		//btnRefresh.setFocusPainted(false);
//		btnRefresh.setContentAreaFilled(t);
//		btnRefresh.setBackground(JScreenMainFrame.AidosGreen);
		
		JLabel lblNewLabel = new JLabel("<html>Staking<br>actions</html>");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel_left.add(lblNewLabel);
		
		panel_left.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		btnStake = new JButton("Stake");
		btnStake.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow() > 0) {
					String address = table.getValueAt(table.getSelectedRow(),1).toString();
					String available_amount = table.getValueAt(table.getSelectedRow(),3).toString();
					if (address.startsWith("0x")) {
						Flow.DoAction("STAKESEND", JScreenStaking.this, new String[] {address, available_amount});
					}
				}
			}
		});
		panel_left.add(btnStake);
		
		btnUnstakeAdk = new JButton("Unstake");
		btnUnstakeAdk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow() > 0) {
					String address = table.getValueAt(table.getSelectedRow(),1).toString();
					String available_amount = table.getValueAt(table.getSelectedRow(),2).toString();
					String available_amount_for_fee = table.getValueAt(table.getSelectedRow(),3).toString();
					
					String lockedUntil = table.getValueAt(table.getSelectedRow(),4).toString();
					String currentBlock = table.getValueAt(table.getSelectedRow(),5).toString();
					
					BigInteger b_available_amount_for_fee = new BigInteger(available_amount_for_fee);
					BigInteger gas = new BigInteger("210000000000000000");  // extra factor 10 for staking
					
					if (available_amount.equals("0") || available_amount.equals("0.0") ) {
						JOptionPane.showMessageDialog(JScreenStaking.this, "No staked balance for this address: Nothing to unstake.");
						return;
					}

					long lLockedUntil = Long.parseLong(lockedUntil);
					long lcurrentBlock = Long.parseLong(currentBlock);
					
					if (lcurrentBlock < lLockedUntil) {
						JOptionPane.showMessageDialog(JScreenStaking.this, "Cannot unstake yet.\n You have to wait for another "+(lLockedUntil-lcurrentBlock)+" milestones.","Error",JOptionPane.ERROR_MESSAGE);
						return;
					}

					if (b_available_amount_for_fee.compareTo(gas) < 0 ) {
						JOptionPane.showMessageDialog(JScreenStaking.this, "You need to have at least 0.21 ADK of unstaked ADK to pay for the unstaking transaction fees.");
						return;
					}

					
					if (address.startsWith("0x")) {
						Flow.DoAction("UNSTAKESEND", JScreenStaking.this, new String[] {address, available_amount});
					}
				}
			}
		});
		panel_left.add(btnUnstakeAdk);
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.DoAction("REFRESHSTAKING", JScreenStaking.this, new String[] {});
			}
		});
		panel_left.add(btnRefresh);
		
		JButton btnBack = new JButton("<html>Back to<br>Wallet</html>");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.DoAction("REFRESH", JScreenStaking.this, new String[] {});
			}
		});
		panel_left.add(btnBack);
		
		JPanel panel_0 = new JPanel();
		//tabbedPane.addTab("Wallet", null, panel_0, null);
		panel_0.setLayout(new BorderLayout(0, 0));
		panel.add(panel_0);
		//table = new JTable();
		table = new JTable(){
		    @Override
		       public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		           Component component = super.prepareRenderer(renderer, row, column);
		           int rendererWidth = component.getPreferredSize().width;
		           TableColumn tableColumn = getColumnModel().getColumn(column);
		           tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
		           return component;
		        }
		    };
		//table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		final TableModel tm = new MyTableModelV2(data_withTotal, headers);
		table.setModel(tm);
		CustomRendererV2 cr = new CustomRendererV2 ();
		table.setDefaultRenderer(Object.class, cr);
		table.setRowHeight(table.getRowHeight()*2);
		table.getColumnModel().getColumn(0).setMaxWidth(50);
		
		//table.getColumnModel().getColumn(1).setMinWidth(400);
		table.setFont(tableDefaultFont);
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse) {
		        updateButtons();
		    }
		});
		
		table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()==2) {
                	try {
						String val = (String) tm.getValueAt(table.getSelectedRow(), table.getSelectedColumn());
						if (table.getSelectedColumn() == 2) {
							val = ConvertWeiToADK(val);
						}
						StringSelection stringSelection = new StringSelection(val);
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						clipboard.setContents(stringSelection, null);
						JOptionPane.showMessageDialog(Flow.mainFrame, "Copied to clipboard:\n\n"+val);
					} catch (Exception e1) {
						//e1.printStackTrace();
					}
                }
                updateButtons();
            }
        });
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel_0.add(scrollPane, BorderLayout.CENTER);
		
		JPanel panel_1 = new JPanel();
		panel_0.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_2 = new JPanel();
		panel_0.add(panel_2, BorderLayout.NORTH);
		
		
		JPanel panel_4 = new JPanel();
		panel_4.setBackground(new Color(99,66,00));
		panel.add(panel_4, BorderLayout.NORTH);
		
		JLabel lbltitle = new JLabel("ADK Staking");
		lbltitle.setForeground(Color.WHITE);
		lbltitle.setHorizontalAlignment(SwingConstants.CENTER);
		lbltitle.setFont(new Font("Arial", Font.BOLD, 16));
		panel_4.add(lbltitle);
		updateButtons();
	 }

	static boolean NumbersOnly(String check) {
		if (check==null || check.equals("")) return false;
		for (char ch : check.toCharArray()) {
			if (ch < '0' || ch > '9') return false;
		}
		return true;
	}
	
	public static String ConvertWeiToADK ( String wei ) {
		String wei_working = wei;
		if (!NumbersOnly(wei)) {
			wei_working = "0";
		} 
		BigDecimal f = new BigDecimal(wei_working);
		BigDecimal f18 = new BigDecimal("0.000000000000000001");
		BigDecimal adk = f.multiply(f18);
		String adkstr = adk.toPlainString();
		if (!adkstr.contains(".")) adkstr += ".0";
		while (true) {
			char lastchar = adkstr.charAt(adkstr.length()-1);
			char secondlastchar = adkstr.charAt(adkstr.length()-2);
			if (lastchar >= '1' && lastchar <= '9') break;
			if (lastchar == '0' && secondlastchar != '.') {
				adkstr = adkstr.substring(0,adkstr.length()-1);
				continue;
			}
			break;
		}
		return adkstr;
	}
//	
//	public JButton getBtnSend() {
//		return btnSend;
//	}
//
	void updateButtons() {
		    if (table.getSelectedRow()>0) {
            	this.getBtnStake().setEnabled(true);
            	this.getBtnStake().setText("Stake ADK");
            	this.getBtnUnstakeAdk().setEnabled(true);
            	this.getBtnUnstakeAdk().setText("Unstake ADK");
            } else {
            	this.getBtnStake().setEnabled(false);
            	this.getBtnStake().setText("<html>Stake ADK<br><i>no address<br>selected</i>)</html>");
            	this.getBtnUnstakeAdk().setEnabled(false);
            	this.getBtnUnstakeAdk().setText("<html>Unstake ADK<br><i>no address<br>selected</i>)</html>");
            	
            }
    }
//	public JButton getMigrateBtn() {
//		return migrateBtn;
//	}
	public JButton getBtnStake() {
		return btnStake;
	}
	public JButton getBtnUnstakeAdk() {
		return btnUnstakeAdk;
	}
}

class MyTableModelV2 extends DefaultTableModel {

	   public MyTableModelV2(Object[][] tableData, Object[] colNames) {
	      super(tableData, colNames);
	   }

	   public boolean isCellEditable(int row, int column) {
	      return false;
	   }
}


class CustomRendererV2 extends DefaultTableCellRenderer 
{

	
	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
		String newValue = (String)value;
        if (column == 2) {
        	// show value as ADK
        	newValue = JScreenStaking.ConvertWeiToADK((String)newValue);
        	int indexofDot = newValue.toString().indexOf('.');
        	while (indexofDot != -1 && indexofDot < 8 ) {
        		newValue = " "+newValue.toString();
        		indexofDot = newValue.toString().indexOf('.');
        	}
        }
        if (column == 3) {
        	// show value as ADK
        	newValue = JScreenStaking.ConvertWeiToADK((String)newValue);
        	int indexofDot = newValue.toString().indexOf('.');
        	while (indexofDot != -1 && indexofDot < 8 ) {
        		newValue = " "+newValue.toString();
        		indexofDot = newValue.toString().indexOf('.');
        	}
        }
        newValue = " "+newValue+" ";
        Component c = super.getTableCellRendererComponent(table, newValue, isSelected, hasFocus, row, column);
        if (c instanceof CustomRendererV2) {
        	CustomRendererV2 cr = (CustomRendererV2) c;
        	
        	// default 
        	cr.setForeground(Color.BLACK);
        	cr.setBackground(Color.WHITE);
        	cr.setFont(JScreenStaking.tableDefaultFont);
        	
        	if (column == 2 && ((String)value).equals("0")) {
        		cr.setForeground(Color.GRAY);
        		cr.setFont(new Font(cr.getFont().getFontName(), Font.ITALIC, cr.getFont().getSize()));
        	}
        	
        	if (column == 0)
        		cr.setHorizontalAlignment(cr.CENTER);
        	
        	if (row == 0) {
        		cr.setFont(new Font(cr.getFont().getFontName(), Font.BOLD, cr.getFont().getSize()+2));
        		cr.setBackground(new Color(169,179,211));
        	}
        	
        	if (row == table.getSelectedRow() && row > 0) {
        		cr.setBackground(new Color(255,255,211));
        	}
        	
        }
        
        
//        if (row == 1)
//          c.setBackground(new java.awt.Color(255, 72, 72));
//        else 
//        	c.setBackground(new java.awt.Color(255, 255, 255));
        
        return c;
    }
}

