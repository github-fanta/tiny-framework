package model;

import java.lang.reflect.Method;

/**
 * 处理器类
 * Created by liq on 2018/5/8.
 */
public class Handler {
    /**
     * 处理器类
     */
    private Class<?> controllerClass;

    /**
     * Method处理方法
     */
    private Method actionMethod;

    public Handler(Class<?> controllerClass, Method actionMethod) {
        this.controllerClass = controllerClass;
        this.actionMethod = actionMethod;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public Method getActionMethod() {
        return actionMethod;
    }
}
