package cn.sf.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelExportField {
	/**
	 * excel字段描述
	 * 
	 * @return
	 */
	String desc() default "";

	/**
	 * excel的cellIndex
	 */
	int cellIndex() default 0;
}
