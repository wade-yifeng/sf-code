package cn.sf.excel.common;

import cn.sf.excel.ExcelExportField;
import lombok.Data;

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

}
