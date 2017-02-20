package cn.sf.auto.code.util;

import cn.sf.auto.code.config.PropertiesLoad;
import cn.sf.auto.code.domain.DBMap;
import cn.sf.auto.code.exps.AutoCodeException;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    public static void getTableInfoByTableName(String tableName) {
        System.out.println(tableName + " start #################################");
        System.out.println("execute sql: " +  StringConstants.selectSQL + "'" + tableName + "'");
        try {
            PreparedStatement ps = StringConstants.conn.prepareStatement(StringConstants.selectSQL + "'" + tableName + "'");
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
            StringConstants.tableMap.put(tableName, tableMeta);

            //获取表描述
            ps = StringConstants.conn.prepareStatement("SELECT table_comment FROM Information_schema.tables WHERE  table_Name = " + "'" + tableName + "'");
            rs = ps.executeQuery();
            while (rs.next()) {
                StringConstants.tableCommentMap.put(tableName, rs.getString(1));
            }
            CommonUtils.close(rs, ps, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getAllTablesBySchema(){
        try {
            //获取表名列表
            PreparedStatement ps = StringConstants.conn.prepareStatement("SELECT table_name FROM Information_schema.tables WHERE  table_schema = " + "'" + PropertiesLoad.getByKey("db_schema", Boolean.TRUE) + "'");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                StringConstants.allTableNames.add(rs.getString(1));
            }
            CommonUtils.close(rs, ps, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void genFile(String basePath, String content) {
        try {
            System.out.println("开始生成文件:"+basePath);
            FileOutputStream fos = new FileOutputStream(new File(basePath));
            Writer os = new OutputStreamWriter(fos, "utf-8");
            os.write(content);
            os.flush();
            fos.close();
            System.out.println("成功生成文件:"+basePath);
        } catch (Exception e) {
            String message = "gen file failed. ";
            throw AutoCodeException.valueOf(message, e);
        }
    }

    public static String ruleConvert(String input, String from, String to) {
        if(StringUtils.isEmpty(from)||StringUtils.isEmpty(to)){
            return input;
        }

        if (from.equals(to)) {
            return input;
        }
        if (from.equals(StringConstants.UNDER_LINE) && to.equals(StringConstants.CAMEL)) {
            Pattern p = Pattern.compile("_[a-zA-Z]");
            Matcher match = p.matcher(input);
            while (match.find()) {
                String str = match.group();
                input = input.replaceFirst(str, str.substring(str.indexOf("_") + 1).toUpperCase());
            }
            return input;
        }
        if (from.equals(StringConstants.CAMEL) && to.equals(StringConstants.UNDER_LINE) ) {
            Pattern p = Pattern.compile("[A-Z]");
            Matcher match = p.matcher(input);
            while (match.find()) {
                String str = match.group();
                input = input.replaceFirst(str, "_" + str.toLowerCase());
            }
            return input;
        }

        String message = "from "+from+" to "+to+" is wrong,not support. ";
        throw AutoCodeException.valueOf(message);
    }

    public static String firstUpper(String input){
        if(input==null||input.length()<1){
            String message = "upper input is null or ''. ";
            throw AutoCodeException.valueOf(message);
        }
        return input.substring(0,1).toUpperCase()+input.substring(1,input.length());
    }

    public static String firstLower(String input){
        if(input==null||input.length()<1){
            String message = "lower input is null or ''.";
            throw AutoCodeException.valueOf(message);
        }
        return input.substring(0,1).toLowerCase()+input.substring(1,input.length());
    }

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    PropertiesLoad.getByKey("db_url", Boolean.TRUE),
                    PropertiesLoad.getByKey("db_user", Boolean.TRUE),
                    PropertiesLoad.getByKey("db_pwd", Boolean.TRUE));
            return conn;
        } catch (Exception e) {
            String message = "get db connection failed.";
            throw AutoCodeException.valueOf(message);
        }
    }

    public static void close(ResultSet rs, PreparedStatement ps, Connection conn) {
        // 关闭记录集
        if (rs != null) {
            try {
                rs.close();
                rs = null;
            } catch (SQLException e) {
                String message = "close rs failed.";
                throw AutoCodeException.valueOf(message);
            }
        }
        // 关闭声明
        if (ps != null) {
            try {
                ps.close();
                ps = null;
            } catch (SQLException e) {
                String message = "close ps failed.";
                throw AutoCodeException.valueOf(message);
            }
        }
        // 关闭链接对象
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException e) {
                String message = "close conn failed.";
                throw AutoCodeException.valueOf(message);
            }
        }
    }

}
