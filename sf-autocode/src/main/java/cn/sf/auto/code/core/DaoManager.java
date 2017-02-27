package cn.sf.auto.code.core;

import cn.sf.auto.code.util.StringConstants;
import com.google.common.base.Splitter;
import cn.sf.auto.code.config.PropertiesLoad;
import cn.sf.auto.code.util.CommonUtils;

import java.util.List;
import java.util.stream.Collectors;

public class DaoManager {

    final static String daoPath = PropertiesLoad.getByKey("dao_path", Boolean.TRUE);
    final static String packageDao = PropertiesLoad.getByKey("package_dao", Boolean.TRUE);
    final static String daoPackageDomain = PropertiesLoad.getByKey("dao_package_domain", Boolean.TRUE);
    final static String daoPackageExtend = PropertiesLoad.getByKey("dao_package_extend", Boolean.TRUE);
    final static String managerPath = PropertiesLoad.getByKey("manager_path", Boolean.TRUE);
    final static String packageManager = PropertiesLoad.getByKey("package_manager", Boolean.TRUE);
    final static String managerPackageDao = PropertiesLoad.getByKey("manager_package_dao", Boolean.TRUE);

    public synchronized static void genDao() {
        //此处可能会抛出异常,不进行捕获了,请填写正确的值,true or false
        if (!Boolean.valueOf(PropertiesLoad.getByKey("gen_dao", Boolean.TRUE))) {
            return;
        }

        List<String> fileNames = Splitter.on(",")
                .omitEmptyStrings()
                .trimResults()
                .splitToList(PropertiesLoad.getByKey("dao_file_names", Boolean.TRUE))
                .stream()
                .collect(Collectors.toList());
        if(fileNames.contains("all")){
            CommonUtils.getAllTablesBySchema();
            fileNames = StringConstants.allTableNames;
        }

        fileNames.forEach(f -> {
            f = CommonUtils.firstUpper(CommonUtils.ruleConvert(f,StringConstants.UNDER_LINE,StringConstants.CAMEL));
            StringBuilder sbDao = new StringBuilder();
            sbDao.append("package " + packageDao + ";\r\n\r\n");
            sbDao.append("import lombok.extern.slf4j.Slf4j;\r\n");
            sbDao.append("import org.springframework.stereotype.Repository;\r\n");
            sbDao.append("import " + daoPackageExtend+";\r\n");
            sbDao.append("import " + daoPackageDomain + "." + CommonUtils.firstUpper(f) + ";\r\n\r\n");
            sbDao.append("@Repository\r\n");
            sbDao.append("@Slf4j\r\n");
            sbDao.append("public class " + f + "Dao extends "+
                    daoPackageExtend.substring(daoPackageExtend.lastIndexOf(".")+1) +"<" + f + "> {\r\n");
            sbDao.append("\r\n\r\n}");
            CommonUtils.genFile(daoPath + '/' + f + "Dao.java", sbDao.toString());
        });

    }

    public synchronized static void genManager() {
        //此处可能会抛出异常,不进行捕获了,请填写正确的值,true or false
        if (!Boolean.valueOf(PropertiesLoad.getByKey("gen_manager", Boolean.TRUE))) {
            return;
        }

        List<String> fileNames = Splitter.on(",")
                .omitEmptyStrings()
                .trimResults()
                .splitToList(PropertiesLoad.getByKey("manager_file_names", Boolean.TRUE))
                .stream()
                .collect(Collectors.toList());
        if(fileNames.contains("all")){
            CommonUtils.getAllTablesBySchema();
            fileNames = StringConstants.allTableNames;
        }
        fileNames.forEach(f -> {
            f = CommonUtils.firstUpper(CommonUtils.ruleConvert(f,StringConstants.UNDER_LINE,StringConstants.CAMEL));
            StringBuilder sbManager = new StringBuilder();
            sbManager.append("package " + packageManager + ";\r\n\r\n");
            sbManager.append("import lombok.extern.slf4j.Slf4j;\r\n");
            sbManager.append("import org.springframework.stereotype.Repository;\r\n");
            sbManager.append("import org.springframework.beans.factory.annotation.Autowired;\r\n");
            sbManager.append("import " + managerPackageDao + "." + f + "Dao;\r\n\r\n");
            sbManager.append("@Repository\r\n");
            sbManager.append("@Slf4j\r\n");
            sbManager.append("public class " + f + "Manager {\r\n\r\n");
            sbManager.append("    @Autowired\r\n");
            sbManager.append("    private " + f + "Dao " + f.substring(0, 1).toLowerCase() + f.substring(1) + "Dao;\r\n");
            sbManager.append("\r\n\r\n}");
            CommonUtils.genFile(managerPath + '/' + f + "Manager.java", sbManager.toString());
        });
    }

}
