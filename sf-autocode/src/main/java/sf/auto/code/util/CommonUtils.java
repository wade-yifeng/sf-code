package sf.auto.code.util;

import org.apache.commons.lang3.StringUtils;
import sf.auto.code.config.PropertiesLoad;
import sf.auto.code.exps.AutoCodeException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nijianfeng on 16/12/23.
 */
public class CommonUtils {

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
