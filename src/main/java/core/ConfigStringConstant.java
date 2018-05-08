package core;

/**
 * 配置项常量
 * Created by liq on 2018/5/7.
 */
public interface ConfigStringConstant {


    String CONFIG_FILE = "tiny.properties";                         //框架配置的文件名
    String JDBC_DRIVER = "tiny.framework.jdbc.driver";
    String JDBC_URL = "tiny.framework.jdbc.url";
    String JDBC_USERNAME = "tiny.framework.jdbc.username";
    String JDBC_PASSWORD = "tiny.framework.jdbc.password";
    String APP_BASE_PACKAGE = "tiny.framework.app.base_package";    //项目包名
    String APP_JSP_PATH = "tiny.framework.app.jsp_path";            //项目jsp路径配置  默认/WEB-INF/view/
    String APP_ASSET_PATH = "tiny.framework.app.asset_path";        //项目静态资源路径 默认/asset/
    String APP_UPLOAD_LIMIT = "tiny.framework.app.upload_limit";    //上传文件大小限制（单位：M）默认10M
}
