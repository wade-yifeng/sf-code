package cn.sf.auto.code.core;

import cn.sf.auto.code.config.PropertiesLoad;
import cn.sf.auto.code.util.CommonUtils;
import cn.sf.auto.code.util.StringConstants;
import com.google.common.base.Splitter;
import cn.sf.auto.code.exps.AutoCodeException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Domain2Mapper {

    private static Class clazz;
    private static List<Field> fields;  //remove serialVersionUID
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
        final String packageMapper = PropertiesLoad.getByKey("package_mapper", Boolean.TRUE);
        final List<String> classNames = Splitter.on(",")
                .omitEmptyStrings()
                .trimResults()
                .splitToList(PropertiesLoad.getByKey("mapper_class_names", Boolean.TRUE))
                .stream()
                .collect(Collectors.toList());

        classNames.forEach(c -> {
            try {
                clazz = Class.forName(packageMapper + "." + c);
            } catch (Exception e) {
                String message = c + " class is not exist. ";
                throw AutoCodeException.valueOf(message, e);
            }
            fields = Arrays.asList(clazz.getDeclaredFields());
            fields = fields.stream()
                    .filter(f -> !(f.getName().equals("serialVersionUID")))
                    .collect(Collectors.toList());

            StringBuilder sb = new StringBuilder();
            //加上xml的头
            sb.append(genHeadString());
            sb.append(genMapperStartString());
            sb.append(genResultMapString());
            sb.append(genTableNameString());
            sb.append(genColsString());
            sb.append(genColsAllString());
            sb.append(genValsString());
            sb.append(genDynamicConditionString());
            sb.append(genSetString());
            if (mapperIds.contains("create")) {
                sb.append(genInsertString());
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
            sb.append(genMapperEndString());

            CommonUtils.genFile(mapperPath + clazz.getSimpleName() + "Mapper.xml", sb.toString());
        });

    }


    private static String genHeadString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.mapper_Head);
        return sb.toString();
    }

    private static String genMapperStartString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.mapper_start.replace("${className}", clazz.getSimpleName()));
        return sb.toString();
    }

    private static String genTableNameString() {
        StringBuilder sb = new StringBuilder();
        String className = clazz.getSimpleName();
        className = CommonUtils.ruleConvert(className, StringConstants.CAMEL, StringConstants.UNDER_LINE);
        sb.append(MapperTemplate.sql_id_tb.replace("${tableName}", className.substring(1, className.length())));
        return sb.toString();
    }

    private static String genResultMapString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.resultMap_start.replace("${className}", clazz.getSimpleName()));
        fields.forEach(f ->
                sb.append(
                        MapperTemplate.resultMap_result
                                .replace("${fieldName}", f.getName())
                                .replace("${columnName}", CommonUtils.ruleConvert(f.getName(), StringConstants.CAMEL, StringConstants.UNDER_LINE))
                ));
        sb.append(MapperTemplate.resultMap_end);
        return sb.toString();
    }

    private static String genColsString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.sql_id_cols_start);
        fields.forEach(f -> {
            if (f.getName().equals("id")) {
                //跳过
            } else {
                sb.append(
                        MapperTemplate.sql_id_cols_value
                                .replace("${columnName}", CommonUtils.ruleConvert(f.getName(), StringConstants.CAMEL, StringConstants.UNDER_LINE))
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
            if (f.getName().equals("id")) {
                //跳过
            } else if (dateNowVal.contains(f.getName())) {
                sb.append(
                        MapperTemplate.sql_id_vals_value
                                .replace("${fieldName}", "now()")
                );
            } else {
                sb.append(
                        MapperTemplate.sql_id_vals_value
                                .replace("${fieldName}", f.getName())
                );
            }

        });
        StringBuilder finalSb = new StringBuilder(sb.substring(0, sb.length() - 1));
        finalSb.append(MapperTemplate.sql_id_vals_end);
        return finalSb.toString().replace("#{now()}", "now()");
    }

    private static String genDynamicConditionString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.sql_id_dynamic_condition_start);
        for (int i = 0; i < fields.size(); i++) {
            if (!dynamicCondition.contains(fields.get(i).getName())) {
                sb.append(
                        MapperTemplate.sql_id_dynamic_condition_value
                                .replace("${fieldName}", fields.get(i).getName())
                                .replace("${columnName}", CommonUtils.ruleConvert(fields.get(i).getName(), StringConstants.CAMEL, StringConstants.UNDER_LINE))
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
            if (!dynamicCondition.contains(fields.get(i).getName())) {
                sb.append(
                        MapperTemplate.sql_id_set_value
                                .replace("${fieldName}", fields.get(i).getName())
                                .replace("${columnName}", CommonUtils.ruleConvert(fields.get(i).getName(), StringConstants.CAMEL, StringConstants.UNDER_LINE))
                );
            }
        }
        sb.append(MapperTemplate.sql_id_set_end);
        return sb.toString();
    }

    private static String genInsertString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.insert_id_create.replace("${className}", clazz.getSimpleName()));
        return sb.toString();
    }

    private static String genPagingString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.paging_id_create.replace("${className}", clazz.getSimpleName()));
        return sb.toString();
    }

    private static String genCountString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.count_id_create.replace("${className}", clazz.getSimpleName()));
        return sb.toString();
    }

    private static String genLoadString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.load_id_create.replace("${className}", clazz.getSimpleName()));
        return sb.toString();
    }

    private static String genListString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.list_id_create.replace("${className}", clazz.getSimpleName()));
        return sb.toString();
    }

    private static String genDeleteString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.delete_id_create.replace("${className}", clazz.getSimpleName()));
        return sb.toString();
    }

    private static String genDeletesString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MapperTemplate.deletes_id_create.replace("${className}", clazz.getSimpleName()));
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

        public static String resultMap_start = "\t<resultMap id=\"${className}Map\" type=\"${className}\">\r\n";
        public static String resultMap_result = "\t\t<result column=\"${columnName}\" property=\"${fieldName}\"/>\r\n";
        public static String resultMap_end = "\t</resultMap>\r\n";

        public static String sql_id_tb = "\t<sql id=\"tb\">\r\n\t\t${tableName}\r\n\t</sql>\r\n";

        public static String sql_id_cols_start = "\t<sql id=\"cols\">\r\n\t\t";
        public static String sql_id_cols_value = "${columnName},";
        public static String sql_id_cols_end = "\r\n\t</sql>\r\n";

        public static String sql_id_cols_all =
                "\t<sql id=\"cols_all\">\r\n" +
                "\t\tid,\r\n" +
                "\t\t<include refid=\"cols\"/>\r\n" +
                "\t</sql>\r\n";

        public static String sql_id_vals_start = "\t<sql id=\"vals\">\r\n\t\t";
        public static String sql_id_vals_value = "#{${fieldName}},";
        public static String sql_id_vals_end = "\r\n\t</sql>\r\n";

        public static String sql_id_dynamic_condition_start = "\t<sql id=\"dynamic_condition\">\r\n";
        public static String sql_id_dynamic_condition_value = "\t\t<if test=\"${fieldName} != null \">AND ${columnName} = #{${fieldName}}</if>\r\n";
        public static String sql_id_dynamic_condition_end = "\t</sql>\r\n";

        public static String sql_id_set_start = "\t<sql id=\"set\">\r\n";
        public static String sql_id_set_value = "\t\t<if test=\"${fieldName} !=null\">,${columnName} = #{${fieldName}}</if>\r\n";
        public static String sql_id_set_end = "\t</sql>\r\n";

        public static String insert_id_create =
                        "\t<insert id=\"create\" parameterType=\"${className}\" keyProperty=\"id\" useGeneratedKeys=\"true\">\r\n" +
                        "\t\tINSERT INTO\r\n" +
                        "\t\t<include refid=\"tb\"/>\r\n" +
                        "\t\t(\r\n" +
                        "\t\t<include refid=\"cols\"/>\r\n" +
                        "\t\t)\r\n" +
                        "\t\tVALUES\r\n" +
                        "\t\t(\r\n" +
                        "\t\t<include refid=\"vals\"/>\r\n" +
                        "\t\t)\r\n" +
                        "\t</insert>\r\n";
        public static String paging_id_create =
                        "\t<select id=\"paging\" parameterType=\"map\" resultMap=\"${className}Map\">\r\n" +
                        "\t\tSELECT \r\n" +
                        "\t\t<include refid=\"cols_all\"/> \r\n" +
                        "\t\tFROM \r\n" +
                        "\t\t<include refid=\"tb\"/>\r\n" +
                        "\t\tWHERE 1=1 \r\n" +
                        "\t\t<include refid=\"dynamic_condition\"/>\r\n" +
                        "\t\tLIMIT #{offset}, #{limit}\r\n" +
                        "\t</select>\r\n";
        public static String count_id_create =
                        "\t<select id=\"count\" parameterType=\"map\" resultType=\"long\">\r\n" +
                        "\t\tSELECT \r\n" +
                        "\t\tcount(1) \r\n" +
                        "\t\tFROM \r\n" +
                        "\t\t<include refid=\"tb\"/> \r\n" +
                        "\t\tWHERE 1=1 \r\n" +
                        "\t\t<include refid=\"dynamic_condition\"/>\r\n" +
                        "\t</select>\r\n";
        public static String load_id_create =
                        "\t<select id=\"load\" parameterType=\"long\" resultMap=\"${className}Map\">\r\n" +
                        "\t\tSELECT \r\n" +
                        "\t\t<include refid=\"cols_all\" /> \r\n" +
                        "\t\tFROM \r\n" +
                        "\t\t<include refid=\"tb\" /> \r\n" +
                        "\t\tWHERE id = #{id}\r\n" +
                        "\t</select>\r\n";
        public static String list_id_create =
                        "\t<select id=\"list\" parameterType=\"map\" resultMap=\"${className}Map\">\r\n" +
                        "\t\tSELECT \r\n" +
                        "\t\t<include refid=\"cols_all\"/> \r\n" +
                        "\t\tFROM \r\n" +
                        "\t\t<include refid=\"tb\"/>\r\n" +
                        "\t\tWHERE 1=1 \r\n" +
                        "\t\t<include refid=\"dynamic_condition\"/>\r\n" +
                        "\t</select>\r\n";
        public static String delete_id_create =
                        "\t<delete id=\"delete\" parameterType=\"long\">\r\n" +
                        "\t\tDELETE FROM \r\n" +
                        "\t\t<include refid=\"tb\"/> \r\n" +
                        "\t\tWHERE id = #{id}\r\n" +
                        "\t</delete>\r\n";

        public static String deletes_id_create =
                        "\t<delete id=\"deletes\" parameterType=\"list\">\n" +
                        "\t\tDELETE FROM\n" +
                        "\t\t<include refid=\"tb\"/>\n" +
                        "\t\tWHERE\n" +
                        "\t\tid\n" +
                        "\t\tIN (\n" +
                        "\t\t\t<foreach collection=\"list\" index=\"index\" item=\"id\" separator=\",\">\n" +
                        "\t\t\t\t#{id}\n" +
                        "\t\t\t</foreach>\n" +
                        "\t\t)\n" +
                        "\t</delete>";

    }

}