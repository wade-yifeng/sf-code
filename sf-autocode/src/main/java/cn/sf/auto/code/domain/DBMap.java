package cn.sf.auto.code.domain;

import lombok.Data;

@Data
public class DBMap {
    private String field;//column名称
    private String type;//数据库类型
    private String memo;//注释
    private String munericLength;
    private String numericScale;
    private String isNullable;//是否可为空
    private String extra;
    private String isDefault;//默认值
    private String characterLength;

}