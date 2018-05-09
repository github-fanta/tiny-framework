import core.*;
import model.*;
import util.CollectionUtil;
import util.JsonUtil;
import util.ReflectionUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by liq on 2018/5/8.
 */
@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet{
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        CoreLoader.init();
        ServletContext servletContext = servletConfig.getServletContext();
        ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        jspServlet.addMapping(ConfigCore.getAppJspPath()+"*");
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        defaultServlet.addMapping(ConfigCore.getAppAssetPath()+"*");

        //文件上传初始化
        UploadCore.init(servletContext);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ServletCore.init(request, response); //封装r&r放入当前线程，以便后续Service与Dao用到。
        try {
            /**
             * 获取参数
             */
            Param param;
            if (UploadCore.isMultipart(request)) {
                //有上传文件时
                param = UploadCore.createParam(request);
            } else {
                //普通表单文件
                param = ParamCore.createParam(request);
            }
            /**
             * 获取请求路径
             */
            String requsetPath = request.getPathInfo();

            /**
             * 执行处理器
             */
            Request requestBean = new Request(requsetPath);
            Handler handler = RequestMappingCore.getHandler(requestBean);

            if (handler != null) {
                //通过handler获取处理方法对象
                Method actionMethod = handler.getActionMethod();
                //获取处理器实例对象
                Object controllerBean = BeanCore.getBean(handler.getControllerClass());

                Object result;
                if (param.isEmpty()) {

                    result = ReflectionUtil.invokeMethod(controllerBean, actionMethod);         //反射执行处理方法，无参数
                } else {

                    result = ReflectionUtil.invokeMethod(controllerBean, actionMethod, param);  //反射执行处理方法，有参数
                }

                /**
                 * 处理返回结果
                 */
                if (result instanceof ModelAndView) {
                    handleViewResult((ModelAndView) result, request, response);
                } else if (result instanceof Json) {
                    handleJsonResult((Json) result, response);
                }
            }
        }finally {
            ServletCore.destroy();
        }
    }

    /**
     * 处理Json类型数据
     * @param result
     * @param response
     * @throws IOException
     */
    private void handleJsonResult(Json result, HttpServletResponse response) throws IOException {
        Object model = result.getModel();
        if (model != null){
            //设置响应头
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            //向浏览器写json数据
            PrintWriter writer = response.getWriter();
            String jsonStr = JsonUtil.toJson(model);
            writer.write(jsonStr);
            writer.flush();
            writer.close();
        }
    }

    /**
     * 处理Jsp视图
     * @param result
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    private void handleViewResult(ModelAndView result, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        //获取视图路径
        String viewPath = result.getViewPath();
        //区分重定向和转发（以“/”开头是转发，否则为重定向）
        if (viewPath.startsWith("/")){
            //重定向
            response.sendRedirect(request.getContextPath()+viewPath);
        }else{
            //请求转发
            Map<String, Object> model = result.getModel();
            if (CollectionUtil.isNotEmpty(model)) {
                for (Map.Entry<String, Object> entry : model.entrySet()) {
                    request.setAttribute(entry.getKey(), entry.getValue());
                }
            }
            request.getRequestDispatcher(ConfigCore.getAppJspPath()+viewPath).forward(request,response);
        }

    }
}
