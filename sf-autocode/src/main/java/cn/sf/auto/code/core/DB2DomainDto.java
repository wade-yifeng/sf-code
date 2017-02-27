package cn.sf.auto.code.core;

import cn.sf.auto.code.config.PropertiesLoad;
import cn.sf.auto.code.domain.DBMap;
import cn.sf.auto.code.util.CommonUtils;
import cn.sf.auto.code.util.StringConstants;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class DB2DomainDto {

    final static String dbNameRule = PropertiesLoad.getByKey("db_name_rule", Boolean.TRUE);
    final static String javaNameRule = PropertiesLoad.getByKey("java_name_rule", Boolean.TRUE);
    final static String domainPath = PropertiesLoad.getByKey("domain_path", Boolean.TRUE);
    final static String packageDomain = PropertiesLoad.getByKey("package_domain", Boolean.TRUE);
    final static String dtoPath = PropertiesLoad.getByKey("dto_path", Boolean.TRUE);
    final static String packageDto = PropertiesLoad.getByKey("package_dto", Boolean.TRUE);
    final static String isExtends = PropertiesLoad.getByKey("extends_domain", Boolean.TRUE);


    public synchronized static void genDomain() {
        //此处可能会抛出异常,不进行捕获了,请填写正确的值,true or false
        if (!Boolean.valueOf(PropertiesLoad.getByKey("gen_domain", Boolean.TRUE))) {
            return;
        }

        List<String> tableNames = Splitter.on(",")
                .omitEmptyStrings()
                .trimResults()
                .splitToList(PropertiesLoad.getByKey("domain_table_names", Boolean.TRUE))
                .stream()
                .collect(Collectors.toList());
        if(tableNames.contains("all")){
            CommonUtils.getAllTablesBySchema();
            tableNames = StringConstants.allTableNames;
        }
        tableNames.forEach(t -> {
            CommonUtils.getTableInfoByTableName(t);
        });

        tableNames.forEach(t -> {
            List<DBMap> dbMaps = StringConstants.tableMap.get(t);
            //类名第一个字母大写
            String className = CommonUtils.ruleConvert(t.replaceFirst(t.substring(0, 1), t.substring(0, 1).toUpperCase()),dbNameRule,javaNameRule);
            StringBuilder sbDomain = new StringBuilder();
            sbDomain.append("package " + packageDomain + ";\r\n\r\n");
            sbDomain.append("import lombok.Data;\r\n");
            sbDomain.append("import lombok.NoArgsConstructor;\r\n\r\n");
            sbDomain.append("import java.io.Serializable;\r\n");
            sbDomain.append("import java.util.Date;\r\n\r\n");
            if (!StringUtils.isBlank(StringConstants.tableCommentMap.get(t))) {
                sbDomain.append("//" + StringConstants.tableCommentMap.get(t) + "\r\n");
            }
            sbDomain.append("@Data\r\n");
            sbDomain.append("@NoArgsConstructor\r\n");
            sbDomain.append("public class " + className + " implements Serializable {\r\n");
            sbDomain.append("    private static final long serialVersionUID = 1L;\r\n\r\n");

            genFieldString(sbDomain,dbMaps);

            sbDomain.append("}\r\n\r\n");
            CommonUtils.genFile(domainPath + "/" + className + ".java", sbDomain.toString());
        });
        StringConstants.tableCommentMap = Maps.newHashMap();
        StringConstants.tableMap = Maps.newHashMap();
    }

    public synchronized static void genDto() {
        //此处可能会抛出异常,不进行捕获了,请填写正确的值,true or false
        if (!Boolean.valueOf(PropertiesLoad.getByKey("gen_dto", Boolean.TRUE))) {
            return;
        }

        List<String> tableNames = Splitter.on(",")
                .omitEmptyStrings()
                .trimResults()
                .splitToList(PropertiesLoad.getByKey("dto_table_names", Boolean.TRUE))
                .stream()
                .collect(Collectors.toList());
        if(tableNames.contains("all")){
            CommonUtils.getAllTablesBySchema();
            tableNames = StringConstants.allTableNames;
        }
        tableNames.forEach(t -> {
            CommonUtils.getTableInfoByTableName(t);
        });

        tableNames.forEach(t -> {
            List<DBMap> dbMaps = StringConstants.tableMap.get(t);
            //类名第一个字母大写
            String className = CommonUtils.ruleConvert(t.replaceFirst(t.substring(0, 1), t.substring(0, 1).toUpperCase()),dbNameRule,javaNameRule);
            StringBuilder sbDto = new StringBuilder();
            sbDto.append("package " + packageDto + ";\r\n\r\n");
            sbDto.append("import lombok.Data;\r\n");
            sbDto.append("import lombok.NoArgsConstructor;\r\n\r\n");
            sbDto.append("import java.io.Serializable;\r\n");
            if("true".equals(isExtends)) {
                sbDto.append("import " + packageDomain + "." + className + ";\r\n");
            }
            sbDto.append("import java.util.Date;\r\n\r\n");
            if (!StringUtils.isBlank(StringConstants.tableCommentMap.get(t))) {
                sbDto.append("//" + StringConstants.tableCommentMap.get(t) + "\r\n");
            }
            sbDto.append("@Data\r\n");
            sbDto.append("@NoArgsConstructor\r\n");
            if(!"true".equals(isExtends)) {
                sbDto.append("public class " + className + "Dto" + " implements Serializable {\r\n");
            }else{
                sbDto.append("public class " + className + "Dto" + " extends "+ className + " {\r\n");
            }
            sbDto.append("    private static final long serialVersionUID = 1L;\r\n\r\n");
            if(!"true".equals(isExtends)) {
                genFieldString(sbDto,dbMaps);
            }
            sbDto.append("}\r\n\r\n");
            CommonUtils.genFile(dtoPath + "/" + className + "Dto.java", sbDto.toString());
        });
        StringConstants.tableCommentMap = Maps.newHashMap();
        StringConstants.tableMap = Maps.newHashMap();
    }

    private static void genFieldString(StringBuilder sbDto,List<DBMap> dbMaps){
        dbMaps.forEach(dbMap -> {
            String type = "";
            //末尾带有id的属性类型为Long
            if (dbMap.getField().toLowerCase().lastIndexOf("id") > 0 || dbMap.getField().equals("id")) {
                type = StringConstants.typeMap.get("long");
            } else {
                type = StringConstants.typeMap.get(dbMap.getType());
            }
            sbDto.append("    " + "private " + type + " " + CommonUtils.ruleConvert(dbMap.getField(), dbNameRule, javaNameRule) + ";//" + dbMap.getMemo() + "\r\n");
        });
    }

}
