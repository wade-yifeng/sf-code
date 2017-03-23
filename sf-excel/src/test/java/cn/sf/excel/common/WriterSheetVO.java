package cn.sf.excel.common;

import cn.sf.excel.ExcelExportField;
import lombok.Data;

import java.util.Date;

/**
 * Created by hznijianfeng on 2017/3/22.
 */
@Data
public class WriterSheetVO {

    @ExcelExportField(cellIndex = 0, desc = "字符串")
    private String attr1;
    @ExcelExportField(cellIndex = 1,  desc = "Integer")
    private Integer attr2;
    @ExcelExportField(cellIndex = 2,  desc = "Long")
    private Long attr3;
    @ExcelExportField(cellIndex = 3,  desc = "Double")
    private Double attr4;
    @ExcelExportField(cellIndex = 4, dateFormat = "yyyy-MM-dd HH:mm:ss", desc = "日期类型")
    private Date date1;
    @ExcelExportField(cellIndex = 5, dateFormat = "yyyy-MM-dd", desc = "日期类型")
    private Date date2;

}
