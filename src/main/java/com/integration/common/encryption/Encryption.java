package com.integration.common.encryption;

public interface Encryption {

	//加密
	public abstract String encrypt(String paramString) throws Exception;
	//加密
	public abstract String encrypt(String paramString,String systemCode ) throws Exception;

	//解密
	public abstract String decrypt(String paramString) throws Exception;
	//解密
	public abstract String decrypt(String paramString,String systemCode ) throws Exception; 
}
