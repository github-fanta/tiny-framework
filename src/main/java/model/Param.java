package model;

import util.CastUtil;
import util.CollectionUtil;
import util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求参数对象 获取来自HttpServletRequest的所有参数
 * Created by liq on 2018/5/9.
 */
public class Param {

    //表单参数列表
    private List<FormParam> formParamList;
    //文件参数列表
    private List<FileParam> fileParamList;


    public Param(List<FormParam> formParamList) {
        this.formParamList = formParamList;
    }

    public Param(List<FormParam> formParamList, List<FileParam> fileParamList) {
        this.formParamList = formParamList;
        this.fileParamList = fileParamList;
    }

    /**
     * 获取表单参数映射
     */
    public Map<String, Object> getFieldMap(){
        HashMap<String, Object> fieldMap = new HashMap<String, Object>();
        if (CollectionUtil.isNotEmpty(formParamList)){
            for(FormParam formParam : formParamList){
                String fieldName = formParam.getFieldName();
                Object fieldValue = formParam.getFieldValue();
                if (fieldMap.containsKey(fieldName)){
                    fieldValue = fieldMap.get(fieldName) + StringUtil.SEPARATOR + fieldValue;
                }
                fieldMap.put(fieldName, fieldValue);
            }
        }
        return fieldMap;
    }


    /**
     * 获取上传文件映射
     */
    public Map<String, List<FileParam>> getFileMap(){
        HashMap<String, List<FileParam>> resultMap = new HashMap<String, List<FileParam>>();
        if (CollectionUtil.isNotEmpty(fileParamList)){
            for (FileParam fileParam : fileParamList){
                String fieldName = fileParam.getFieldName();
                List<FileParam> list;
                if (resultMap.containsKey(fieldName)){
                    list = resultMap.get(fieldName);
                }else{
                    list = new ArrayList<FileParam>();
                }
                list.add(fileParam);
                resultMap.put(fieldName, list);
            }
        }
        return resultMap;
    }

    /**
     * 获取所有上传文件
     */
    public List<FileParam> getFileList(String fieldName){
        return getFileMap().get(fieldName);
    }

    /**
     * 获取一上传文件
     */
    public FileParam getFile(String fieldName){
        List<FileParam> fileParamList = getFileList(fieldName);
        if (CollectionUtil.isNotEmpty(fileParamList) && fileParamList.size() == 1){
            return fileParamList.get(0);
        }
        return null;
    }

    /**
     *根据参数名获取long型参数值
     */
    public long getLong(String name){
        return CastUtil.castLong(getFieldMap().get(name));
    }
    /**
     *根据参数名获取double型参数值
     */
    public double getDouble(String name){
        return CastUtil.castDouble(getFieldMap().get(name));
    }

    /**
     *根据参数名称获取int型参数值
     */
    public int getInt(String name){
        return CastUtil.castInt(getFieldMap().get(name));
    }

    /**
     *根据参数名获取boolean型参数值
     */
    public boolean getBoolean(String name){
        return CastUtil.castBoolean(getFieldMap().get(name));
    }

    /**
     * 根据参数名获取String型的参数值
     */
    public String getString(String name){
        return CastUtil.castString(getFieldMap().get(name));
    }
    /**
     * 验证参数是否为空
     */
    public boolean isEmpty(){
        return CollectionUtil.isEmpty(formParamList) && CollectionUtil.isEmpty(fileParamList);
    }
}
