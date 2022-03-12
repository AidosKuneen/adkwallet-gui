package com.aidoskuneen.wallet;

import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Wallet {
	
	private static Wallet main = new Wallet();
	
	private Wallet() {
		reset();
	}
	
	public static Wallet MainWallet() {
		return main;
	}
	
	public String walletName = "default";
	public String mnemonicEncoded;
	public ArrayList<WalletAccount> accounts = new ArrayList<WalletAccount>();
	public String passwordHash;
	public boolean initialized = false;
	
	public static String walletFilename = "wallet.json"; // wallet.json is the main local wallet, changed to temp if using Metamask 'connect' 
	public static boolean isTempWallet = false;  
	
	public static boolean ReadWallet() { // returns if wallet is initialized
		Flow.LogLn("Reading "+walletFilename);
		main.reset();
		JSONParser parser = new JSONParser();
		Object obj;
		try {
			FileReader fr = new FileReader(walletFilename);
			obj = parser.parse(fr);
			fr.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			Flow.LogLn(e.getMessage());
			return false;
		}
        JSONObject jsonWallet =  (JSONObject) obj;

        try {
			main.walletName = (String)jsonWallet.getOrDefault("WalletName", "default");
			main.initialized = (boolean)jsonWallet.getOrDefault("Initialized", false);
			main.mnemonicEncoded = (String)jsonWallet.getOrDefault("MnemonicEncoded", "");
			main.passwordHash = (String)jsonWallet.getOrDefault("PasswordHash", "");
			JSONArray jsonAccounts = (JSONArray)jsonWallet.getOrDefault("Accounts", new JSONArray());
			int id = 0;
			for (Object o : jsonAccounts) {
				JSONObject acct = (JSONObject) o;
				WalletAccount a = new WalletAccount();
				a.id = id;
				String lastBal = (String)acct.getOrDefault("LastBalance", "0");
				if (lastBal.equalsIgnoreCase("")) {
					lastBal = "0";
				}
				a.LastBalance = new BigInteger(lastBal);
				a.privKeyEncoded = (String)acct.getOrDefault("PrivKeyEncoded", "");
				a.pubKey = (String)acct.getOrDefault("PubKey", "");
				main.accounts.add(a);
				id++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Flow.LogLn(e.getMessage());
			main.reset();
			return false;
		}
        
        return true;
	}

	private void reset() {
		walletName = "default";
		mnemonicEncoded = "";
		accounts.clear();
		passwordHash = "";
		initialized = false;
	}
	
}
