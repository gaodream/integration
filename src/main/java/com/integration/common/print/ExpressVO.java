package com.integration.common.print;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExpressVO  implements Serializable {

    private static final long serialVersionUID = 1L;

	private String beb_ROW_ID;//主键
	private String beb_EXPRESS_NO;//运单号
	private String beb_EXPRESS_STATUS;//运单号状态
	private String beb_EXPRESS_TYPE;//包裹类型
	private String beb_SENDER;//寄件人
	private String beb_SENDER_MOBILE;//寄件人联系电话
	private String beb_SENDER_DATE;//寄件日期
	private String beb_RECEIVER;//收件人
	private String beb_RECEIVER_MOBILE;//收件人联系电话
	private String beb_ID_CARD;//收件人身份证
	private String beb_PROVINCE;//收件地址省份
	private String beb_CITY;//收件地址城市
	private String beb_COUNTY;//收件地址区县
	private String beb_COMPANY;//收件地址单位
	private String beb_DETAIL_RECEIVER_ADDR;//详细收件地址
	private String beb_ZIP_CODE;//邮编
	private double beb_TARIFF;//关税
	private double beb_CHARGES;//资费
	private double beb_INSURANCE_FEE;//保险费
	private double beb_OTHER_FEE;//其他费用
	private double beb_TOTAL_FEE;//总资费
	private double beb_TOTAL_WEIGHT;//总重量
	private String beb_INPUT_USER;//录入人编码
	private String beb_INPUT_NAME;//录入人名称
	private String beb_INPUT_DATE;//录入时间
	private String beb_CUSTOMER_CODE;//客户编码
	private String beb_CUSTOMER_NAME;//客户名称
	private String beb_GROUP_CODE;//组编码
	private String beb_GROUP_NAME;//组名称
	private String beb_DELETED_FLAG;//删除标志
	private String beb_CREATED_BY;//记录创建人ID
	private String beb_CREATED_DATE;//记录创建时间
	private String beb_LAST_UPD_BY;//记录最近修改人ID
	private String beb_LAST_UPD_DATE;//记录最近修改日期
	private String beb_MODIFICATION_NUM;//记录修改次数
	private String beb_REMARK;//备注
    
    
    private String beb_EXPRESS_BEGIN_NO;// 备注
    private String beb_EXPRESS_END_NO;// 备注

	
    private boolean savePerson;
    
    private boolean saveID;
    
    
    

    public String getBeb_INPUT_NAME() {
		return beb_INPUT_NAME;
	}

	public void setBeb_INPUT_NAME(String beb_INPUT_NAME) {
		this.beb_INPUT_NAME = beb_INPUT_NAME;
	}

	public String getBeb_CUSTOMER_CODE() {
		return beb_CUSTOMER_CODE;
	}

	public void setBeb_CUSTOMER_CODE(String beb_CUSTOMER_CODE) {
		this.beb_CUSTOMER_CODE = beb_CUSTOMER_CODE;
	}

	public String getBeb_CUSTOMER_NAME() {
		return beb_CUSTOMER_NAME;
	}

	public void setBeb_CUSTOMER_NAME(String beb_CUSTOMER_NAME) {
		this.beb_CUSTOMER_NAME = beb_CUSTOMER_NAME;
	}

	public String getBeb_GROUP_CODE() {
		return beb_GROUP_CODE;
	}

	public void setBeb_GROUP_CODE(String beb_GROUP_CODE) {
		this.beb_GROUP_CODE = beb_GROUP_CODE;
	}

	public String getBeb_GROUP_NAME() {
		return beb_GROUP_NAME;
	}

	public void setBeb_GROUP_NAME(String beb_GROUP_NAME) {
		this.beb_GROUP_NAME = beb_GROUP_NAME;
	}

	public boolean isSaveID() {
		return saveID;
	}

	public void setSaveID(boolean saveID) {
		this.saveID = saveID;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getBeb_EXPRESS_BEGIN_NO() {
		return beb_EXPRESS_BEGIN_NO;
	}

	public void setBeb_EXPRESS_BEGIN_NO(String beb_EXPRESS_BEGIN_NO) {
		this.beb_EXPRESS_BEGIN_NO = beb_EXPRESS_BEGIN_NO;
	}

	public String getBeb_EXPRESS_END_NO() {
		return beb_EXPRESS_END_NO;
	}

	public void setBeb_EXPRESS_END_NO(String beb_EXPRESS_END_NO) {
		this.beb_EXPRESS_END_NO = beb_EXPRESS_END_NO;
	}

	public ExpressVO() {
    }

    public ExpressVO(String beb_EXPRESS_NO, String beb_SENDER_DATE) {
        this.beb_EXPRESS_NO = beb_EXPRESS_NO;
        this.beb_SENDER_DATE = beb_SENDER_DATE;
    }

    public ExpressVO(String beb_CREATED_DATE) {
        this.beb_CREATED_DATE = beb_CREATED_DATE;
    }

    public String getBeb_ROW_ID() {
        return beb_ROW_ID;
    }

    public void setBeb_ROW_ID(String beb_ROW_ID) {
        this.beb_ROW_ID = beb_ROW_ID;
    }

    public String getBeb_EXPRESS_NO() {
        return beb_EXPRESS_NO;
    }

    public void setBeb_EXPRESS_NO(String beb_EXPRESS_NO) {
        this.beb_EXPRESS_NO = beb_EXPRESS_NO;
    }

    public String getBeb_EXPRESS_STATUS() {
        return beb_EXPRESS_STATUS;
    }

    public void setBeb_EXPRESS_STATUS(String beb_EXPRESS_STATUS) {
        this.beb_EXPRESS_STATUS = beb_EXPRESS_STATUS;
    }

    public String getBeb_EXPRESS_TYPE() {
        return beb_EXPRESS_TYPE;
    }

    public void setBeb_EXPRESS_TYPE(String beb_EXPRESS_TYPE) {
        this.beb_EXPRESS_TYPE = beb_EXPRESS_TYPE;
    }

    public String getBeb_SENDER() {
        return beb_SENDER;
    }

    public void setBeb_SENDER(String beb_SENDER) {
        this.beb_SENDER = beb_SENDER;
    }

    public String getBeb_SENDER_MOBILE() {
        return beb_SENDER_MOBILE;
    }

    public void setBeb_SENDER_MOBILE(String beb_SENDER_MOBILE) {
        this.beb_SENDER_MOBILE = beb_SENDER_MOBILE;
    }

    public String getBeb_SENDER_DATE() {
        return beb_SENDER_DATE;
    }

    public void setBeb_SENDER_DATE(String beb_SENDER_DATE) {
        this.beb_SENDER_DATE = beb_SENDER_DATE;
    }

    public String getBeb_RECEIVER() {
        return beb_RECEIVER;
    }

    public void setBeb_RECEIVER(String beb_RECEIVER) {
        this.beb_RECEIVER = beb_RECEIVER;
    }

    public String getBeb_RECEIVER_MOBILE() {
        return beb_RECEIVER_MOBILE;
    }

    public void setBeb_RECEIVER_MOBILE(String beb_RECEIVER_MOBILE) {
        this.beb_RECEIVER_MOBILE = beb_RECEIVER_MOBILE;
    }

    public String getBeb_ID_CARD() {
        return beb_ID_CARD;
    }

    public void setBeb_ID_CARD(String beb_ID_CARD) {
        this.beb_ID_CARD = beb_ID_CARD;
    }

    public String getBeb_PROVINCE() {
        return beb_PROVINCE;
    }

    public void setBeb_PROVINCE(String beb_PROVINCE) {
        this.beb_PROVINCE = beb_PROVINCE;
    }

    public String getBeb_CITY() {
        return beb_CITY;
    }

    public void setBeb_CITY(String beb_CITY) {
        this.beb_CITY = beb_CITY;
    }

    public String getBeb_COUNTY() {
        return beb_COUNTY;
    }

    public void setBeb_COUNTY(String beb_COUNTY) {
        this.beb_COUNTY = beb_COUNTY;
    }

    public String getBeb_COMPANY() {
        return beb_COMPANY;
    }

    public void setBeb_COMPANY(String beb_COMPANY) {
        this.beb_COMPANY = beb_COMPANY;
    }

    public String getBeb_DETAIL_RECEIVER_ADDR() {
        return beb_DETAIL_RECEIVER_ADDR;
    }

    public void setBeb_DETAIL_RECEIVER_ADDR(String beb_DETAIL_RECEIVER_ADDR) {
        this.beb_DETAIL_RECEIVER_ADDR = beb_DETAIL_RECEIVER_ADDR;
    }

    public String getBeb_ZIP_CODE() {
        return beb_ZIP_CODE;
    }

    public void setBeb_ZIP_CODE(String beb_ZIP_CODE) {
        this.beb_ZIP_CODE = beb_ZIP_CODE;
    }

    public double getBeb_TARIFF() {
        return beb_TARIFF;
    }

    public void setBeb_TARIFF(double beb_TARIFF) {
        this.beb_TARIFF = beb_TARIFF;
    }

    public double getBeb_CHARGES() {
        return beb_CHARGES;
    }

    public void setBeb_CHARGES(double beb_CHARGES) {
        this.beb_CHARGES = beb_CHARGES;
    }

    public double getBeb_INSURANCE_FEE() {
        return beb_INSURANCE_FEE;
    }

    public void setBeb_INSURANCE_FEE(double beb_INSURANCE_FEE) {
        this.beb_INSURANCE_FEE = beb_INSURANCE_FEE;
    }

    public double getBeb_OTHER_FEE() {
        return beb_OTHER_FEE;
    }

    public void setBeb_OTHER_FEE(double beb_OTHER_FEE) {
        this.beb_OTHER_FEE = beb_OTHER_FEE;
    }

    public double getBeb_TOTAL_FEE() {
        return beb_TOTAL_FEE;
    }

    public void setBeb_TOTAL_FEE(double beb_TOTAL_FEE) {
        this.beb_TOTAL_FEE = beb_TOTAL_FEE;
    }

    public double getBeb_TOTAL_WEIGHT() {
        return beb_TOTAL_WEIGHT;
    }

    public void setBeb_TOTAL_WEIGHT(double beb_TOTAL_WEIGHT) {
        this.beb_TOTAL_WEIGHT = beb_TOTAL_WEIGHT;
    }

    public String getBeb_INPUT_USER() {
        return beb_INPUT_USER;
    }

    public void setBeb_INPUT_USER(String beb_INPUT_USER) {
        this.beb_INPUT_USER = beb_INPUT_USER;
    }

    public String getBeb_INPUT_DATE() {
        return beb_INPUT_DATE;
    }

    public void setBeb_INPUT_DATE(String beb_INPUT_DATE) {
        this.beb_INPUT_DATE = beb_INPUT_DATE;
    }


    public String getBeb_DELETED_FLAG() {
        return beb_DELETED_FLAG;
    }

    public void setBeb_DELETED_FLAG(String beb_DELETED_FLAG) {
        this.beb_DELETED_FLAG = beb_DELETED_FLAG;
    }

    public String getBeb_CREATED_BY() {
        return beb_CREATED_BY;
    }

    public void setBeb_CREATED_BY(String beb_CREATED_BY) {
        this.beb_CREATED_BY = beb_CREATED_BY;
    }

    public String getBeb_CREATED_DATE() {
        return beb_CREATED_DATE;
    }

    public void setBeb_CREATED_DATE(String beb_CREATED_DATE) {
        this.beb_CREATED_DATE = beb_CREATED_DATE;
    }

    public String getBeb_LAST_UPD_BY() {
        return beb_LAST_UPD_BY;
    }

    public void setBeb_LAST_UPD_BY(String beb_LAST_UPD_BY) {
        this.beb_LAST_UPD_BY = beb_LAST_UPD_BY;
    }

    public String getBeb_LAST_UPD_DATE() {
        return beb_LAST_UPD_DATE;
    }

    public void setBeb_LAST_UPD_DATE(String beb_LAST_UPD_DATE) {
        this.beb_LAST_UPD_DATE = beb_LAST_UPD_DATE;
    }

    public String getBeb_MODIFICATION_NUM() {
        return beb_MODIFICATION_NUM;
    }

    public void setBeb_MODIFICATION_NUM(String beb_MODIFICATION_NUM) {
        this.beb_MODIFICATION_NUM = beb_MODIFICATION_NUM;
    }

    public String getBeb_REMARK() {
        return beb_REMARK;
    }

    public void setBeb_REMARK(String beb_REMARK) {
        this.beb_REMARK = beb_REMARK;
    }


    public boolean getSavePerson() {
        return savePerson;
    }

    public void setSavePerson(boolean savePerson) {
        this.savePerson = savePerson;
    }

    
    public static  List<ExpressVO> getList(){
    	
    	List<ExpressVO> list = new ArrayList<ExpressVO>();
    	for(int i=0;i<3;i++){
    		ExpressVO expressVO =new ExpressVO();
    		expressVO.setBeb_ROW_ID(String.valueOf(1000+i));
    		expressVO.setBeb_EXPRESS_NO("01000025"+i);
    		expressVO.setBeb_EXPRESS_TYPE("A");
    		expressVO.setBeb_SENDER("张三"+i);
    		expressVO.setBeb_COMPANY("阿波罗");
    		expressVO.setBeb_SENDER_MOBILE("13951884271");
    		expressVO.setBeb_RECEIVER("李四"+i);
    		expressVO.setBeb_PROVINCE("江苏省");
    		expressVO.setBeb_CITY("南京市");
    		expressVO.setBeb_COUNTY("雨花台");
    		expressVO.setBeb_RECEIVER_MOBILE("13951884271");
    		expressVO.setBeb_REMARK("奶粉 * "+i+"，尿不湿 * "+i);
    		expressVO.setBeb_DETAIL_RECEIVER_ADDR("海通大厦2210");
    		
    		list.add(expressVO);
    	}
    	return list;
    }
}