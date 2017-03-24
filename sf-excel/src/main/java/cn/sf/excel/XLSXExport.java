package cn.sf.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;

@Slf4j
public class XLSXExport extends AbstractExcelExport{

	//通过文件名构造实例
	public XLSXExport(String exportFilePath) {
		this.exportFilePath = exportFilePath;
		this.workbook = new XSSFWorkbook();
	}
	public XLSXExport(String exportFilePath,String templateFilePath) {
		try {
			this.exportFilePath = exportFilePath;
			this.templateFilePath = templateFilePath;
			if(templateFilePath!=null) {
				this.workbook = new XSSFWorkbook(new FileInputStream(templateFilePath));
			}else{
				this.workbook = new XSSFWorkbook();
			}
		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}

}