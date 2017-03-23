package cn.sf.excel;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by nijianfeng on 17/3/23.
 */
@Slf4j
public abstract class AbstractExcelExport {

    // 设置cell编码解决中文高位字节截断
    private static short XLS_ENCODING = HSSFCell.ENCODING_UTF_16;
    // 定制浮点数格式
    private static String NUMBER_FORMAT = " #,##0.00 ";

    @Getter
    protected String xlsFilePath;
    @Getter
    protected Workbook workbook;
    @Getter
    protected Sheet sheet;
    @Getter
    protected Row row;

    ///////////////////////////////////////////////////////////
    //行操作
    ///////////////////////////////////////////////////////////
    //根据行号增加一行
    public void createRow(int index) {
        this.row = this.sheet.createRow(index);
    }
    //删除行
    public void deleteRow(Row row) {
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
    //根据name获取sheet第一行
    public Row getFirstRowBySheetName(String name) {
        Sheet sheet = this.workbook.getSheet(name);
        if (sheet != null)
            return sheet.getRow(0);
        return null;
    }
    //设置行高
    public void setRowHight(float height) {
        if (this.row != null)
            this.row.setHeightInPoints(height);
    }

    ///////////////////////////////////////////////////////////
    //sheet操作
    ///////////////////////////////////////////////////////////
    //添加一张sheet
    public void addSheet(String sheetName) {
        if (this.workbook == null) {
            this.workbook = new HSSFWorkbook();
        }
        Sheet tmpSheet = workbook.getSheet(sheetName);
        if (tmpSheet == null)
            this.sheet = workbook.createSheet(sheetName);
        else {
            this.sheet = tmpSheet;
        }
    }
    //根据name修改当前操作的sheet
    public boolean changeSheet(String name) {
        Sheet tmpSheet = workbook.getSheet(name);
        if (tmpSheet == null)
            return false;
        else {
            this.sheet = tmpSheet;
            return true;
        }
    }
    //根据index修改当前操作的sheet
    public boolean changeSheet(int index) {
        Sheet tmpSheet = workbook.getSheetAt(index);
        if (tmpSheet == null)
            return false;
        else {
            this.sheet = tmpSheet;
            return true;
        }
    }

    ///////////////////////////////////////////////////////////
    //cell操作
    ///////////////////////////////////////////////////////////
    public void setCell(int index, String value, CellStyle style) {
        Cell cell = this.row.createCell(index);
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellType(XLS_ENCODING);
        if(style!=null) {
            cell.setCellStyle(style);
        }
        cell.setCellValue(value);
    }
    //设置单元格填充值
    public void setCell(int index, String value) {
        Cell cell = this.row.createCell(index);
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellType(XLS_ENCODING);
        cell.setCellValue(value);
    }
    //设置单元格填充值
    public void setCell(int index, Integer value) {
        Cell cell = this.row.createCell(index);
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellValue(value);
    }
    //设置单元格填充值
    public void setCell(int index, Long value) {
        Cell cell = this.row.createCell(index);
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellValue(value);
    }
    //设置单元格填充值
    public void setCell(int index, Double value) {
        Cell cell = this.row.createCell(index);
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellValue(value);
        CellStyle cellStyle = workbook.createCellStyle(); // 建立新的cell样式
        DataFormat format = workbook.createDataFormat();
        cellStyle.setDataFormat(format.getFormat(NUMBER_FORMAT)); // 设置cell样式为定制的浮点数格式
        cell.setCellStyle(cellStyle); // 设置该cell浮点数的显示格式
    }

    ///////////////////////////////////////////////////////////
    //导出操作
    ///////////////////////////////////////////////////////////
    //导出Excel文件
    public void exportXLS() {
        if(xlsFilePath ==null||workbook==null){
            return;
        }
        try {
            FileOutputStream fOut = new FileOutputStream(xlsFilePath);
            workbook.write(fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    //导出Excel文件
    public void exportXLS(OutputStream out) {
        if(workbook==null){
            return;
        }
        try {
            workbook.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    //workbook转换成InputStream
    public InputStream getInputStreamFromWorkbook() {
        if(workbook==null){
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            workbook.write(os);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);
        return is;
    }

    ///////////////////////////////////////////////////////////
    //style操作
    ///////////////////////////////////////////////////////////
    public CellStyle createStyle() {
        return workbook.createCellStyle();
    }

    ///////////////////////////////////////////////////////////
    //data数据操作
    ///////////////////////////////////////////////////////////
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
                                if (field.getType() == Date.class) {
                                    this.setCell(cellIndex, new SimpleDateFormat(excelAnnotation.dateFormat()).format(result));
                                }else if (field.getType() == String.class) {
                                    this.setCell(cellIndex, (String) result);
                                }else if (field.getType() == Long.class) {
                                    this.setCell(cellIndex, (Long) result);
                                }else if (field.getType() == Double.class) {
                                    this.setCell(cellIndex, (Double) result);
                                }else if (field.getType() == Integer.class) {
                                    this.setCell(cellIndex, (Integer) result);
                                }else {
                                    this.setCell(cellIndex, val);
                                }
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("给excel设值失败.",e);
                    }

                }
            }
        }
    }

}
