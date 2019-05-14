package cn.laymm.Utils;


import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Component
@PropertySource({"classpath:application.yml"})
public class ExportExcelUtil {

	@Value("${spring.datasource.driver-class-name}")
	public String driver;
	@Value("${spring.datasource.url}")
	public String url;
	@Value("${spring.datasource.username}")
	public String userName;
	@Value("${spring.datasource.password}")
	public String password;

	public Connection getConnection(){
		Connection conn=null;
		try {
			Class.forName(driver);
		   //new oracle.jdbc.driver.OracleDriver();   
		    conn = DriverManager.getConnection(url, userName, password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}

	public void exportExcel(String title, String[] name, ResultSet rs) throws Exception {
		HSSFWorkbook workBook = new HSSFWorkbook(); //创建excel文件
		HSSFSheet sheet = workBook.createSheet(); //创建工作表
		
		for(int i=0; i<name.length; i++){
			sheet.setColumnWidth(i, 20*256);
		}
		
		//标题样式
		HSSFCellStyle titleStyle = workBook.createCellStyle();
		titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); //水平居中
		titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		HSSFFont titleFont =  workBook.createFont(); //字体
		titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); //加粗
		titleFont.setFontHeightInPoints((short)14);  //字号
		titleStyle.setFont(titleFont);               //标题使用字体样式
		//正文样式
		HSSFCellStyle contentStyle = workBook.createCellStyle();
		HSSFFont contentFont = workBook.createFont(); //字体
		contentFont.setFontHeightInPoints((short)12);
		contentStyle.setFont(contentFont);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//边框
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); //居中
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		
		HSSFRow titleRow = sheet.createRow(0);    //创建标题行
		HSSFCell titleCell = titleRow.createCell(0);
		titleCell.setCellValue(title);
		titleCell.setCellStyle(titleStyle);     //单元格样式
		CellRangeAddress address = new CellRangeAddress(0, 0, 0, name.length-1);
		sheet.addMergedRegion(address);
		titleRow.setHeightInPoints(30); //行高
		
		
		HSSFRow row2 = sheet.createRow(1);      
		row2.setHeightInPoints(20);
		for(int i=0; i<name.length; i++){
			HSSFCell cell = row2.createCell(i);
			cell.setCellValue(name[i]);
			cell.setCellStyle(contentStyle);
		}
		
		int flag = 2;
		while(rs.next()){
			HSSFRow datarow = sheet.createRow(flag);
			datarow.setHeightInPoints(20);
			ResultSetMetaData metaData = rs.getMetaData();
			for(int i=0; i<metaData.getColumnCount(); i++){
				HSSFCell datacell = datarow.createCell(i);
				datacell.setCellValue(rs.getString(i+1));
				datacell.setCellStyle(contentStyle);
			}
			
			flag++;
		}
		
		String path = "e:\\demo.xls";
		FileOutputStream out = new FileOutputStream(path);
		workBook.write(out);	//保存单元格
		out.close();
		
	}
}
