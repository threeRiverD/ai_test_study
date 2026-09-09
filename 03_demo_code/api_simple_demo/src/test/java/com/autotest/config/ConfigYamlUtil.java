package com.autotest.config;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;

public class ConfigYamlUtil {
    private static Map<String,Object> configMap;
    static {
        Yaml yaml = new Yaml();
        try(InputStream inputStream = ConfigYamlUtil.class.getClassLoader().getResourceAsStream("application.yml")){
            configMap = yaml.load(inputStream);
        }catch (Exception e){
            throw new RuntimeException("读取yml配置失败",e);
        }
    }

    // 支持层级读取，例如 api.baseUrl
    public static String getValue(String key){
        String[] keys = key.split("\\.");
        Object temp = configMap;
        for(String k : keys){
            if(temp instanceof Map){
                temp = ((Map<?,?>)temp).get(k);
            }
        }
        return String.valueOf(temp);
    }
}
