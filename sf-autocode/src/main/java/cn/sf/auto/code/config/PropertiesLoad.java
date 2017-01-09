package cn.sf.auto.code.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import cn.sf.auto.code.exps.AutoCodeException;

import java.io.IOException;
import java.util.Properties;

public class PropertiesLoad {

    private static Properties properties;

    public static void init(String propertiesPath){
        try {
            properties = PropertiesLoaderUtils.loadAllProperties(propertiesPath);
        } catch (IOException e) {
            String message = "PropertiesLoad load "+ propertiesPath  +" failed.";
            throw AutoCodeException.valueOf(message,e);
        }
    }

    public static String getByKey(String key, Boolean isVaildNull){
        if(properties==null){
            String message = "properties is null. please execute init first.";
            throw AutoCodeException.valueOf(message);
        }
        String ret = properties.getProperty(key);
        if(isVaildNull){
            if(StringUtils.isEmpty(ret)){
                String message = key + " is null in properties. ";
                throw AutoCodeException.valueOf(message);
            }
        }
        return ret;
    }

}
