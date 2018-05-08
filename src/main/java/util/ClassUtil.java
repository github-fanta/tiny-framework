package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by liq on 2018/5/8.
 */
public final class ClassUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassUtil.class);
    /**
     * 获取类加载器
     */
    public static ClassLoader getClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 获取指定包名下的所有类
     */
    public static Set<Class<?>> getClassSet(String packageName) {

        Set<Class<?>> classSet = new HashSet<Class<?>>();
        if (StringUtil.isEmpty(packageName)) {
            return classSet;
        }

        Enumeration<URL> urls = null;
        try {
            //获取当前目录下（不递归）的所有文件和目录的url
            urls = getClassLoader().getResources(packageName.replace(".", "/"));
            if (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                //file协议
                if ("file".equals(url.getProtocol())) {
                    //RFC1738规定url只能由字母，数字，以及一些特殊字符组成,其他的字符都要进行编码，空格编码之后的结果就是%20
                    String packagePath = url.getPath().replaceAll("%20", " ");
                    addFileClass(classSet, packagePath, packageName);
                } else if ("jar".equals(url.getProtocol())) {
                    //jar协议
                    JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                    if (jarURLConnection != null) {
                        JarFile jarFile = jarURLConnection.getJarFile();
                        if (jarFile != null) {
                            Enumeration<JarEntry> entries = jarFile.entries();
                            while (entries.hasMoreElements()) {
                                JarEntry jarEntry = entries.nextElement();
                                String jarEntryName = jarEntry.getName();// 类似：sun/security/internal/interfaces/TlsMasterSecret.class
                                if (jarEntryName.endsWith(".class")) {
                                    String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                                    addSingleClass(classSet, className);
                                }
                            }
                        }
                    }
                }
            }

            } catch(Exception e){
                LOGGER.error("get class set failure", e);
                throw new RuntimeException(e);
            }
            return classSet;
    }

    private static void addFileClass(Set<Class<?>> classSet, String packagePath, String wholeClassName) {
        //listFiles()返回某个目录下所有文件和目录的绝对路径，返回的是File数组
        File[] files = new File(packagePath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".class")
                        || pathname.isDirectory();
            }
        });

        for(File file : files) {
            String fileName = file.getName();
            if (file.isFile()){
                //是class文件
                String className = fileName.substring(0, fileName.lastIndexOf("."));
                className = wholeClassName + "." + className;
                addSingleClass(classSet, className);
            }else {
                //是目录
                String subPath = fileName;
                if (StringUtil.isNotEmpty(packagePath)) {
                    subPath = packagePath + "/" + subPath;
                }
                String subClassName = fileName;
                if (StringUtil.isNotEmpty(wholeClassName)) {
                    subClassName = wholeClassName +"."+ subClassName;
                }
                addFileClass(classSet, subPath, subClassName);
            }
        }
    }

    private static void addSingleClass(Set<Class<?>> classSet, String className) {
        Class<?> clazz = loadClass(className, false);
        classSet.add(clazz);
    }

    /**
     * 加载类
     */
    public static Class<?> loadClass(String className, boolean isInitialized){
        Class<?> cls;
        try {
            cls = Class.forName(className, isInitialized, getClassLoader());
        } catch (ClassNotFoundException e) {
            LOGGER.error("load class failure", e);
            throw new RuntimeException(e);
        }
        return cls;
    }

}
