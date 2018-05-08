package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 字符串工具类
 * Created by liq on 2018/5/7.
 */
public final class StringUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringUtil.class);

    /**
     * 字符串分隔符
     */
    public static final String SEPARATOR = String.valueOf((char)29);

    public static boolean isEmpty(String str){

        if (str != null && !str.trim().equals("")){
            return false;
        }
        return true;
    }

    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }

    /**
     * 根据所给字符串切分
     * @param sourceStr
     * @param splitStr
     * @return
     */
    public static String[] splitString(String sourceStr, String splitStr){

        if (sourceStr == null && splitStr == null){
            LOGGER.error("split string failure");
            throw new RuntimeException();
        }

        return sourceStr.split(splitStr);
    }
}