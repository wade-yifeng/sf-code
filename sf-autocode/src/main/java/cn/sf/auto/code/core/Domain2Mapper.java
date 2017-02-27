package cn.sf.auto.code.core;

import cn.sf.auto.code.config.PropertiesLoad;
import cn.sf.auto.code.domain.DBMap;
import cn.sf.auto.code.util.CommonUtils;
import cn.sf.auto.code.util.StringConstants;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.stream.Collectors;

public class Domain2Mapper {

    private static String clazz;
    private static List<String> fields;  //remove serialVersionUID
    private static List<String> dateNowVal;
    private static List<String> dynamicCondition;
    private static List<String> mapperIds;


    static {
        dateNowVal = Splitter.on(",")
                .omitEmptyStrings()
                .trimResults()
                .splitToList(PropertiesLoad.getByKey("date_to_now", Boolean.TRUE))
                .stream()
                .collect(Collectors.toList());
        dynamicCondition = Splitter.on(",")
                .omitEmptyStrings()
                .trimResults()
                .splitToList(PropertiesLoad.getByKey("mapper_id_dynamic_condition_exclude", Boolean.TRUE))
                .stream()
                .collect(Collectors.toList());
        mapperIds = Splitter.on(",")
                .omitEmptyStrings()
                .trimResults()
                .splitToList(PropertiesLoad.getByKey("mapper_sql_ids", Boolean.TRUE))
                .stream()
                .collect(Collectors.toList());
    }

    public synchronized static void genMapper() {
        //此处可能会抛出异常,不进行捕获了,请填写正确的值,true or false
        if (!Boolean.valueOf(PropertiesLoad.getByKey("gen_mapper", Boolean.TRUE))) {
            return;
        }
        final String mapperPath = PropertiesLoad.getByKey("mapper_path", Boolean.TRUE);
        List<String> classNames = Splitter.on(",")
                .omitEmptyStrings()
                .trimResults()
                .splitToList(PropertiesLoad.getByKey("mapper_class_names", Boolean.TRUE))
                .stream()
                .collect(Collectors.toList());
        if(classNames.contains("all")){
            CommonUtils.getAllTablesBySchema();
            classNames = StringConstants.allTableNames;
        }

        classNames.forEach(c -> {
            StringConstants.tableCommentMap = Maps.newHashMap();
            StringConstants.tableMap = Maps.newHashMap();
            fields = Lists.newArrayList();
            clazz = CommonUtils.firstUpper(CommonUtils.ruleConvert(c, StringConstants.UNDER_LINE, StringConstants.CAMEL));
            //填充fields
            CommonUtils.getTableInfoByTableName(c);
            for(DBMap dbMap : StringConstants.tableMap.get(c)){
                fields.add(CommonUtils.ruleConvert(dbMap.getField(), StringConstants.UNDER_LINE, StringConstants.CAMEL));
            }

            StringBuilder sb = new StringBuilder();
            //加上xml的头
            sb.append(genHeadString());
            sb.append(genMapperStartString());
            sb.append(genResultMapString());
            sb.append(genTableNameString());
            sb.append(genColsString());
            sb.append(genColsAllString());
            sb.append(genValsString());
            sb.append(genValsListString());
            sb.append(genDynamicConditionString());
            sb.append(genSetString());
            if (mapperIds.contains("create")) {
                sb.append(genCreateString());
            }
            if (mapperIds.contains("creates")) {
                sb.append(genCreatesString());
            }
            if(mapperIds.contains("update")) {
                sb.append(genUpdateString());
            }
            if (mapperIds.contains("paging")) {
                sb.append(genPagingString());
            }
            if (mapperIds.contains("count")) {
                sb.append(genCountString());
            }
            if (mapperIds.contains("load")) {
                sb.append(genLoadString());
            }
            if (mapperIds.contains("list")) {
                sb.append(genListString());
            }
            if (mapperIds.contains("delete")) {
                sb.append(genDeleteString());
            }
            if(mapperIds.contains("deletes")){
                sb.append(genDeletesString());
            }
            sb.append(genMapperEndString());
            CommonUtils.genFile(mapperPath + '/' + clazz + "Mapper.xml", sb.toString());
        });

    }


