import core.BeanCore;
import core.ClassCore;
import core.ConfigCore;
import core.RequestMappingCore;
import model.Handler;
import model.ModelAndView;
import model.Request;
import util.CollectionUtil;
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
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requsetPath = request.getPathInfo();
        Request requestBean = new Request(requsetPath);
        Handler handler = RequestMappingCore.getHandler(requestBean);

        //通过handler获取处理方法对象
        Method actionMethod = handler.getActionMethod();
        //获取处理器实例对象
        Object controllerBean = BeanCore.getBean(handler.getControllerClass());

        //反射执行处理方法
        Object result = ReflectionUtil.invokeMethod(controllerBean, actionMethod);

        if (result instanceof ModelAndView) {
            handleViewResult((ModelAndView) result, request, response);
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
