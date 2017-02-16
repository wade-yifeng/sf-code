package cn.sf.auto.code.util;

import cn.sf.auto.code.config.PropertiesLoad;
import cn.sf.auto.code.domain.DBMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class StringConstants {

    public static String CAMEL = "camel";
    public static String UNDER_LINE = "under_line";

    public static Connection conn = CommonUtils.getConnection();


    public static Map<String, List<DBMap>> tableMap = Maps.newHashMap();
    public static Map<String, String> tableCommentMap = Maps.newHashMap();
    public static List<String> allTableNames = Lists.newArrayList();

    public static Map<String, String> typeMap = Maps.newHashMap();
    static {
        typeMap.put("datetime", "Date");
        typeMap.put("date", "Date");
        typeMap.put("varchar", "String");
        typeMap.put("tinyint", "Integer");
        typeMap.put("int", "Integer");
        typeMap.put("long", "Long");
        typeMap.put("bigint", "Long");
        typeMap.put("smallint", "Integer");
    }

    private static final String SELECT_SQL_FIELD = " column_name as field,";
    private static final String SELECT_SQL_TYPE = " data_type as type,";
    private static final String SELECT_SQL_MEMO = " column_comment as memo,";
    private static final String SELECT_SQL_MUNERIC_LENGTH = " numeric_precision as munericLength,";
    private static final String SELECT_SQL_NUMERIC_SCALE = " numeric_scale as numericScale, ";
    private static final String SELECT_SQL_ISNULLABLE = " is_nullable as isNullable,";
    private static final String SELECT_SQL_EXTRA = " CASE WHEN extra = 'auto_increment' THEN 1 ELSE 0 END as extra,";
    private static final String SELECT_SQL_ISDEFAULT = " column_default as isDefault,";
    private static final String SELECT_SQL_CHARACTER_LENGTH = " character_maximum_length  AS characterLength ";
    public static final String selectSQL = "SELECT " +
            SELECT_SQL_FIELD +
            SELECT_SQL_TYPE +
            SELECT_SQL_MEMO +
            SELECT_SQL_MUNERIC_LENGTH +
            SELECT_SQL_NUMERIC_SCALE +
            SELECT_SQL_ISNULLABLE +
            SELECT_SQL_EXTRA +
            SELECT_SQL_ISDEFAULT +
            SELECT_SQL_CHARACTER_LENGTH +
            " FROM Information_schema.columns " +
            "WHERE  TABLE_SCHEMA='"+ PropertiesLoad.getByKey("db_schema", Boolean.TRUE)+"' AND table_Name = ";


}