    private static String genHeadString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.mapper_Head);
        return sb.toString();
    }

    private static String genMapperStartString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.mapper_start.replace("${className}", clazz));
        return sb.toString();
    }

    private static String genTableNameString() {
        StringBuilder sb = new StringBuilder();
        String className = CommonUtils.ruleConvert(CommonUtils.firstLower(clazz), StringConstants.CAMEL, StringConstants.UNDER_LINE);
        sb.append(MapperTemplate.sql_id_tb.replace("${tableName}", className));
        return sb.toString();
    }

    private static String genResultMapString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.resultMap_start.replace("${className}", clazz));
        fields.forEach(f ->
                sb.append(
                        MapperTemplate.resultMap_result
                                .replace("${fieldName}", f)
                                .replace("${columnName}", CommonUtils.ruleConvert(f, StringConstants.CAMEL, StringConstants.UNDER_LINE))
                ));
        sb.append(MapperTemplate.resultMap_end);
        return sb.toString();
    }

    private static String genColsString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.sql_id_cols_start);
        fields.forEach(f -> {
            if (f.equals("id")) {
                //跳过
            } else {
                sb.append(
                        MapperTemplate.sql_id_cols_value
                                .replace("${columnName}", CommonUtils.ruleConvert(f, StringConstants.CAMEL, StringConstants.UNDER_LINE))
                );
            }


        });
        StringBuilder finalSb = new StringBuilder(sb.substring(0, sb.length() - 1));
        finalSb.append(MapperTemplate.sql_id_cols_end);
        return finalSb.toString();
    }

    private static String genColsAllString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.sql_id_cols_all);
        return sb.toString();
    }

    private static String genValsString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.sql_id_vals_start);
        fields.forEach(f -> {
            if (f.equals("id")) {
                //跳过
            } else if (dateNowVal.contains(f)) {
                sb.append(
                        MapperTemplate.sql_id_vals_value
                                .replace("${fieldName}", "now()")
                );
            } else {
                sb.append(
                        MapperTemplate.sql_id_vals_value
                                .replace("${fieldName}", f)
                );
            }

        });
        StringBuilder finalSb = new StringBuilder(sb.substring(0, sb.length() - 1));
        finalSb.append(MapperTemplate.sql_id_vals_end);
        return finalSb.toString().replace("#{now()}", "now()");
    }

    private static String genValsListString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.sql_id_vals_list_start);
        fields.forEach(f -> {
            if (f.equals("id")) {
                //跳过
            } else if (dateNowVal.contains(f)) {
                sb.append(
                        MapperTemplate.sql_id_vals_list_value
                                .replace("${fieldName}", "now()")
                );
            } else {
                sb.append(
                        MapperTemplate.sql_id_vals_list_value
                                .replace("${fieldName}", f)
                );
            }

        });
        StringBuilder finalSb = new StringBuilder(sb.substring(0, sb.length() - 1));
        finalSb.append(MapperTemplate.sql_id_vals_list_end);
        return finalSb.toString().replace("#{item.now()}", "now()");
    }

    private static String genDynamicConditionString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.sql_id_dynamic_condition_start);
        for (int i = 0; i < fields.size(); i++) {
            if (!dynamicCondition.contains(fields.get(i))) {
                sb.append(
                        MapperTemplate.sql_id_dynamic_condition_value
                                .replace("${fieldName}", fields.get(i))
                                .replace("${columnName}", CommonUtils.ruleConvert(fields.get(i), StringConstants.CAMEL, StringConstants.UNDER_LINE))
                );
            }
        }
        sb.append(MapperTemplate.sql_id_dynamic_condition_end);
        return sb.toString();
    }

    private static String genSetString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.sql_id_set_start);
        for (int i = 0; i < fields.size(); i++) {
            if (!dynamicCondition.contains(fields.get(i))) {
                sb.append(
                        MapperTemplate.sql_id_set_value
                                .replace("${fieldName}", fields.get(i))
                                .replace("${columnName}", CommonUtils.ruleConvert(fields.get(i), StringConstants.CAMEL, StringConstants.UNDER_LINE))
                );
            }
        }
        sb.append(MapperTemplate.sql_id_set_end);
        return sb.toString();
    }

    private static String genCreateString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.insert_id_create.replace("${className}", clazz));
        return sb.toString();
    }

    private static String genCreatesString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.insert_id_creates);
        return sb.toString();
    }

    private static String genUpdateString() {
        StringBuilder sb = new StringBuilder();
        String tmp = "id=#{id}";
        for (String str1 : dateNowVal) {
            for (String str2 : fields) {
                if(str1.equals(str2)&&str1.matches("^update(.)*At$")){
                    tmp = str2+"=now()";
                    break;
                }
            }
        }
        sb.append(MapperTemplate.update_id_update.replace("${className}", clazz).replace("${updatePrefix}", tmp));
        return sb.toString();
    }

    private static String genPagingString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.paging_id_create.replace("${className}", clazz));
        return sb.toString();
    }

    private static String genCountString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.count_id_create.replace("${className}", clazz));
        return sb.toString();
    }

    private static String genLoadString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.load_id_create.replace("${className}", clazz));
        return sb.toString();
    }

    private static String genListString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.list_id_create.replace("${className}", clazz));
        return sb.toString();
    }

    private static String genDeleteString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.delete_id_create.replace("${className}", clazz));
        return sb.toString();
    }

    private static String genDeletesString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.deletes_id_create.replace("${className}", clazz));
        return sb.toString();
    }

    private static String genMapperEndString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.mapper_end);
        return sb.toString();
    }

    static class MapperTemplate {
        public static String mapper_Head = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\r\n";

        public static String mapper_start = "<mapper namespace=\"${className}\">\r\n";
        public static String mapper_end = "</mapper>\r\n";

        public static String resultMap_start = "    <resultMap id=\"${className}Map\" type=\"${className}\">\r\n";
        public static String resultMap_result = "        <result column=\"${columnName}\" property=\"${fieldName}\"/>\r\n";
        public static String resultMap_end = "    </resultMap>\r\n";

        public static String sql_id_tb = "    <sql id=\"tb\">\r\n        ${tableName}\r\n    </sql>\r\n";

        public static String sql_id_cols_start = "    <sql id=\"cols\">\r\n        ";
        public static String sql_id_cols_value = "${columnName},";
        public static String sql_id_cols_end = "\r\n    </sql>\r\n";

        public static String sql_id_cols_all =
                "    <sql id=\"cols_all\">\r\n" +
                "        id,\r\n" +
                "        <include refid=\"cols\"/>\r\n" +
                "    </sql>\r\n";

        public static String sql_id_vals_start = "    <sql id=\"vals\">\r\n        ";
        public static String sql_id_vals_value = "#{${fieldName}},";
        public static String sql_id_vals_end = "\r\n    </sql>\r\n";

        public static String sql_id_vals_list_start = "    <sql id=\"vals_list\">\r\n        (";
        public static String sql_id_vals_list_value = "#{item.${fieldName}},";
        public static String sql_id_vals_list_end = ")\r\n    </sql>\r\n";

        public static String sql_id_dynamic_condition_start = "    <sql id=\"dynamic_condition\">\r\n";
        public static String sql_id_dynamic_condition_value = "        <if test=\"${fieldName} != null \">AND ${columnName} = #{${fieldName}}</if>\r\n";
        public static String sql_id_dynamic_condition_end = "    </sql>\r\n";

        public static String sql_id_set_start = "    <sql id=\"set\">\r\n";
        public static String sql_id_set_value = "        <if test=\"${fieldName} !=null\">,${columnName} = #{${fieldName}}</if>\r\n";
        public static String sql_id_set_end = "    </sql>\r\n";

        public static String insert_id_create =
                        "    <insert id=\"create\" parameterType=\"${className}\" keyProperty=\"id\" useGeneratedKeys=\"true\">\r\n" +
                        "        INSERT INTO\r\n" +
                        "        <include refid=\"tb\"/>\r\n" +
                        "        (\r\n" +
                        "        <include refid=\"cols\"/>\r\n" +
                        "        )\r\n" +
                        "        VALUES\r\n" +
                        "        (\r\n" +
                        "        <include refid=\"vals\"/>\r\n" +
                        "        )\r\n" +
                        "    </insert>\r\n";
        public static String insert_id_creates =
                        "    <insert id=\"creates\" parameterType=\"list\">\r\n" +
                        "        INSERT INTO\r\n" +
                        "        <include refid=\"tb\"/>\r\n" +
                        "        (\r\n" +
                        "        <include refid=\"cols\"/>\r\n" +
                        "        )\r\n" +
                        "        VALUES\r\n" +
                        "        <foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\">\r\n" +
                        "            <include refid=\"vals_list\"/>\r\n" +
                        "        </foreach>\r\n"+
                        "    </insert>\r\n";
        public static String update_id_update =
                        "    <update id=\"update\" parameterType=\"${className}\">\r\n"+
                        "        UPDATE\r\n" +
                        "        <include refid=\"tb\"/>\r\n" +
                        "        <set>\r\n"+
                        "            id=#{id}\r\n"+
                        "            <include refid=\"set\"/>\r\n"+
                        "        </set>\r\n"+
                        "        WHERE id=#{id}\r\n"+
                        "    </update>\r\n";
        public static String paging_id_create =
                        "    <select id=\"paging\" parameterType=\"map\" resultMap=\"${className}Map\">\r\n" +
                        "        SELECT \r\n" +
                        "        <include refid=\"cols_all\"/> \r\n" +
                        "        FROM \r\n" +
                        "        <include refid=\"tb\"/>\r\n" +
                        "        WHERE 1=1 \r\n" +
                        "        <include refid=\"dynamic_condition\"/>\r\n" +
                        "        LIMIT #{offset}, #{limit}\r\n" +
                        "    </select>\r\n";
        public static String count_id_create =
                        "    <select id=\"count\" parameterType=\"map\" resultType=\"long\">\r\n" +
                        "        SELECT \r\n" +
                        "        count(1) \r\n" +
                        "        FROM \r\n" +
                        "        <include refid=\"tb\"/> \r\n" +
                        "        WHERE 1=1 \r\n" +
                        "        <include refid=\"dynamic_condition\"/>\r\n" +
                        "    </select>\r\n";
        public static String load_id_create =
                        "    <select id=\"load\" parameterType=\"long\" resultMap=\"${className}Map\">\r\n" +
                        "        SELECT \r\n" +
                        "        <include refid=\"cols_all\" /> \r\n" +
                        "        FROM \r\n" +
                        "        <include refid=\"tb\" /> \r\n" +
                        "        WHERE id = #{id}\r\n" +
                        "    </select>\r\n";
        public static String list_id_create =
                        "    <select id=\"list\" parameterType=\"map\" resultMap=\"${className}Map\">\r\n" +
                        "        SELECT \r\n" +
                        "        <include refid=\"cols_all\"/> \r\n" +
                        "        FROM \r\n" +
                        "        <include refid=\"tb\"/>\r\n" +
                        "        WHERE 1=1 \r\n" +
                        "        <include refid=\"dynamic_condition\"/>\r\n" +
                        "    </select>\r\n";
        public static String delete_id_create =
                        "    <delete id=\"delete\" parameterType=\"long\">\r\n" +
                        "        DELETE FROM \r\n" +
                        "        <include refid=\"tb\"/> \r\n" +
                        "        WHERE id = #{id}\r\n" +
                        "    </delete>\r\n";
        public static String deletes_id_create =
                        "    <delete id=\"deletes\" parameterType=\"list\">\r\n" +
                        "        DELETE FROM\r\n" +
                        "        <include refid=\"tb\"/>\r\n" +
                        "        WHERE\r\n" +
                        "        id\r\n" +
                        "        IN\r\n" +
                        "        (\r\n" +
                        "            <foreach collection=\"list\" index=\"index\" item=\"id\" separator=\",\">\r\n" +
                        "                #{id}\r\n" +
                        "            </foreach>\r\n" +
                        "        )\r\n" +
                        "    </delete>\r\n";

    }

}
