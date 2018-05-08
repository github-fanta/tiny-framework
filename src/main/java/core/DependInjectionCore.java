package core;

import annotation.Inject;
import util.CollectionUtil;
import util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 依赖注入核心
 * Created by liq on 2018/5/8.
 */
public final class DependInjectionCore {

    static {
        Map<Class<?>, Object> beanMap = BeanCore.getBeanMap();
        if (CollectionUtil.isNotEmpty(beanMap)){
            for(Map.Entry<Class<?>, Object> entry : beanMap.entrySet()){
                //获取Bean类和Bean实例
                Class<?> beanClass = entry.getKey();
                Object beanInstance = entry.getValue();
                //Bean类中获取成员变量
                Field[] fields = beanClass.getDeclaredFields();
                //遍历成员变量
                for (Field field:fields) {
                    //若成员变量有Inject注解，获取其类型，再从beanMap中找到其实例。
                    if (field.isAnnotationPresent(Inject.class)){
                        //返回该字段的类型的Class对象
                        Class<?> beanFieldClass = field.getType();
                        Object beanFieldInstance = beanMap.get(beanFieldClass);
                        //如果该实例不为空，将其赋给Inject注解的成员变量
                        if (beanFieldInstance != null){
                            ReflectionUtil.setField(beanInstance, field, beanFieldInstance);
                        }
                    }
                }
            }
        }
    }
}
