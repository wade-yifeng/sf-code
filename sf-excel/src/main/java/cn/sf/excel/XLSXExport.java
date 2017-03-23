package cn.sf.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class XLSXExport extends AbstractExcelExport{

	//通过文件名构造实例
	public XLSXExport(String fileName) {
		this.xlsFilePath = fileName;
		this.workbook = new XSSFWorkbook();
	}
	//通过文件流构造实例
	public XLSXExport(InputStream ins) {
		try {
			this.workbook = new XSSFWorkbook(ins);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	//通过文件名构造实例并创建一张sheet
	public XLSXExport(String fileName, String sheetName) {
		this.xlsFilePath = fileName;
		this.workbook = new XSSFWorkbook();
		this.sheet = workbook.createSheet(sheetName);
	}

}