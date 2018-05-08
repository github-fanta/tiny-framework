package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 获取属性工具类
 * Created by liq on 2018/5/7.
 */
public class PropsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropsUtil.class);
    /**
     * 加载属性文件
     */
    public static Properties loadProps(String fileName){

        Properties props = null;
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if (is == null){
                throw new FileNotFoundException(fileName+" is not found");
            }
            props = new Properties();
            props.load(is);
        } catch (IOException e) {
                LOGGER.error("load Props failure", e);
        }finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOGGER.error("close input stream failure", e);
                }
            }
        }
        return props;
    }

    /**
     * 获取字符型属性（默认值为空字符串）
     */
    public static String getString(Properties props, String key){
        return getString(props, key, "");
    }

    /**
     * 获取字符型属性（指定默认值）
     */
    public static String getString(Properties props, String key, String defaultValue){
        String result=null;
        if (props.containsKey(key)){
            result = props.getProperty(key);
        }
        return result;
    }

    /**
     * 获取整型属性（默认值为0）
     */
    public static int getInt(Properties props, String key){
        return getInt(props, key,0);
    }

    /**
     * 获取整型属性（指定默认值）
     */
    public static int getInt(Properties props, String key, int defaultValue){

        int result = defaultValue;
        if (props.containsKey(key)){
            result = CastUtil.castInt(props.getProperty(key));
        }
        return result;
    }

    /**
     * 获取布尔型属性（默认值为false)
     */
    public static boolean getBoolean(Properties props, String key){
        return getBoolean(props, key,false);
    }

    /**
     *获取布尔型属性（可指定默认值）
     */
    public static boolean getBoolean(Properties props, String key, boolean defaultValue){

        boolean result = defaultValue;
        if (props.containsKey(key)){
            result = CastUtil.castBoolean(props.getProperty(key));
        }
        return result;
    }
}
