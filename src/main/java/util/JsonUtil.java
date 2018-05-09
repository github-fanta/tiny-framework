package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Json工具类
 * Created by liq on 2018/5/9.
 */
public class JsonUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * POJO 转 Json
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String toJson(Object obj){
        String result = null;
        try {
            result = OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOGGER.error("convert POJO to Json failure", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    public static <T> T toPojo(String str, Class<T> classType){
        T pojo = null;
        try {
            pojo = OBJECT_MAPPER.readValue(str, classType);
        } catch (IOException e) {
            LOGGER.error("convert Json to POJO failure", e);
            throw new RuntimeException(e);
        }
        return pojo;
    }
}
