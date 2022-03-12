package com.aidoskuneen.wallet;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.FileLock;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Flow {

	public static JScreenMainFrame mainFrame;
	
	public static String adkwallet_executable_name = "adkwallet_cli.exe";
	public static String adkwallet_executable_nonwindows_name = "adkwallet_cli";
	
	public static File CLI = pathToCLI();
	public static String OS = System.getProperty("os.name","windows").toLowerCase();
	
	public static File pathToCLI() {
		// make sure we have the current working directory (to be used for CLI, json and property files).
		// note this can change depending on how this app is run (from java, javaw, executable jar, launch4j, etc..
				
		String executable = System.getProperty("os.name","windows").toLowerCase().contains("windows")?
				adkwallet_executable_name : adkwallet_executable_nonwindows_name;
		File cli = new File(adkwallet_executable_name);
		System.out.println("Trying to locate CLI executable in: " + (cli==null?"NULL":cli.getAbsolutePath()));
		   
		if (cli.exists()) { // current working dir?
			   return cli;
		}
		cli = null;
		// no? keep looking
		// first try current working directory
		try {
			ProtectionDomain pd = Flow.class.getProtectionDomain();
			if (pd != null) {
				CodeSource cs = pd.getCodeSource();
				if (cs!= null) {
				   String locpath = cs.getLocation().getPath();
				   String decodedPath = URLDecoder.decode(locpath, "UTF-8");
				   cli = new File(decodedPath,executable.replace("./", ""));
				   System.out.println("Trying to locate CLI executable in: " + (cli==null?"NULL":cli.getAbsolutePath()));
				   if (cli != null && cli.exists()) {
					   return cli;
				   }
				   // else look up one dir higher (e.g. if we are within a "jar" file)
				   URI uri = new URI(decodedPath);
				   URI parent = uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");
				   cli = new File(parent.getPath(),executable.replace("./", ""));
				   System.out.println("Trying to locate CLI executable in: " + (cli==null?"NULL":cli.getAbsolutePath()));
				   if (cli != null && cli.exists()) {
					   return cli;
				   }
				}
			}
		} catch (UnsupportedEncodingException | SecurityException | URISyntaxException | NullPointerException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		//JOptionPane.showMessageDialog(null,"CLI file: "+ (CLI==null?"NULL":CLI.getAbsoluteFile()));
		if (CLI == null) {
			if (OS.contains("windows")) {
					JOptionPane.showMessageDialog(null, "Missing adkwallet_cli.exe command line executable.\n"+
			                                           "Please ensure it is in the same path as the GUI Application.","Error",JOptionPane.ERROR_MESSAGE);
					
					return;
			} else {
				JOptionPane.showMessageDialog(null, "Missing ./adkwallet_cli command line executable.\n"+
		                                           "Please ensure it is in the same path as the GUI Application.","Error",JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		
		if (!lockInstance("pid.lock")) {
			JOptionPane.showMessageDialog(null, "Another instance already running. Close other instances first.");
			return;
		}
		
		JConfig.LoadConfig();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Flow();
			}
		});
		
		
	}

    static String cachedPassword = "";
    
	public Flow() {
		mainFrame = new JScreenMainFrame();
		mainFrame.setVisible(true);
		
		/////////////////////////////JobMonitor
		Runnable r = new Runnable() {
			public void run() {
				OpenWallet();
			}
		};
		new Thread(r).start();
		/////////////////////////////
	}
	
	public void setScreen(JPanel p) {
		JScreenMainFrame.replacePanelCenter(p);
	}
	
	public void OpenWallet() {
		setScreen(new JScreenOpen()); // open local // open meta
		NextUserAction();
	}
	
	public boolean CheckPassword(String userPW) {
		ProcResult result_mn = CallCLIWallet("checkpassword","(**)"+userPW);
		JSONObject j = getJsonFromResult(result_mn);
		if (j==null || !isOKJson(j)) {
			return false;
		} else {
			return true;
		}
	}
	
	public void OpenLocal() { // open local wallet if exists, or create
		Wallet.ReadWallet(); // wallet exists?
		if (Wallet.MainWallet().initialized ) { // local wallet exists
			//
			int cnt = 0;
			while (cachedPassword.equals("")) {
				String userPW = GetPasswordFromUser("Enter password for local wallet:","Open Local Wallet", 8, true);
				if (userPW.equals("")) { // user cancelled password entry
					System.exit(0); 
				}
				if (!CheckPassword(userPW)){
					cnt++;
					Flow.ShowErrorMessage("Invalid password");
				 } else {
					cachedPassword = userPW;
					break;
				}
			}
			
			int cntAccounts = Wallet.MainWallet().accounts.size();
			String[][] data = new String[cntAccounts][3];
			
			int row = 0;
			for (WalletAccount a : Wallet.MainWallet().accounts) {
				data[row][0] = a.id+"";
				data[row][1] = a.pubKey;
				data[row][2] = a.LastBalance.toString();
				row++;
			}
			setScreen(new JScreenWallet(data)); //
			RequestRefresh();
			setScreen(new JScreenWallet(data)); // we do this twice, because it looks nicer if the refresh has the wallet in the background
			
			//
		} else { // need to set up a new local wallet
			setScreen(new JScreenNewWallet()); //
		}
	}
	
	
	
	static boolean readyForNextAction  = false;
	
	public void NextUserAction() {// ready for next user action
		new Thread(
			new Runnable() { // to avoid stack issues
			@Override
			public void run() {
				UserAction userAction = waitForAction();
				// User actioned:
				switch (userAction.action) {
				    ////////////////////////////// Main
					case "EXIT" :
						System.exit(0);
						break;
					case "OPENLOCAL" :
						OpenLocal();
						NextUserAction();
						break;
					case "OPENMETA" :
						// since we are connecting to metamask, we need to use a temporary wallet file, not the standared local file
						//Wallet.walletFilename = ""; // actually, we do this once we actually connect with "CONNECTMETA"..
						setScreen(new JScreenConnectMetamask()); 
						NextUserAction();
						break;
					case "CONNECTMETA" :
						String pw = userAction.params[0];
						ProcResult resultconnect = CallCLIWallet("loadMetamaskMnemonics",pw);
						Wallet.isTempWallet = true;
						EvaluateResultForMMImport(resultconnect, false, pw);
						NextUserAction();
						break;
					case "CREATE" :
						OpenLocal();
						NextUserAction();
						break;
					case "RECOVER" :
						
						OpenLocal();
						NextUserAction();
						break;
					case "IMPORTMETA" :
						String pw2 = userAction.params[0];
						ProcResult resultImport = CallCLIWallet("loadMetamaskMnemonics",pw2);
						EvaluateResultForMMImport(resultImport, true, pw2);
						NextUserAction();
						break;
					//////////////////////////// New Wallet
					case "CREATEWALLET-MN":
						
						String newPW = GetNewPassword();
						
						ProcResult result_mn = CallCLIWallet("createWalletFromMnemonic","(**)"+userAction.params[1], "(**)"+newPW);
						JSONObject j = getJsonFromResult(result_mn);
						if (j==null || !isOKJson(j)) {
							ShowErrorMessage("Error in CLI command during wallet creation. Check log.");
							LogLn(j==null?"null":j.toJSONString());
						} else {
							ShowInfoMessage("Wallet successfully imported.");
							cachedPassword = newPW; // no need to reenter..
						}
						OpenLocal();
						NextUserAction();
						break;
					
					case "CONNECTWALLET-MN": // if we are connecting rather than creating...
						
						String connectPW = userAction.params[2]; // no need to reenter password if we are only connecting temporarily //GetNewPassword();
						
						ProcResult result_mn_connect = CallCLIWallet("createWalletFromMnemonic","(**)"+userAction.params[1], "(**)"+connectPW);
						JSONObject jconnect = getJsonFromResult(result_mn_connect);
						if (jconnect==null || !isOKJson(jconnect)) {
							ShowErrorMessage("Error in CLI command during wallet creation. Check log.");
							LogLn(jconnect==null?"null":jconnect.toJSONString());
						} else {
							ShowInfoMessage("Connected.");
							LogLn(jconnect==null?"null":jconnect.toJSONString());
							cachedPassword = connectPW;
						}
						OpenLocal();
						NextUserAction();
						break;
						
					case "CREATENEWWALLET":
						
						String newPWforNewWallet = GetNewPassword();
						
						ProcResult result_mn2 = CallCLIWallet("createWalletNew","(**)"+newPWforNewWallet);
						JSONObject j2 = getJsonFromResult(result_mn2);
						if (j2==null || !isOKJson(j2) || !(j2.get("data") instanceof JSONArray)) {
							ShowErrorMessage("Error in CLI command during wallet creation. Check log.");
							LogLn(j2==null?"null":j2.toJSONString());
						} else {
							//System.out.println(j2.toJSONString());
							JSONArray data = (JSONArray) j2.get("data");
							ShowMnemonics(data.get(1).toString());
						}
						OpenLocal();
						NextUserAction();
						break;
					case "RECOVERWALLET":
						
						String mnemonics = GetTextFromUser("Enter your 12/24 mnemonic seed words:","Recover with Mnemonic:", 40);
						
						if (mnemonics.split(" ").length<12) {
							ShowErrorMessage("This does not look like a 12 or 24 mnemonic word seed.");
						} else {
							String newPWforNewWallet_rec = GetNewPassword();
							ProcResult result_rec = CallCLIWallet("createWalletFromMnemonic","(**)"+mnemonics, "(**)"+newPWforNewWallet_rec);
							JSONObject j_rec = getJsonFromResult(result_rec);
							if (j_rec==null || !isOKJson(j_rec)) {
								ShowErrorMessage("Error in CLI command during wallet creation. Check log.");
								LogLn(j_rec==null?"null":j_rec.toJSONString());
							} else {
								ShowInfoMessage("Wallet successfully recovered.");
								RequestRefresh();
							}
						}
						OpenLocal();
						NextUserAction();
						break;
					case "REFRESH":
						OpenLocal();
						NextUserAction();
					break;
					case "SEND": // send butten requested, get user input
						String from = userAction.params[0];
						String available = userAction.params[1];
						setScreen(new JScreenSend(from, available)); 
						NextUserAction();
					break;
					case "DOSEND": //execute sending
						String send_method = userAction.params[0]; //pow or gas
						String send_from = userAction.params[1];
						String send_to = userAction.params[2];
						String send_amount = userAction.params[3];
						ProcResult send_result_rec = CallCLIWallet("send",send_method,"(**)"+cachedPassword, send_from,send_to,send_amount);
						JSONObject send_j_rec = getJsonFromResult(send_result_rec);
						if (send_j_rec==null || !isOKJson(send_j_rec)|| !(send_j_rec.get("data") instanceof JSONArray)) {
							ShowErrorMessage("Error sending. Check log.");
							LogLn(send_j_rec==null?"null":send_j_rec.toJSONString());
						} else {
							JSONArray dt = ((JSONArray)(send_j_rec.get("data")));
							String tx = "[null]";
							if (dt.size() > 0)
								tx = (String)dt.get(0).toString();
							LogLn("*************************************\n");
							LogLn("Transaction Id: "+tx);
							LogLn("*************************************\n");
							ShowInfoMessage("Transaction sent. (Transaction ID can be found in log)");
						}
						
						OpenLocal();
						NextUserAction();
					break;
					
					case "CANCELSEND"://cancel sending
						OpenLocal();
						NextUserAction();
					break;
					case "MIGRATE": //execute sending
						String migrate_to = userAction.params[0];
						
						if (!GetOKCancelChoiceFromUser(
								"This function will allow you to migrate OLD ADK from the v1 Mesh (i.e. ADK balances on old AZ9 addresses)\n "
								+ "to the NEW 0x address \""+migrate_to+"\" you have selected on the previous screen.\n"
								+ " \n"
								+ "Note: You will need your OLD v1 WALLET SEED (81 character, AZ9 format)\n"
								+ " \n"
								+ " Please note that this process can take a while, as up to 10,000 addresses from the old seed will be scanned.\n",
								"ADK v1 Migration","Next Step (Enter Seed)","Cancel")) {
							ShowInfoMessage("cancelled.");
						} else {
								
							    String seed = Flow.GetTextFromUser("Enter your OLD 81 char SEED (from the old ADK wallet)", "Enter OLD Seed", 60).trim().toUpperCase();
								
								if (!seed.matches("^[A-Z9]{81}$")) {
									   ShowErrorMessage("Invalid mesh v1 seed. Must be 81 characters long and only consist of A-Z and 9");
									
								} else {//  seed OK
									if (GetOKCancelChoiceFromUser("Start migration of all old ADK linked to the entered v1 SEED to new address "+migrate_to+" ?\n\n"+
								        "This can take a while. Process/Results will be written to the log window below.", "Final check","OK, Start","Cancel")) {
										// proceed
										ProcResult send_result_migr = CallCLIWallet("migrate",seed,migrate_to);
										
										JSONObject j_mig_result = getJsonFromResult(send_result_migr);
										if (j_mig_result==null || !isOKJson(j_mig_result)|| !(j_mig_result.get("data") instanceof JSONArray)) {
											ShowErrorMessage("Error in migration process. Please check the log output.");
											LogLn(j_mig_result==null?"null":j_mig_result.toJSONString());
										} else {
											JSONArray migdata = (JSONArray) (j_mig_result.get("data"));
											if (migdata.size()>=2) {
												String adkMigrated = JScreenWallet.ConvertWeiToADK((String)migdata.get(0));
												boolean migrationJobStatus = "true".equalsIgnoreCase(((String)migdata.get(1)));
												ShowInfoMessage("Migration process results:\n\n"+
												                 "ADK migrated: "+adkMigrated+" ADK\n" +
												                 (migrationJobStatus?"(no ADK available to be claimed)":"(Some errors occured, please check log)"));
											}
										}
										
									} else {
										ShowInfoMessage("cancelled.");
									}
								}
						}
						OpenLocal();
						NextUserAction();
					break;
					case "ADDADDRESS":
						if (!cachedPassword.equals("")) {
							ProcResult result_rec = CallCLIWallet("addaddress","(**)"+cachedPassword);
							JSONObject j_rec = getJsonFromResult(result_rec);
							if (j_rec==null || !isOKJson(j_rec)) {
								ShowErrorMessage("Error adding address. Check log.");
								LogLn(j_rec==null?"null":j_rec.toJSONString());
								
							} else {
								ShowInfoMessage("Address added.");
							}
						}
						RequestRefresh();
						OpenLocal();
						NextUserAction();
					break;
					
					default:
						NextUserAction();
				}
			}
		}).start();
	}
	
	void RequestRefresh() {
		ProcResult result_rfr = CallCLIWallet("updatebalance");
		JSONObject jrfr = getJsonFromResult(result_rfr);
		if (jrfr==null || !isOKJson(jrfr) ) {
			//System.out.println(jrfr.toJSONString());
			ShowErrorMessage("Error in CLI command during balance refresh. Check log.");
			LogLn(jrfr==null?"null":jrfr.toJSONString());
			
		} else {
			LogLn(jrfr.toJSONString());
		}
	}
	
	String GetNewPassword() {
		
		String pw = GetPasswordFromUser("Enter a new password (min 8 char):", "Choose a password for your NEW LOCAL WALLET", 8);
		String pw2 = GetPasswordFromUser("Enter the password AGAIN:", "Check password", 8);
		
		
		if (pw.equals(pw2)) {
			return pw;
		} else {
			ShowErrorMessage("Passwords don't match! Please try again.");
			return GetNewPassword();
		}
		
	}
	
	String GetPasswordFromUser(String message, String title, int minlen) {
		return GetPasswordFromUser(message,title,minlen, false);
	}
	
	String GetPasswordFromUser(String message, String title, int minlen, boolean cancel_allowed) {
		char[] password = new char[0];
		while(true) {
				JPanel panel = new JPanel();
				panel.setLayout(new GridLayout(0, 1, 0, 0));
				panel.setBorder(BorderFactory.createEmptyBorder(5,10, 5, 10));
				
				
				JLabel label = new JLabel(message);
				
				JPasswordField pass = new JPasswordField(10);
				label.setFont(new Font("Arial", Font.PLAIN, 15));
				pass.setFont(new Font("Arial", Font.PLAIN, 15));
				panel.add(label);
				//panel.add(panelSpacer);
				panel.add(pass);
				String[] options = new String[]{"   OK   "};
				
				pass.addAncestorListener( new RequestFocusListener() );
				
				int option = JOptionPane.showOptionDialog(mainFrame, panel, title,
				                         JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
				                         null, options, options[0]);
				if(option == 0) // pressing OK button
				{
				    password = pass.getPassword();
				    if (password.length < minlen) {
				    	ShowErrorMessage("Password is too short. Min "+minlen+" char.");
				    	continue;
				    }
				    break;
				} else { //pressed X 
					if (!cancel_allowed) continue; 
					return "";
				}
		}
		return new String(password);
	}
	
	
	public static String[] BreakStringIntoLines(String msg) {
		String tmp = msg;
		if (tmp == null) return new String[]{""};
		//
		// first break forced linebreaks
		String[] lines1 = tmp.split("\n"); 
		return lines1;
	}
	
	public static String GetTextFromUser(String message, String title, int charwidth) {
		
				JPanel panel = new JPanel();
				panel.setLayout(new GridLayout(0, 1, 0, 0));
				panel.setBorder(BorderFactory.createEmptyBorder(5,10, 5, 10));
				JTextField pass = new JTextField(charwidth);
				
				for (String line : BreakStringIntoLines(message)) {
					JLabel label = new JLabel(line);
					panel.add(label);
				}
				
				panel.add(pass);
				String[] options = new String[]{"OK"};
				
				pass.addAncestorListener( new RequestFocusListener() );
				
				JOptionPane.showOptionDialog(mainFrame, panel, title,
				                         JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
				                         null, options, options[0]);
				String text = "";
				text = pass.getText();
				
		return new String(text);
	}
	
	public static boolean GetOKCancelChoiceFromUser(String message, String title, String oklabel, String cancellabel) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		panel.setBorder(BorderFactory.createEmptyBorder(5,10, 5, 10));
		for (String line : BreakStringIntoLines(message)) {
			JLabel label = new JLabel(line);
			panel.add(label);
		}
		String[] options = new String[]{oklabel, cancellabel};
		
		int option = JOptionPane.showOptionDialog(mainFrame, panel, title,
		                         JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
		                         null, options, options[0]);
		
	    return option == 0;
	}
	
	void ShowMnemonics(String message) {
			JPanel panel = new JPanel();
			JLabel label = new JLabel("Seed: ");
			JTextArea mm = new JTextArea();
			mm.setColumns(30);
			mm.setWrapStyleWord(true);
			mm.setLineWrap(true);
			panel.add(label);
			panel.add(mm);
			mm.setText(message);
			mm.setEditable(false);
			mm.setRows(5);
			mm.setFont(new Font("Arial", Font.BOLD, 16));
			String[] options = new String[]{"I have stored this SEED in a safe place."};
			
			JOptionPane.showOptionDialog(mainFrame, panel, "Your Mnemonic Seed. Don't share it with anyone.",
			                         JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
			                         null, options, options[0]);
		
	}
	
	
	static JSONObject getJsonFromResult(ProcResult result) {
		JSONObject output = null;
		if (result!= null && 
			result.outputText!=null &&
			result.outputText.trim().startsWith("{") &&
			result.outputText.trim().endsWith("}")
			) {
			LogLn("Got valid output. processing");
			JSONParser parser = new JSONParser();
			try {
				output = (JSONObject)parser.parse(result.outputText.trim());
				if (output.containsKey("ok")){
					return output;
				}
			} catch (ParseException e) {
				LogLn(e);
			}
		}
		return null;
	}
	
	static boolean isOKJson (JSONObject result) {
		if (result == null) return false;
		return (boolean)result.getOrDefault("ok", false);
	}
	
	
	void EvaluateResultForMMImport(ProcResult result, boolean useLocal, String password){
	
		HashMap<String,String> pk_mm = new HashMap<String,String>();
		JSONObject resultJSON = getJsonFromResult(result);
		boolean success = false; 
		
		if (resultJSON!=null) {
				Object msg = resultJSON.get("msg");
				Object data = resultJSON.get("data");
				
				
				if (isOKJson(resultJSON) && "mnemonics".equalsIgnoreCase((String) msg)) {
					if (data instanceof JSONObject) {
						for (Object acct : ((JSONObject) data).keySet()) {
							String pubKey = (String) acct;
							pk_mm.put(pubKey, (String)(((JSONObject) data).get(acct)));
							success = true;
						}
						
				    }
				} 
		}
		
		if (success) {
			setScreen(new JScreenChooseMetamaskAccount(pk_mm, useLocal, password));
		}
		else {
			ShowErrorMessage("No Metamask accounts found for this password.\nPassword incorrect or Metamask not installed.");
		}
	}
	
	static Object execSem = new Object();
	
	///// CALLING THE CLI PROGRAM
	
	static String APIVersion = "1";
	
	static synchronized ProcResult CallCLIWallet(String... commandline) {
		String c = commandline.length==0?"":commandline[0];
		JSplashWait.StartWait(c);
	    ArrayList<String> cmd = new ArrayList<String>();
	    
	    cmd.add(CLI.getAbsolutePath());
	    
	    cmd.add("-adknode");
    	cmd.add(JConfig.serverAPI);
    	cmd.add("-apiversion");// set sersion
    	cmd.add(APIVersion);
    	cmd.add("-apiwallet");// set wallet file
    	cmd.add(Wallet.walletFilename);
    	cmd.add("-command");
    	
    	String fullcmd = "";
    	for (String arg : cmd) { // first lot of params
    		fullcmd += arg + " ";
    	}
	     
	    for (String arg : commandline) { // second lot of params
	    	boolean hide = false;
	    	if (arg.startsWith("(**)")){
	    		arg = arg.substring(4);
	    		hide = true;
	    	}
	    	
	    	cmd.add(arg);
    		fullcmd += " " + (hide?"**********":arg);
    		
	    }
	    
	    final StringBuilder outString = new StringBuilder();
	    final StringBuilder errString = new StringBuilder();
	    
	    LogLn("executing: "+fullcmd); //remove this...
	    
	    
	    try {
			ProcessBuilder ps =new ProcessBuilder(cmd.toArray(new String[0]));
			ps.redirectErrorStream(false);
			
			Process pr = ps.start();  
			final InputStream isInput = pr.getInputStream();
			final InputStream isErr = pr.getErrorStream();
			
			byte[] be = new byte[1];
			int ne;
			//String line = "";
			
			// read err output
			while ((ne=isErr.read(be)) >= 0) {
				char ch = (char)be[0];
				if (ne>=1) {
					errString.append(ch);
				    Log(ch+"");
				}
			}
			String line = "";
			if (!line.equals("")) LogLn(line);
			while ((ne=isInput.read(be)) >= 0) {
				char ch = (char)be[0];
				if (ne>=1) {
					outString.append(ch);
					if (ch == '\n' || ch == '\r') {
						if (!line.equals("")) LogLn(line);
						line = "";
					}
				}
			}
			try {
				pr.waitFor();
			} catch (InterruptedException e) {}
		
			JSplashWait.StopWait();
			return new ProcResult(pr.exitValue(), outString.toString(), errString.toString());
			
		} catch (IOException e) {
			LogLn(e);
		}
	    JSplashWait.StopWait();
	    return new ProcResult(1, "", "");
				
	}
	
	static final UserAction userAction = new UserAction();
	
	public static synchronized void DoAction(String action, JPanel screen, String[] params) {
		
		Runnable rAction = new Runnable() {
			@Override
			public void run() {
				if (!readyForNextAction) {
					SetMessage("Action cannot be performed, please wait for current job to complete ("+userAction.action+").");
					Toolkit.getDefaultToolkit().beep();  
					return;
				}
				synchronized(userAction) {
					try {
						userAction.action=action;
						userAction.params=params;
						SetMessage("busy... ("+userAction.action+")");
						userAction.notifyAll();
						
					} catch (IllegalMonitorStateException  e) {
						// System is busy, no input allowed
						e.printStackTrace();
						SetMessage("Action cannot be performed, please wait for current job to complete (2) ("+userAction.action+").");
						Toolkit.getDefaultToolkit().beep();  
						return;
					}
				}
				return;			
			}
	   };
	   new Thread(rAction).start();
	}

	public static UserAction waitForAction() {
		readyForNextAction = true;
		SetMessage("ready.");
		

		try {
			synchronized(userAction) {
				userAction.wait();
			}
		} catch (InterruptedException e) {
			userAction.action = "INTERRUPTED";
		}
		readyForNextAction = false;
		
		return userAction;
	}
	

	public static synchronized void LogLn(String text) {
		Log(text+"\n");
	}
	
	public static synchronized void LogLn(Exception e) {
		Log("Exception "+e.getClass()+" : "+e.getMessage());
	}
	static Object msgsem = new Object();
	
	public static synchronized void Log(final String text) {
		synchronized(msgsem) {
			if (Flow.mainFrame != null && Flow.mainFrame.getTextAreaLog()!= null) {
				Flow.mainFrame.getTextAreaLog().append(text);
				Flow.mainFrame.getTextAreaLog().setCaretPosition(Flow.mainFrame.getTextAreaLog().getDocument().getLength());
				Flow.mainFrame.getTextAreaLog().invalidate();
			}
		}
	}
	
	public static synchronized void SetMessage(final String msg) {
		Flow.mainFrame.statusText.setText(msg);
		LogLn(msg); // also log
	}
	
	public static void ShowErrorMessage(String msg) {
		JOptionPane.showMessageDialog(mainFrame, msg, "Error", JOptionPane.ERROR_MESSAGE);
		LogLn("Error: "+msg);
	}
	
	public static void ShowInfoMessage(String msg) {
		JOptionPane.showMessageDialog(mainFrame, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
		LogLn("Info: "+msg);
	}
	
	
	private static boolean lockInstance(final String lockFile) {
	    try {
	        final File file = new File(lockFile);
	        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
	        final FileLock fileLock = randomAccessFile.getChannel().tryLock();
	        if (fileLock != null) {
	            Runtime.getRuntime().addShutdownHook(new Thread() {
	                public void run() {
	                    try {
	                        fileLock.release();
	                        randomAccessFile.close();
	                        file.delete();
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                    }
	                }
	            });
	            return true;
	        }
	    } catch (Exception e) {
	        return true; // allow opening anyways...
	    }
	    return false;
	}
	
}
