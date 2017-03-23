package cn.sf.excel.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.NumberToTextConverter;

import java.io.*;
import java.util.Iterator;

@Slf4j
public class ExcelUtils {

	public static boolean isRowEmpty(Row row) {
		Iterator<Cell> cellIter = row.cellIterator();
		boolean isRowEmpty = true;
		while (cellIter.hasNext()) {
			Cell cell = cellIter.next();
			String value = getValueOfCell(cell);
			if(!"".equals(value)){
				isRowEmpty = false;
				break;
			}
		}
		return isRowEmpty;
	}

	public static String getValueOfCell(Cell cell) {
		String value;
		if (cell == null)
			return null;
		if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
			value = NumberToTextConverter.toText(cell.getNumericCellValue());
		} else {
			value = cell.toString();
		}
		return value.trim();
	}

	public static Workbook createWorkBook(String filePath) {
		Workbook workbook = null;
		try {
			workbook = WorkbookFactory.create(new FileInputStream(new File(filePath)));
		} catch (Exception e) {
			throw new RuntimeException("请上传xlsx文件!",e);
		}
		return workbook;
	}

}
