package com.integration.common.print;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

public class PrintUtil {
	
	private static String OS = System.getProperty("os.name").toLowerCase();

	public static void main(String[] args) throws Exception {
		new PrintUtil().print("", "");
	}
	
	public static AcroFields invokeField(AcroFields form ,ExpressVO expressVO) throws Exception{
		BaseFont bf = BaseFont.createFont("STSong-Light","UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);
		form.setField("loanCode", "3333333333");
		form.setFieldProperty("loadName","textfont",bf,null);
		form.setField("loanName", "3333333333");
		return form;
	}
	
	public void print(String ctx,String realpath) {
		try{
			List<ExpressVO> list = ExpressVO.getList();
			int total = list.size(); //总共记录
			int page = total / 3; //页数
			int left = total % 3; //最后不满一页的条数,可能为0
			
	        FileOutputStream fos = new FileOutputStream(realpath+"template/out.pdf"); 
	        System.out.println(realpath+"template/out.pdf");
			Document doc = new Document();
			PdfWriter writer = PdfWriter.getInstance(doc, fos);
			Map<String,Image> imageMap = this.genPage(writer, list,ctx,realpath);
			doc.open();
			for(int i = 0; i < page; i++){
				if(i>0){doc.newPage();}
				Image pageImg = imageMap.get(i+"");
				pageImg.setAbsolutePosition(0, 0);
				
				Image barcode1 = imageMap.get(i+"-0");
				barcode1.setAbsolutePosition(325, 740);  
				barcode1.scaleAbsolute(240, 60);

				Image barcode2 = imageMap.get(i+"-1");
				barcode2.setAbsolutePosition(325, 470);  
				barcode2.scaleAbsolute(240, 60);
		        
				Image barcode3 = imageMap.get(i+"-2");
				barcode3.setAbsolutePosition(325, 200);  
				barcode3.scaleAbsolute(240, 60);
		        
				doc.add(pageImg);
				doc.add(barcode1);
				doc.add(barcode2);
				doc.add(barcode3);
			}
			if(left==1){
				doc.newPage();
				Image pageImg = imageMap.get("L1");
				pageImg.setAbsolutePosition(0, 560);
				Image barcode1 = imageMap.get("L1-1");
				barcode1.setAbsolutePosition(325, 740);  
				barcode1.scaleAbsolute(240, 60);
				doc.add(pageImg);
				doc.add(barcode1);
			}
			if(left==2){
				doc.newPage();
				Image pageImg = imageMap.get("L1");
				pageImg.setAbsolutePosition(0, 290);
				
				Image barcode1 = imageMap.get("L1-1");
				barcode1.setAbsolutePosition(325, 740);  
				barcode1.scaleAbsolute(240, 60);
				
				Image barcode2 = imageMap.get("L1-2");
				barcode2.setAbsolutePosition(325, 470);  
				barcode2.scaleAbsolute(240, 60);
				
				doc.add(pageImg);
				doc.add(barcode1);
				doc.add(barcode2);
			}
			
	        doc.close();//当文件拷贝  记得关闭doc
	        
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public Map<String,Image> genPage(PdfWriter writer,List<ExpressVO> list,String ctx,String realpath) throws Exception{
		int total = list.size(); //总共记录
		int page = total / 3; //页数
		int left = total % 3; //最后不满一页的条数,可能为0
		int j = 0; 
		Map<String,Image> imgMap = new HashMap<String,Image>();
		for(int i = 0; i < page; i++){
			//生成第 i+1 页
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// 打开已经定义好字段以后的pdf模板 
			PdfReader reader = new PdfReader(realpath+"template/A4.pdf");
			// 将要生成的目标PDF文件名称
			PdfStamper stamp = new PdfStamper(reader, baos);
			AcroFields form = stamp.getAcroFields();
			for(;j <total; j++){
				ExpressVO express = list.get(j);
				fillExpressForm(form,Integer.toString((j)%3+1),express);
				Image image = Image.getInstance(new URL(ctx+"images/barCode.jpg?barcode="+express.getBeb_EXPRESS_NO()));
				imgMap.put(i+"-"+((j)%3), image);
				if((j+1)%3==0){
					j++;
					break;
				}
			}
			stamp.setFormFlattening(true);
			stamp.close(); 
			//PdfImportedPage impPage = writer.getImportedPage(new PdfReader(baos.toByteArray()), 1); 
			PdfImportedPage impPage =   writer.getImportedPage(new PdfReader(baos.toByteArray()), 1); 
			imgMap.put(i+"", Image.getInstance(impPage));
		}
		if(left==1){
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfReader reader = new PdfReader(realpath+"template/A4-1.pdf");
			PdfStamper stamp = new PdfStamper(reader, baos);
			AcroFields form = stamp.getAcroFields();
			ExpressVO express = list.get(j);
			fillExpressForm(form,1+"",express);
			Image image = Image.getInstance(new URL(ctx+"images/barCode.jpg?barcode="+express.getBeb_EXPRESS_NO()));
			imgMap.put("L1-1", image);
			stamp.setFormFlattening(true);
			stamp.close(); 
			PdfImportedPage impPage =   writer.getImportedPage(new PdfReader(baos.toByteArray()), 1); 
			imgMap.put("L1", Image.getInstance(impPage));
		}
		if(left==2){
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfReader reader = new PdfReader(realpath+"template/A4-2.pdf");
			PdfStamper stamp = new PdfStamper(reader, baos);
			AcroFields form = stamp.getAcroFields();
			//倒数第二条
			ExpressVO express = list.get(j);
			fillExpressForm(form,1+"",express);
			Image image = Image.getInstance(new URL(ctx+"images/barCode.jpg?barcode="+express.getBeb_EXPRESS_NO()));
			imgMap.put("L1-1", image);
			
			//倒数第一条
			express = list.get(j+1);
			fillExpressForm(form,2+"",express);
			image = Image.getInstance(new URL(ctx+"images/barCode.jpg?barcode="+express.getBeb_EXPRESS_NO()));
			imgMap.put("L1-2", image);
			
			stamp.setFormFlattening(true);
			stamp.close(); 
			PdfImportedPage impPage =   writer.getImportedPage(new PdfReader(baos.toByteArray()), 1); 
			imgMap.put("L1", Image.getInstance(impPage));
			
		}
		System.out.println(imgMap.size());
		return imgMap;
	}
	
	
	/**
     * @Title 填充物流单字段
     * @param form
     * 		 	物流单
     * @param formNum
     * 		 	第几张表单（一页3张）
     * @param express
     * 			填充的物流单Bean
     * @author tq
     */
	private void fillExpressForm(AcroFields form, String formNum, ExpressVO express) throws Exception{
		BaseFont bf = BaseFont.createFont("STSong-Light","UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);
		form.setFieldProperty("postDate"+ formNum,"textfont",bf,null);
		form.setField("postDate" + formNum, express.getBeb_SENDER_DATE());
		form.setFieldProperty("packType"+ formNum,"textfont",bf,null);
		String type = "A".equals(express.getBeb_EXPRESS_TYPE().trim())?"普快":"特快";
		
		form.setField("packType" + formNum, type);
		form.setFieldProperty("account"+ formNum,"textfont",bf,null);
		form.setField("account" + formNum, null);
		form.setFieldProperty("from"+ formNum,"textfont",bf,null);
		form.setField("from" + formNum, express.getBeb_SENDER());
		form.setFieldProperty("company"+ formNum,"textfont",bf,null);
		form.setField("company" + formNum, null);
		form.setFieldProperty("fromAddress"+ formNum,"textfont",bf,null);
		form.setField("fromAddress" + formNum, null);
		form.setFieldProperty("fromTel"+ formNum,"textfont",bf,null);
		form.setField("fromTel" + formNum, express.getBeb_SENDER_MOBILE());
		form.setFieldProperty("to"+ formNum,"textfont",bf,null);
		form.setField("to" + formNum, express.getBeb_RECEIVER());
		form.setFieldProperty("postcode"+ formNum,"textfont",bf,null);
		form.setField("postcode" + formNum, express.getBeb_ZIP_CODE());
		String toMainAddress = express.getBeb_PROVINCE()+"  "+express.getBeb_CITY()+"  "+express.getBeb_COUNTY()+"\n";
		
		form.setFieldProperty("toMainAddress"+ formNum,"textfont",bf,null);
		form.setField("toMainAddress" + formNum, toMainAddress);
		form.setFieldProperty("toAddress"+ formNum,"textfont",bf,null);
		form.setField("toAddress" + formNum, express.getBeb_DETAIL_RECEIVER_ADDR());
		form.setFieldProperty("toTel"+ formNum,"textfont",bf,null);
		form.setField("toTel" + formNum, express.getBeb_RECEIVER_MOBILE());
		form.setFieldProperty("description"+ formNum,"textfont",bf,null);
		form.setField("description" + formNum, express.getBeb_REMARK());
		form.setFieldProperty("weight"+ formNum,"textfont",bf,null);
		form.setField("weight" + formNum,  Double.toString(express.getBeb_TOTAL_WEIGHT()));
	}
	
	
	public  void generatePdf() throws Exception{
		
		String path = "/data/files/private/contract/"; 
		if(OS.indexOf("windows")!=-1){
			path = "F:/";
		}
		String inputFile = path + "loan/template/loanTemplate.html";   
        String outputFile = path + "loan/template/flying.pdf";  
        OutputStream os = new FileOutputStream(outputFile);  
        ITextRenderer renderer = new ITextRenderer();  
        ITextFontResolver fontResolver = renderer.getFontResolver();  
        fontResolver.addFont(path + "loan/template/msyh.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        File file = new File(inputFile);    
        renderer.setDocumentFromString(FileUtils.readFileToString(file));  
        // 解决图片的相对路径问题  
        String baseUrl = "file:"+path + "loan/template/";
		if(OS.indexOf("windows")!=-1){
			baseUrl = "file:/"+path + "loan/template/";
		}
        renderer.getSharedContext().setBaseURL(baseUrl);
        renderer.layout();  
        renderer.createPDF(os);  
        os.close();  
	}
	
	
}

