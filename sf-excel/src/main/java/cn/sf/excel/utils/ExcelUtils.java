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
		} else
			value = cell.toString();

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

	//导出Excel文件
	public static void exportXLS(Workbook workbook, String filePath) {
		try {
			FileOutputStream fOut = new FileOutputStream(filePath);
			workbook.write(fOut);
			fOut.flush();
			fOut.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	//导出Excel文件
	public static void exportXLS(Workbook workbook, OutputStream out) {
		try {
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	//workbook转换成InputStream
	public static InputStream getInputStreamFromWorkbook(Workbook workbook) {
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

}
