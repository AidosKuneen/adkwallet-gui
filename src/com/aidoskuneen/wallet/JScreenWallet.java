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

public class JScreenWallet extends JPanel {
	private JTable table;
	//private JTable table;

	/**
	 * Create the panel.
	 */
	
	static Font tableDefaultFont = new Font("Courier New", Font.PLAIN, 15);
	private JButton btnSend;
	private JButton migrateBtn;
	
	public JScreenWallet(String data[][]) {
		setLayout(new BorderLayout(0, 0));
		
		String headers[] = { "Id", "Address", "ADK Balance" };
        
        String data_withTotal[][] = new String[data.length+1][headers.length];
        
        BigInteger btotal = BigInteger.ZERO;
        for (int row = 0; row < data.length; row++) {
        	for (int col = 0; col < headers.length; col++) {
        		data_withTotal[row+1][col] = data[row][col];
        		if (col==2) {
        			btotal = btotal.add(new BigInteger(data[row][col]));
        		}
            }
        }
        data_withTotal[0][0] = "";
        data_withTotal[0][1] = "Total ADK (shown)";
        data_withTotal[0][2] = btotal.toString(10);
        
        
		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_left = new JPanel();
		panel.add(panel_left, BorderLayout.WEST);
		panel_left.setLayout(new GridLayout(0, 1, 0, 0));
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.DoAction("REFRESH", JScreenWallet.this, null);
			}
		});
//		//btnRefresh.setBorderPainted(false);
//		//btnRefresh.setFocusPainted(false);
//		btnRefresh.setContentAreaFilled(t);
//		btnRefresh.setBackground(JScreenMainFrame.AidosGreen);
		
		JLabel lblNewLabel = new JLabel("Wallet actions");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel_left.add(lblNewLabel);
		panel_left.add(btnRefresh);
		
		btnSend = new JButton("Send ADK");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRow() > 0) {
					String address = table.getValueAt(table.getSelectedRow(),1).toString();
					String available_amount = table.getValueAt(table.getSelectedRow(),2).toString();
					if (address.startsWith("0x")) {
						Flow.DoAction("SEND", JScreenWallet.this, new String[] {address, available_amount});
					}
					
				}
			}
		});
		btnSend.setEnabled(false);
		btnSend.setText("<html>Send ADK<br><i>no address<br>selected</i>)</html>");
		panel_left.add(btnSend);
		
		JButton btnAddAddress = new JButton("Add Address");
		btnAddAddress.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Flow.DoAction("ADDADDRESS", JScreenWallet.this, null);
			}
		});
		panel_left.add(btnAddAddress);
		
		JButton btnGetTXinfo = new JButton("<html><center>Get TX Info<br>(advanced)</center></html>");
		btnGetTXinfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String tx = Flow.GetTextFromUser("Enter transaction id:", 
						     "Get technical transaction info (for advanced users)",50).trim();
				if (tx.matches("^0x[a-fA-F0-9]{64}$")) {
					
					new Thread(new Runnable() {
                        @Override
						public void run() {
							// TODO Auto-generated method stub
							ProcResult result_rec = Flow.CallCLIWallet("txinfo",tx);
							JSONObject j_rec = Flow.getJsonFromResult(result_rec);
							if (j_rec==null || !Flow.isOKJson(j_rec)) {
								Flow.ShowErrorMessage("Error retrieving TX info. Check log.");
								Flow.LogLn(j_rec==null?"null":j_rec.toJSONString());
								
							} else {
								JSONArray ja = (JSONArray)j_rec.get("data");
								Flow.LogLn("#### TX "+tx);
								for (Object o : ja) {
									Flow.LogLn(o.toString());
									Flow.LogLn("===============================");
								}
								Flow.ShowInfoMessage("TX data retrieved. Find data in log.");
							}
					  }}).start();
				} else {
					Flow.ShowErrorMessage("Invalid transaction ID, or request cancelled by user.");
				}
			}
		});
		
		migrateBtn = new JButton("<html><center>Migrate<br>\r\nAZ9 ADK<br>\r\n(old Mesh)</center></html>");
		migrateBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// migrate old ADK
				if (table.getSelectedRow() > 0) {
					String address = table.getValueAt(table.getSelectedRow(),1).toString();
					if (address.startsWith("0x")) {
						Flow.DoAction("MIGRATE", JScreenWallet.this, new String[] {address});
					}
				}
				//
			}
		});
		
		migrateBtn.setEnabled(false);
		panel_left.add(migrateBtn);
		panel_left.add(btnGetTXinfo);
		
		panel_left.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
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
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		final TableModel tm = new MyTableModel(data_withTotal, headers);
		table.setModel(tm);
		CustomRenderer cr = new CustomRenderer ();
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
		panel_4.setBackground(new Color(34,118,72));
		panel.add(panel_4, BorderLayout.NORTH);
		
		JLabel lbltitle = new JLabel("Wallet");
		lbltitle.setForeground(Color.WHITE);
		lbltitle.setHorizontalAlignment(SwingConstants.CENTER);
		lbltitle.setFont(new Font("Arial", Font.BOLD, 16));
		panel_4.add(lbltitle);
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
	
	public JButton getBtnSend() {
		return btnSend;
	}

	void updateButtons() {
		    if (table.getSelectedRow()>0) {
            	btnSend.setEnabled(true);
            	btnSend.setText("Send ADK");
            	migrateBtn.setEnabled(true);
            } else {
            	btnSend.setEnabled(false);
            	btnSend.setText("<html>Send ADK<br><i>no address<br>selected</i>)</html>");
            	migrateBtn.setEnabled(false);
            }
    }
	public JButton getMigrateBtn() {
		return migrateBtn;
	}
}

class MyTableModel extends DefaultTableModel {

	   public MyTableModel(Object[][] tableData, Object[] colNames) {
	      super(tableData, colNames);
	   }

	   public boolean isCellEditable(int row, int column) {
	      return false;
	   }
}


class CustomRenderer extends DefaultTableCellRenderer 
{

	
	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
		String newValue = (String)value;
        if (column == 2) {
        	// show value as ADK
        	newValue = JScreenWallet.ConvertWeiToADK((String)newValue);
        	int indexofDot = newValue.toString().indexOf('.');
        	while (indexofDot != -1 && indexofDot < 8 ) {
        		newValue = " "+newValue.toString();
        		indexofDot = newValue.toString().indexOf('.');
        	}
        }
        newValue = " "+newValue+" ";
        Component c = super.getTableCellRendererComponent(table, newValue, isSelected, hasFocus, row, column);
        if (c instanceof CustomRenderer) {
        	CustomRenderer cr = (CustomRenderer) c;
        	
        	// default 
        	cr.setForeground(Color.BLACK);
        	cr.setBackground(Color.WHITE);
        	cr.setFont(JScreenWallet.tableDefaultFont);
        	
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

