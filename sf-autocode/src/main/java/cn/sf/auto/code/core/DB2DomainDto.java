package cn.sf.auto.code.core;

import cn.sf.auto.code.config.PropertiesLoad;
import cn.sf.auto.code.util.CommonUtils;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DB2DomainDto {

    private static Map<String, List<DBMap>> tableMap = Maps.newHashMap();
    private static Map<String, String> tableCommentMap = Maps.newHashMap();
    private static Map<String, String> typeMap = Maps.newHashMap();

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

    public synchronized static void genDomain() {
        //此处可能会抛出异常,不进行捕获了,请填写正确的值,true or false
        if (!Boolean.valueOf(PropertiesLoad.getByKey("gen_domain", Boolean.TRUE))) {
            return;
        }

        final String domainPath = PropertiesLoad.getByKey("domain_path", Boolean.TRUE);
        final String packageDomain = PropertiesLoad.getByKey("package_domain", Boolean.TRUE);
        final String dbNameRule = PropertiesLoad.getByKey("db_name_rule", Boolean.TRUE);
        final String javaNameRule = PropertiesLoad.getByKey("java_name_rule", Boolean.TRUE);
        final List<String> tableNames = Splitter.on(",")
                .omitEmptyStrings()
                .trimResults()
                .splitToList(PropertiesLoad.getByKey("domain_table_names", Boolean.TRUE))
                .stream()
                .collect(Collectors.toList());

        tableNames.forEach(t -> {
            excuteSql(CommonUtils.getConnection(), t);
        });

        tableNames.forEach(t -> {
            List<DBMap> dbMaps = tableMap.get(t);
            //类名第一个字母大写
            String className = CommonUtils.ruleConvert(t.replaceFirst(t.substring(0, 1), t.substring(0, 1).toUpperCase()),dbNameRule,javaNameRule);
            StringBuilder sbDomain = new StringBuilder();
            sbDomain.append("package " + packageDomain + ";\r\n\r\n");
            sbDomain.append("import lombok.Data;\r\n\r\n");
            sbDomain.append("import java.io.Serializable;\r\n");
            sbDomain.append("import java.util.Date;\r\n\r\n");
            if (!StringUtils.isBlank(tableCommentMap.get(t))) {
                sbDomain.append("//" + tableCommentMap.get(t) + "\r\n");
            }
            sbDomain.append("@Data\r\n");
            sbDomain.append("public class " + className + " implements Serializable {\r\n");
            sbDomain.append("\tprivate static final long serialVersionUID = 1L;\r\n\r\n");
            dbMaps.forEach(dbMap -> {
                String type = "";
                //末尾带有Id的属性类型为Long
                if (dbMap.getField().toLowerCase().lastIndexOf("id") > 0 || dbMap.getField().equals("id")) {
                    type = typeMap.get("long");
                } else {
                    type = typeMap.get(dbMap.getType());
                }
                sbDomain.append("\t" + "private " + type + " " + CommonUtils.ruleConvert(dbMap.getField(),dbNameRule,javaNameRule) + ";//" + dbMap.getMemo() + "\r\n");
            });
            sbDomain.append("}\r\n\r\n");
            CommonUtils.genFile(domainPath + className + ".java", sbDomain.toString());
        });
        tableCommentMap = Maps.newHashMap();
        tableMap = Maps.newHashMap();
    }

    public synchronized static void genDto() {
        //此处可能会抛出异常,不进行捕获了,请填写正确的值,true or false
        if (!Boolean.valueOf(PropertiesLoad.getByKey("gen_dto", Boolean.TRUE))) {
            return;
        }

        final String dtoPath = PropertiesLoad.getByKey("dto_path", Boolean.TRUE);
        final String packageDto = PropertiesLoad.getByKey("package_dto", Boolean.TRUE);
        final String dbNameRule = PropertiesLoad.getByKey("db_name_rule", Boolean.TRUE);
        final String javaNameRule = PropertiesLoad.getByKey("java_name_rule", Boolean.TRUE);
        final List<String> tableNames = Splitter.on(",")
                .omitEmptyStrings()
                .trimResults()
                .splitToList(PropertiesLoad.getByKey("dto_table_names", Boolean.TRUE))
                .stream()
                .collect(Collectors.toList());

        tableNames.forEach(t -> {
            excuteSql(CommonUtils.getConnection(), t);
        });

        tableNames.forEach(t -> {
            List<DBMap> dbMaps = tableMap.get(t);
            //类名第一个字母大写
            String className = CommonUtils.ruleConvert(t.replaceFirst(t.substring(0, 1), t.substring(0, 1).toUpperCase()),dbNameRule,javaNameRule);
            StringBuilder sbDto = new StringBuilder();
            sbDto.append("package " + packageDto + ";\r\n\r\n");
            sbDto.append("import lombok.Data;\r\n\r\n");
            sbDto.append("import java.io.Serializable;\r\n");
            sbDto.append("import java.util.Date;\r\n\r\n");
            if (!StringUtils.isBlank(tableCommentMap.get(t))) {
                sbDto.append("//" + tableCommentMap.get(t) + "\r\n");
            }
            sbDto.append("@Data\r\n");
            sbDto.append("public class " + className + "Dto" + " implements Serializable {\r\n");
            sbDto.append("\tprivate static final long serialVersionUID = 1L;\r\n\r\n");
            dbMaps.forEach(dbMap -> {
                String type = "";
                if (dbMap.getField().toLowerCase().lastIndexOf("id") > 0 || dbMap.getField().equals("id")) {
                    type = typeMap.get("long");
                } else {
                    type = typeMap.get(dbMap.getType());
                }
                sbDto.append("\t" + "private " + type + " " + CommonUtils.ruleConvert(dbMap.getField(),dbNameRule,javaNameRule) + ";//" + dbMap.getMemo() + "\r\n");
            });
            sbDto.append("}\r\n\r\n");
            CommonUtils.genFile(dtoPath + "/" + className + "Dto.java", sbDto.toString());
        });
        tableCommentMap = Maps.newHashMap();
        tableMap = Maps.newHashMap();
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
    private static final String selectSQL = "SELECT " +
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
            "WHERE  TABLE_SCHEMA='"+PropertiesLoad.getByKey("db_schema", Boolean.TRUE)+"' AND table_Name = ";

    public static void excuteSql(Connection conn, String tableName) {
        System.out.println(tableName + " start #################################");
        System.out.println("execute sql: " +  selectSQL + "'" + tableName + "'");
        try {
            PreparedStatement ps = conn.prepareStatement(selectSQL + "'" + tableName + "'");
            ResultSet rs = ps.executeQuery();
            List<DBMap> tableMeta = Lists.newArrayList();
            while (rs.next()) {
                DBMap dbMap = new DBMap();
                dbMap.setField(rs.getString(1));
                dbMap.setType(rs.getString(2));
                dbMap.setMemo(rs.getString(3));
                dbMap.setMunericLength(rs.getString(4));
                dbMap.setNumericScale(rs.getString(5));
                dbMap.setIsNullable(rs.getString(6));
                dbMap.setExtra(rs.getString(7));
                dbMap.setIsDefault(rs.getString(8));
                dbMap.setCharacterLength(rs.getString(9));
                tableMeta.add(dbMap);
                //打印数据库某个表每列的返回数据
                System.out.println(dbMap);
            }
            System.out.println(tableName + " end #################################");
            tableMap.put(tableName, tableMeta);

            //获取表描述
            ps = conn.prepareStatement("SELECT table_comment FROM Information_schema.tables WHERE  table_Name = " + "'" + tableName + "'");
            rs = ps.executeQuery();
            while (rs.next()) {
                tableCommentMap.put(tableName, rs.getString(1));
            }
            CommonUtils.close(rs, ps, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Data
    static class DBMap {
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

}
