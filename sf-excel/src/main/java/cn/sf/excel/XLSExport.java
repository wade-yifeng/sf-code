package cn.sf.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;

@Slf4j
public class XLSExport extends AbstractExcelExport{

    //通过文件名构造实例
    public XLSExport(String exportFilePath) {
        this.exportFilePath = exportFilePath;
        this.workbook = new HSSFWorkbook();
    }
    public XLSExport(String exportFilePath,String templateFilePath) {
        try {
            this.exportFilePath = exportFilePath;
            this.templateFilePath = templateFilePath;
            if(templateFilePath!=null) {
                this.workbook = new HSSFWorkbook(new FileInputStream(templateFilePath));
            }else{
                this.workbook = new HSSFWorkbook();
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
