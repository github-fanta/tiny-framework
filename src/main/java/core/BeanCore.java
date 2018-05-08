package core;

import util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Bean核心类 将实例化的类放入静态Map中
 */
public final class BeanCore {
    /**
     * 定义Bean的Map，存放Bean类和Bean实例的映射关系
     */
    private static final Map<Class<?>, Object> BEAN_MAP = new HashMap<Class<?>,Object>();
    static {
        //实例化所有类,都是单例
        Set<Class<?>> beanClassSet = ClassCore.getBeanClassSet();
        for (Class<?> clazz : beanClassSet){
            BEAN_MAP.put(clazz, ReflectionUtil.newInstance(clazz));
        }
    }

    /**
     * 获取Bean映射
     */
    public static Map<Class<?>, Object> getBeanMap(){
        return BEAN_MAP;
    }

    /**
     * 获取某个Bean实例
     */
    public static <T> T getBean(Class<T> clazz){
        if (!BEAN_MAP.containsKey(clazz)){
            throw  new RuntimeException("can not get bean by class");
        }
        return (T) BEAN_MAP.get(clazz);
    }

    /**
     * 设置Bean实例
     */
    public static void setBean(Class<?> clazz, Object bean){
        BEAN_MAP.put(clazz, bean);
    }


}
