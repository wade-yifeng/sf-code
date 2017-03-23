package cn.sf.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class XLSExport extends AbstractExcelExport{

    //通过文件名构造实例
    public XLSExport(String fileName) {
        this.xlsFilePath = fileName;
        this.workbook = new HSSFWorkbook();
    }
    //通过文件流构造实例
    public XLSExport(InputStream ins) {
        try {
            this.workbook = new HSSFWorkbook(ins);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
    //通过文件名构造实例并创建一张sheet
    public XLSExport(String fileName, String sheetName) {
        this.xlsFilePath = fileName;
        this.workbook = new HSSFWorkbook();
        this.sheet = workbook.createSheet(sheetName);
    }

}
