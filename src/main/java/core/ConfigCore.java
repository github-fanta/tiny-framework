package core;

import util.PropsUtil;

import java.util.Properties;

/**
 * 属性文件获取核心
 * Created by liq on 2018/5/7.
 */
public final class ConfigCore {

    private static final Properties CONFIG_PROPS = PropsUtil.loadProps(ConfigStringConstant.CONFIG_FILE);

    /**
     * 获取上传文件大小限制(默认10M)
     */
    public static int getAppUploadLimit(){
        return PropsUtil.getInt(CONFIG_PROPS, ConfigStringConstant.APP_UPLOAD_LIMIT, 10);
    }

    /**
     * 获取JDBC驱动
     */
    public static String getJdbcDriver(){
        return PropsUtil.getString(CONFIG_PROPS, ConfigStringConstant.JDBC_DRIVER);
    }

    /**
     * 获取JDBC URL
     */
    public static String getJdbcUrl(){
        return PropsUtil.getString(CONFIG_PROPS, ConfigStringConstant.JDBC_URL);
    }
    /**
     * 获取JDBC 用户名
     */
    public static String getJdbcUsername(){
        return PropsUtil.getString(CONFIG_PROPS, ConfigStringConstant.JDBC_USERNAME);
    }

    /**
     * 获取JDBC 密码
     */
    public static String getJdbcPassword(){
        return PropsUtil.getString(CONFIG_PROPS, ConfigStringConstant.JDBC_PASSWORD);
    }

    /**
     * 获取应用基础包名
     */
    public static String getAppBasePackage(){
        return PropsUtil.getString(CONFIG_PROPS, ConfigStringConstant.APP_BASE_PACKAGE);
    }

    /**
     * 获取应用 JSP 路径
     */
    public static String getAppJspPath(){
        return PropsUtil.getString(CONFIG_PROPS, ConfigStringConstant.APP_JSP_PATH, "/WEB-INF/view/");
    }

    /**
     * 获取静态资源路径
     */
    public static String getAppAssetPath(){
        return PropsUtil.getString(CONFIG_PROPS, ConfigStringConstant.APP_ASSET_PATH, "/asset/");
    }
}
