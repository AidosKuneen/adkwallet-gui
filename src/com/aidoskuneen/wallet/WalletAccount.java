package com.aidoskuneen.wallet;

import java.math.BigInteger;

public class WalletAccount
{
	public int id = -1;
	public String pubKey = "";
	public String privKeyEncoded = "";
	public BigInteger LastBalance = BigInteger.ZERO ;
	public BigInteger LastBalanceStaked = BigInteger.ZERO ;
	public BigInteger LastStakedBlock = BigInteger.ZERO ;
	public BigInteger LastBlock = BigInteger.ZERO ;
	public BigInteger LockedForBlocks = BigInteger.ZERO ;
	public BigInteger LockedPeriod = BigInteger.ZERO ;
	
}
