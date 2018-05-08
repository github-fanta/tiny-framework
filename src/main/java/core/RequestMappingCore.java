package core;

import annotation.RequestMapping;
import model.Handler;
import model.Request;
import util.CollectionUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 控制器核心
 * Created by liq on 2018/5/8.
 */
public final class RequestMappingCore {

    /**
     * 存放请求与处理器之间的映射关系
     */
    private static final Map<Request, Handler> REQUEST_HANDLER_MAP = new HashMap<Request, Handler>();
    static {
        //获取所有的Controller类
        Set<Class<?>> controllerClassSet = ClassCore.getControllerClassSet();
        //不为空，遍历所有的Controller类
        if (CollectionUtil.isNotEmpty(controllerClassSet)){
            //获取每一个Controller类里面的所有方法
            for (Class<?> controllerClass : controllerClassSet){
                Method[] methods = controllerClass.getDeclaredMethods();
                //不为空，遍历这些方法
                for (Method method : methods){
                    //对于带有RequestMapping注解的方法
                    if (method.isAnnotationPresent(RequestMapping.class)){
                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                        //从RequestMapping注解中获取URL请求
                        String path = requestMapping.path();

                        if(path.matches("/\\w*")){
                            //封装请求路径到Request对象
                            Request request = new Request(path);
                            Handler handler = new Handler(controllerClass, method);
                            //将请求对象和处理器对象放入ACTION_MAP
                            REQUEST_HANDLER_MAP.put(request, handler);
                        }
                    }
                }
            }
        }
    }

    public static Handler getHandler(Request requestBean) {
        return REQUEST_HANDLER_MAP.get(requestBean);
    }
}
