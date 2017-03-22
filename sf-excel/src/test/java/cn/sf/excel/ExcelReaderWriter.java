package cn.sf.excel;

import cn.sf.excel.common.ReaderSheetVO;
import cn.sf.excel.common.WriterSheetVO;
import cn.sf.excel.excp.ExcelParseExceptionInfo;
import cn.sf.excel.excp.ExcelParseExeption;
import cn.sf.excel.utils.ExcelUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by hznijianfeng on 2016/4/9.
 */
@Slf4j
public class ExcelReaderWriter {


    public static void main(String[] args) {
        writerXLSXExcelTest();
        writerXLSExcelTest();
        readExcelTest();
    }

    public static void writerXLSExcelTest() {
        String path = ExcelReaderWriter.class.getResource("/").getPath();
        int index = path.indexOf("sf-excel") + "sf-excel".length();
        File file = new File(path.substring(0, index) + "/src/test/java/cn/sf/excel/common/writer-test.xls");
        System.out.println(file.getAbsolutePath());
        file.delete();
        //数据准备
        WriterSheetVO o1 = new WriterSheetVO();
        o1.setAttr1("萨芬发放萨法俄啊饿疯俄国呃啊飞");
        o1.setAttr2(12);
        o1.setAttr3(22L);
        o1.setAttr4(22.22);
        WriterSheetVO o2 = new WriterSheetVO();
        o2.setAttr1("萨芬");
        o2.setAttr2(12);
        o2.setAttr3(22L);
        o2.setAttr4(22.22);
        List<WriterSheetVO> list = Lists.newArrayList(o1,o2);

        //第一张sheet
        XLSExport xlsExport = new XLSExport(file.getAbsolutePath(),"测试1");
        xlsExport.createEXCEL(list,WriterSheetVO.class);
        //第二张sheet
        xlsExport.addSheet("测试2");
        xlsExport.createEXCEL(list,WriterSheetVO.class);

        ExcelUtils.exportXLS(xlsExport.getWorkbook(),xlsExport.getXlsFileName());
    }

    public static void writerXLSXExcelTest() {
        String path = ExcelReaderWriter.class.getResource("/").getPath();
        int index = path.indexOf("sf-excel") + "sf-excel".length();
        File file = new File(path.substring(0, index) + "/src/test/java/cn/sf/excel/common/writer-test.xlsx");
        System.out.println(file.getAbsolutePath());
        file.delete();
        //数据准备
        WriterSheetVO o1 = new WriterSheetVO();
        o1.setAttr1("萨芬发放萨法俄啊饿疯俄国呃啊飞");
        o1.setAttr2(12);
        o1.setAttr3(22L);
        o1.setAttr4(22.22);
        WriterSheetVO o2 = new WriterSheetVO();
        o2.setAttr1("萨芬");
        o2.setAttr2(12);
        o2.setAttr3(22L);
        o2.setAttr4(22.22);
        List<WriterSheetVO> list = Lists.newArrayList(o1,o2);

        //第一张sheet
        XLSXExport xlsxExport = new XLSXExport(file.getAbsolutePath(),"测试1");
        xlsxExport.createEXCEL(list,WriterSheetVO.class);
        //第二张sheet
        xlsxExport.addSheet("测试2");
        xlsxExport.createEXCEL(list,WriterSheetVO.class);

        ExcelUtils.exportXLS(xlsxExport.getWorkbook(),xlsxExport.getXlsFileName());
    }

    public static void readExcelTest() {
        // 验证错误信息采集，保存错误采集
        String path = ExcelReaderWriter.class.getResource("/").getPath();
        int index = path.indexOf("sf-excel") + "sf-excel".length();
        String filePath = path.substring(0, index) + "/src/test/java/cn/sf/excel/common/reader-test.xlsx";
        System.out.println(filePath);
        Workbook book =  ExcelUtils.createWorkBook(filePath);
        // 一个excel验证并解析成以sheetName为key，每行为一个对象的集合组成的MAP
        StringBuilder sb = new StringBuilder();
        Map<String, List<ReaderSheetVO>> sheetsMap = Maps.newLinkedHashMap();
        if (book != null) {
            ExcelParse<ReaderSheetVO> excelParse = new ExcelParse<>();
            int sheetNum = book.getNumberOfSheets();
            for (int i = 0; i < sheetNum; i++) {
                Sheet sheet = book.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                // 如果sheetName有限制，可在此处验证
                if (!"测试".equals(sheetName)) {
                    sb.append("sheetName必须是测试!");
                }
                List<ReaderSheetVO> rsVOList = Lists.newArrayList();
                Iterator<Row> iter = sheet.rowIterator();
                // 去除第一行
                iter.next();
                while (iter.hasNext()) {
                    Row row = iter.next();
                    boolean isEmpty = ExcelUtils.isRowEmpty(row);
                    if (!isEmpty) {
                        ReaderSheetVO rsVO = null;
                        try {
                            // 此处可抛出异常，譬如excel里的5e不能转换成ReaderSheetVO的Integer
                            rsVO = excelParse.getObject(row, ReaderSheetVO.class);
                        } catch (ExcelParseExeption e) {
                            List<ExcelParseExceptionInfo> errInfos = e.getInfoList();
                            if (errInfos != null && errInfos.size() > 0) {
                                for (ExcelParseExceptionInfo errInfo : errInfos) {
                                    sb.append(sheetName + "第").append(errInfo.getRowNum()).append("行").append("，");
                                    sb.append(sheetName + "字段“").append(errInfo.getColumnName()).append("”").append("，")
                                            .append(errInfo.getErrMsg()).append(";");
                                }
                            }
                        }
                        if (rsVO != null) {
                            // 验证一些必要的日期字符串是否正确属性等excel组件没支持的验证过程
                            // 验证完毕把一行数据添加到list中
                            rsVOList.add(rsVO);
                        }
                    }
                }
                // 一个sheetName循环完填充到MAP
                sheetsMap.put(sheetName, rsVOList);
            }
        }
        System.out.println(sheetsMap);
    }
}
