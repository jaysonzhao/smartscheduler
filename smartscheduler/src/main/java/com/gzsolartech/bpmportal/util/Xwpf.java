package com.gzsolartech.bpmportal.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;



public class Xwpf {  
    

   public static void main(String[] args) throws IOException, XmlException, OpenXML4JException {
	
	   InputStream is = new FileInputStream("C:\\Users\\solar\\Desktop\\平台组任务统计.xls");
       HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(is));
       ExcelExtractor extractor = new ExcelExtractor(wb);
       extractor.setIncludeSheetNames(true);
       extractor.setFormulasNotResults(false);
       extractor.setIncludeCellComments(true);
       //获得字符串形式
       String text = extractor.getText();
       System.out.println(text);
}
    
    public static String readECLCE2007(String file)throws IOException, XmlException, OpenXML4JException{
    	   return new XSSFExcelExtractor(POIXMLDocument.openPackage(file)).getText();   
    }
    
    
    public static String readECLCE2003(String file) throws IOException{
    	 InputStream is = new FileInputStream(file);
         HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(is));
         ExcelExtractor extractor = new ExcelExtractor(wb);
         extractor.setIncludeSheetNames(true);
         extractor.setFormulasNotResults(false);
         extractor.setIncludeCellComments(true);
    	
 	   return extractor.getText();   
 }
    
    public static  String readPPT2007(String file) throws IOException, XmlException, OpenXML4JException {
        return new XSLFPowerPointExtractor(POIXMLDocument.openPackage(file)).getText();   
   }
    public static String readWord2007(String file) throws IOException, XmlException, OpenXML4JException{
    	return new XWPFWordExtractor(POIXMLDocument.openPackage(file)).getText();
    } 
	public static String readWord2003(String file) throws IOException{
		return new WordExtractor( new FileInputStream(file)).getText();  
	}
	/* public static String readECLCE2003(String file)throws IOException, XmlException, OpenXML4JException{
		   return new ExcelExtractor(new FileInputStream(file)).getText();   
		   
	 }*/
	
	public static String readPPT2003(String file) throws IOException{
        PowerPointExtractor extractor=new PowerPointExtractor(new FileInputStream(file));
        return extractor.getText();
    }
		

    
    public static String getPdfText(String file)throws Exception{
        boolean sort=false;
        int startPage=1;
        int endPage=10;
        PDDocument document=null;
        try{
            try{
                document=PDDocument.load(new File(file));
            }catch(MalformedURLException e){
                
            }
            PDFTextStripper stripper=new PDFTextStripper();
            stripper.setSortByPosition(sort);
            stripper.setStartPage(startPage);
            stripper.setEndPage(endPage);
            return stripper.getText(document);
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }finally{
            if(document!=null){
                document.close();
            }
        }
    }
 }  