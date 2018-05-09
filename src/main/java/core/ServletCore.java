package core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 *  HttpServletRequest HttpServletResponse封装，用于后续Service，DAO使用。
 * Created by liq on 2018/5/9.
 */
public final class ServletCore {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServletCore.class);

    /**
     * 每个线程独立拥有一份ServletHelper实例
     */
    private static final ThreadLocal<ServletCore> SERVLET_CORE_THREAD_LOCAL = new ThreadLocal<ServletCore>();
    private HttpServletRequest request;
    private HttpServletResponse response;

    public ServletCore(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    /**
     * 初始化
     */
    public static void init(HttpServletRequest request, HttpServletResponse response){
        SERVLET_CORE_THREAD_LOCAL.set(new ServletCore(request, response));
    }

    /**
     * 销毁
     */
    public static void destroy(){
        SERVLET_CORE_THREAD_LOCAL.remove();
    }

    /**
     * 将属性放入Request中
     */
    public static void setRequestAttribute(String key, Object value){
        getRequest().setAttribute(key, value);
    }

    /**
     * 从Request中获取属性
     */
    public static <T> T getRequestAttribute(String key){
        return (T)getRequest().getAttribute(key);
    }

    /**
     * 从Reqeust中移除属性
     */
    public static void removeRequestAttribute(String key){
        getRequest().removeAttribute(key);
    }

    /**
     * 发送重定向响应
     */
    public static void sendRedirect(String location){
        try {
            getResponse().sendRedirect(getRequest().getContextPath()+location);
        } catch (IOException e) {
            LOGGER.error("redirect filure", e);
        }
    }

    /**
     * 将属性放入Session中
     */
    public static void setSessionAttribute(String key, Object value){
        getSession().setAttribute(key, value);
    }

    /**
     * 从Session中获取属性
     */
    public static <T> T getSessionAttribute(String key){
        return (T)getSession().getAttribute(key);
    }

    /**
     * 从Session中移除属性
     */
    public static void removeSessionAttribute(String key){
        getSession().removeAttribute(key);
    }

    /**
     * 使Session失效
     */
    public static void invalidateSession(){
        getSession().invalidate();
    }


    /**
     * 获取Request对象
     */
    private static HttpServletRequest getRequest(){
        return SERVLET_CORE_THREAD_LOCAL.get().request;
    }

    /**
     * 获取Response对象
     */
    private static HttpServletResponse getResponse(){
        return SERVLET_CORE_THREAD_LOCAL.get().response;
    }

    /**
     * 获取Session对象
     */
    private static HttpSession getSession(){
        return getRequest().getSession();
    }

    /**
     * 获取ServletContext对象
     */
    private static ServletContext getServletContext(){
        return getRequest().getServletContext();
    }
    /**
     * 获取上下文路径ContextPath
     */
    public static String getContextPath(){
        return getRequest().getContextPath();
    }
}
