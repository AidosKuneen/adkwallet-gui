package com.aidoskuneen.wallet;

public class ProcResult {
	public ProcResult(int _pexitCode, String out, String err) {
		exitCode = _pexitCode;
		errorText = err;
		outputText = out;
	}
	public int exitCode = -1;
	public 	String errorText = "";
	public String outputText = "";
}