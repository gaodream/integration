package com.integration.bigdata.mq.consumer;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class MQMessage implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String pMerCode;
	private String pErrCode;
	private String pErrMsg;
	private String p3DesXmlPara;
	private String pSign;
	private String decodeParam;
	private boolean isSync = true;
	private int order;
	private Map<String, Object> resultMap ;
	
	public MQMessage(){
		
	}
	public MQMessage(HttpServletRequest request,boolean isSync){
		this.isSync = isSync;
		this.pMerCode = request.getParameter("pMerCode");
		this.pErrCode = request.getParameter("pErrCode");
		this.pErrMsg = request.getParameter("pErrMsg");
		this.p3DesXmlPara = request.getParameter("p3DesXmlPara");
		this.pSign = request.getParameter("pSign");

        /*String signPlainText = pMerCode + pErrCode + pErrMsg + p3DesXmlPara + IPSConfig.CERT_MD5;
        String localSign = com.ips.security.utility.IpsCrypto.md5Sign(signPlainText);
        if (localSign.equals(pSign)) {
            System.out.println("MD5验签通过！");
            this.decodeParam = com.ips.security.utility.IpsCrypto.triDesDecrypt(p3DesXmlPara, IPSConfig.DES_KEY, IPSConfig.DES_IV);
        }*/
        //resultMap = IPSUtils.verifySign(pMerCode, pErrCode, pErrMsg, p3DesXmlPara, pSign);
	}
	
	
	public String getpMerCode() {
		return pMerCode;
	}
	public void setpMerCode(String pMerCode) {
		this.pMerCode = pMerCode;
	}
	public String getpErrCode() {
		return pErrCode;
	}
	public void setpErrCode(String pErrCode) {
		this.pErrCode = pErrCode;
	}
	public String getpErrMsg() {
		return pErrMsg;
	}
	public void setpErrMsg(String pErrMsg) {
		this.pErrMsg = pErrMsg;
	}
	public String getP3DesXmlPara() {
		return p3DesXmlPara;
	}
	public void setP3DesXmlPara(String p3DesXmlPara) {
		this.p3DesXmlPara = p3DesXmlPara;
	}
	public String getpSign() {
		return pSign;
	}
	public void setpSign(String pSign) {
		this.pSign = pSign;
	}
	public String getDecodeParam() {
		return decodeParam;
	}
	public void setDecodeParam(String decodeParam) {
		this.decodeParam = decodeParam;
	}
	public boolean isSync() {
		return isSync;
	}
	public void setSync(boolean isSync) {
		this.isSync = isSync;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public Map<String, Object> getResultMap() {
		return resultMap;
	}
	public void setResultMap(Map<String, Object> resultMap) {
		this.resultMap = resultMap;
	}
	
	

}
