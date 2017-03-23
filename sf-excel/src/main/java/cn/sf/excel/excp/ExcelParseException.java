package cn.sf.excel.excp;

import java.util.List;

public class ExcelParseException extends Exception {

	private static final long serialVersionUID = -1L;

	private List<ExcelParseExceptionInfo> infoList;

	public ExcelParseException(List<ExcelParseExceptionInfo> infoList) {
		super();
		this.infoList = infoList;
	}

	public List<ExcelParseExceptionInfo> getInfoList() {
		return infoList;
	}

	public void setInfoList(List<ExcelParseExceptionInfo> infoList) {
		this.infoList = infoList;
	}
}
