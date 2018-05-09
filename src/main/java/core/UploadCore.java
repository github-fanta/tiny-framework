package core;

import model.FileParam;
import model.FormParam;
import model.Param;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CollectionUtil;
import util.StreamUtil;
import util.StringUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文件上传核心
 * Created by liq on 2018/5/9.
 */
public final class UploadCore {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadCore.class);

    private static ServletFileUpload servletFileUpload;

    /**
     * 文件上传初始化
     */
    public static void init(ServletContext servletContext) {
        //ServletContext的临时目录，有了它，servlet重启时，容器不再需要维持临时目录的内容。属性关联的对象必须是File类型
        File repository = (File) servletContext.getAttribute(ServletContext.TEMPDIR);//设置缓存目录
        //通过DiskFileItemFactory（文件项工厂对象）上传时有一个默认缓存，大小是10K，超过10K，就用磁盘作为缓存，存放缓存的目录默认是系统临时目录。可以利用factory.setRepository()来设置缓存目录。
        //DiskFileItemFactory作用：将请求消息实体中的每一个项目封装成单独的DiskFileItem (FileItem接口的实现) 对象。当上传的文件项目比较小时，直接保存在内存中，比较大时，以临时文件的形式，保存在磁盘临时文件夹
        servletFileUpload = new ServletFileUpload(new DiskFileItemFactory(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD, repository));
        int uploadLimit = ConfigCore.getAppUploadLimit();
        servletFileUpload.setFileSizeMax(uploadLimit * 1024 * 1024);
    }


    /**
     * 判断请求是否为multipart类型
     */
    public static boolean isMultipart(HttpServletRequest request) {
        return ServletFileUpload.isMultipartContent(request);
    }

    /**
     * 创建请求对象
     */
    public static Param createParam(HttpServletRequest request) throws IOException {

        ArrayList<FormParam> formParamList = new ArrayList<FormParam>();
        ArrayList<FileParam> fileParamList = new ArrayList<FileParam>();
        Map<String, List<FileItem>> fileItemListMap = null;
        try {
            fileItemListMap = servletFileUpload.parseParameterMap(request);
            if (CollectionUtil.isNotEmpty(fileItemListMap)) {
                for (Map.Entry<String, List<FileItem>> fileItemListEntry : fileItemListMap.entrySet()) {
                    String fieldName = fileItemListEntry.getKey();
                    List<FileItem> fileItemList = fileItemListEntry.getValue();
                    if (CollectionUtil.isNotEmpty(fileItemList)) {
                        for (FileItem fileItem : fileItemList) {
                            if (fileItem.isFormField()) {
                                //fileItem封装的是普通文本表单域
                                String fieldValue = fileItem.getString("UTF-8");
                                formParamList.add(new FormParam(fieldName, fieldValue));
                            } else {
                                //fileItem封装的是文件上传表单域
                                String fileName = new String(fileItem.getName().getBytes(), "utf-8");
                                if (StringUtil.isNotEmpty(fileName)) {
                                    long fileSize = fileItem.getSize();
                                    String contentType = fileItem.getContentType();
                                    InputStream inputStream = fileItem.getInputStream();
                                    fileParamList.add(new FileParam(fieldName, fileName, fileSize, contentType, inputStream));
                                }
                            }
                        }
                    }
                }
            }
        } catch (FileUploadException e) {
            LOGGER.error("create param failure", e);
            throw new RuntimeException(e);
        }
        return new Param(formParamList, fileParamList);
    }

    /**
     * 上传文件
     */
    public static void uploadFile(String basePath, FileParam fileParam){

        try {
            String filePath = basePath + fileParam.getFileName();
            BufferedInputStream inputStream = new BufferedInputStream(fileParam.getIntputStream());
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath));
            StreamUtil.copyStream(inputStream, outputStream);
        } catch (Exception e) {
            LOGGER.error("upload file failure", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量上传文件
     */
    public static void uploadFile(String basePath, List<FileParam> fileParamList){
        try{
            if (CollectionUtil.isNotEmpty(fileParamList)){
                for(FileParam fileParam : fileParamList){
                    uploadFile(basePath, fileParam);
                }
            }
        }catch(Exception e){
            LOGGER.error("upload file failure", e);
            throw new RuntimeException(e);
        }
    }
}