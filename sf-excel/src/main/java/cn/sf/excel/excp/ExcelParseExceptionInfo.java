package cn.sf.excel.excp;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExcelParseExceptionInfo implements Serializable {
	private int rowNum;

	private String columnName;

	private String errMsg;

	public ExcelParseExceptionInfo(int rowNum, String columnName, String errMsg) {
		this.rowNum = rowNum;
		this.columnName = columnName;
		this.errMsg = errMsg;
	}

}
