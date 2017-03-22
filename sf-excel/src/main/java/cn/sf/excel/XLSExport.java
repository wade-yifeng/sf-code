package cn.sf.excel;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

@Slf4j
public class XLSExport{

    // 设置cell编码解决中文高位字节截断
    private static short XLS_ENCODING = HSSFCell.ENCODING_UTF_16;
    // 定制浮点数格式
    private static String NUMBER_FORMAT = " #,##0.00 ";

    @Getter
    private String xlsFileName;
    @Getter
    private HSSFWorkbook workbook;
    @Getter
    private HSSFSheet sheet;
    @Getter
    private HSSFRow row;

    //通过文件名构造实例
    public XLSExport(String fileName) {
        this.xlsFileName = fileName;
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
        this.xlsFileName = fileName;
        this.workbook = new HSSFWorkbook();
        this.sheet = workbook.createSheet(sheetName);
    }
    //添加一张sheet
    public void addSheet(String sheetName) {
        if (this.workbook == null) {
            this.workbook = new HSSFWorkbook();
        }
        HSSFSheet tmpSheet = workbook.getSheet(sheetName);
        if (tmpSheet == null)
            this.sheet = workbook.createSheet(sheetName);
        else {
            this.sheet = tmpSheet;
        }
    }

    //根据name修改当前操作的sheet
    public boolean changeSheet(String name) {
        HSSFSheet tmpSheet = workbook.getSheet(name);
        if (tmpSheet == null)
            return false;
        else {
            this.sheet = tmpSheet;
            return true;
        }
    }
    //根据index修改当前操作的sheet
    public boolean changeSheet(int index) {
        HSSFSheet tmpSheet = workbook.getSheetAt(index);
        if (tmpSheet == null)
            return false;
        else {
            this.sheet = tmpSheet;
            return true;
        }
    }

    //根据行号增加一行
    public void createRow(int index) {
        this.row = this.sheet.createRow(index);
    }
    //删除行
    public void deleteRow(HSSFRow row) {
        if (this.sheet == null)
            throw new RuntimeException("sheet is null!");
        this.sheet.removeRow(row);
    }
    //根据行号删除一行
    public void deleteRow(int index) {
        if (this.sheet == null)
            throw new RuntimeException("sheet is null!");
        deleteRow(sheet.getRow(index));
    }

    /**
     * 设置单元格
     * @param index 列号
     * @param value 单元格填充值
     */
    public void setCell(int index, String value, HSSFCellStyle style) {
        HSSFCell cell = this.row.createCell(index);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellType(XLS_ENCODING);
        if(style!=null) {
            cell.setCellStyle(style);
        }
        cell.setCellValue(value);
    }
    /**
     * 设置单元格
     * @param index 列号
     * @param value 单元格填充值
     */
    public void setCell(int index, String value) {
        HSSFCell cell = this.row.createCell(index);
        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        cell.setCellType(XLS_ENCODING);
        cell.setCellValue(value);
    }
    /**
     * 设置单元格
     * @param index 列号
     * @param value 单元格填充值
     */
    public void setCell(int index, Integer value) {
        HSSFCell cell = this.row.createCell(index);
        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
        cell.setCellValue(value);
    }
    /**
     * 设置单元格
     * @param index 列号
     * @param value 单元格填充值
     */
    public void setCell(int index, Long value) {
        HSSFCell cell = this.row.createCell(index);
        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
        cell.setCellValue(value);
    }
    /**
     * 设置单元格
     * @param index 列号
     * @param value 单元格填充值
     */
    public void setCell(int index, Double value) {
        HSSFCell cell = this.row.createCell(index);
        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
        cell.setCellValue(value);
        HSSFCellStyle cellStyle = workbook.createCellStyle(); // 建立新的cell样式
        HSSFDataFormat format = workbook.createDataFormat();
        cellStyle.setDataFormat(format.getFormat(NUMBER_FORMAT)); // 设置cell样式为定制的浮点数格式
        cell.setCellStyle(cellStyle); // 设置该cell浮点数的显示格式
    }

    public HSSFRow getFirstRowBySheetName(String name) {
        HSSFSheet sheet = this.workbook.getSheet(name);
        if (sheet != null)
            return sheet.getRow(0);
        return null;
    }

    public HSSFCellStyle createStyle() {
        return workbook.createCellStyle();

    }

    public void setRowHight(float height) {
        if (this.row != null)
            this.row.setHeightInPoints(height);
    }

    public void createEXCEL(List data, Class type) {
        // 还未创建sheet返回
        if (this.workbook == null || this.sheet == null) {
            return;
        }
        //组装数据
        Field[] fields = type.getDeclaredFields();
        int rowOffset = 0;
        this.createRow(rowOffset++);
        //设置标题列
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            ExcelExportField excelAnnotation = field.getAnnotation(ExcelExportField.class);
            if (excelAnnotation == null)
                continue;
            int cellIndex = excelAnnotation.cellIndex();
            String title = excelAnnotation.desc();
            this.setCell(cellIndex, title);
        }
        //遍历list
        listDatas(data, type, fields, rowOffset);
    }
    public void createEXCEL(List<?> data, Class type,int startRowIndex) {
        // 还未创建sheet返回
        if (this.workbook == null || this.sheet == null) {
            return;
        }
        //组装数据
        Field[] fields = type.getDeclaredFields();
        //遍历list
        listDatas(data, type, fields, startRowIndex);
    }

    private void listDatas(List data, Class type, Field[] fields, int rowOffset) {
        if (data != null && data.size() > 0) {
            for (Object obj : data) {
                this.createRow(rowOffset++);
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    ExcelExportField excelAnnotation = field.getAnnotation(ExcelExportField.class);
                    if (excelAnnotation == null)
                        continue;
                    int cellIndex = excelAnnotation.cellIndex();
                    // 利用反射赋值
                    StringBuilder sb = new StringBuilder();
                    String fieldName = field.getName();
                    sb.append("get");
                    sb.append(fieldName.substring(0, 1).toUpperCase());
                    sb.append(fieldName.substring(1));
                    try {
                        Method getMethod = type.getMethod(sb.toString());
                        Object result = getMethod.invoke(obj);
                        if (result != null) {
                            String val = result.toString();
                            if (val != null && !"".equals(val)) {
                                this.setCell(cellIndex, val);
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("create excel failed.",e);
                    }

                }
            }
        }
    }

}
