package core;

import annotation.Controller;
import annotation.Service;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import util.ClassUtil;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * 类管理核心
 * Created by liq on 2018/5/8.
 */
public final class ClassCore {
    private static final Set<Class<?>> CLASS_SET;

    static {
        //加载包下所有类到CLASS_SET中
        CLASS_SET = ClassUtil.getClassSet(ConfigCore.getAppBasePackage());
    }

    /**
     * 获取应用下所有带有Service注解的类
     */
    public static Set<Class<?>> getServiceClassSet(){
        Set<Class<?>> serviceClassSet = new HashSet<Class<?>>();
        for (Class cls : CLASS_SET) {
            if(cls.isAnnotationPresent(Service.class)){
                serviceClassSet.add(cls);
            }
        }
        return serviceClassSet;
    }

    /**
     * 获取应用下所有带有Controller注解的类
     */
    public static Set<Class<?>> getControllerClassSet(){

        Set<Class<?>> controllerClassSet = new HashSet<Class<?>>();
        for (Class cls : CLASS_SET) {
            if (cls.isAnnotationPresent(Controller.class)){
                controllerClassSet.add(cls);
            }
        }
        return controllerClassSet;
    }

    /**
     * 获取应用下所有Bean类,包括Service Controller等
     */
    public static Set<Class<?>> getBeanClassSet(){
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        classSet.addAll(getControllerClassSet());
        classSet.addAll(getServiceClassSet());
        return classSet;
    }

    /**
     * 获取应用包名下某父类（或接口）的所有子类（或实现类）
     */
    public static Set<Class<?>> getClassSetBySuper(Class<?> superClass){
        HashSet<Class<?>> classSet = new HashSet<Class<?>>();
        for(Class<?> cls : CLASS_SET){
            if (superClass.isAssignableFrom(cls) && !superClass.equals(cls)){
                classSet.add(cls);
            }
        }
        return classSet;
    }

    /**
     * 获取某注解类
     */
    public static Set<Class<?>> getClassSetByAnnotation(Class<? extends Annotation> annotationClass){
        HashSet<Class<?>> classSet = new HashSet<Class<?>>();
        for(Class<?> cls : CLASS_SET){
            if (cls.isAnnotationPresent(annotationClass)){
                classSet.add(cls);
            }
        }
        return classSet;
    }
}
