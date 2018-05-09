package core;

import model.FormParam;
import model.Param;
import util.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

/**
 * 封装请求参数
 * Created by liq on 2018/5/9.
 */
public final class ParamCore {

    /**
     * 创建请求对象
     */
    public static Param createParam(HttpServletRequest request) throws IOException {
        ArrayList<FormParam> formParamList = new ArrayList<FormParam>();
        formParamList.addAll(parseParameterNames(request));
        formParamList.addAll(parseInputStream(request));//非multipart类型也不是application/x- www-form-urlencoded这种表单类型时候，如 application/json，此时request.getParameter(Str)是无法拿到数据的。
        return new Param(formParamList);
    }

    /**
     * 解析request域中参数
     * @param request
     * @return
     */
    private static Collection<? extends FormParam> parseParameterNames(HttpServletRequest request) {
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ArrayList<FormParam> formParamList = new ArrayList<FormParam>();
        Enumeration<String> paramNames = request.getParameterNames();
        while(paramNames.hasMoreElements()){
            String fieldName = paramNames.nextElement();
            String[] fieldValues = request.getParameterValues(fieldName);//获得如checkbox类（名字相同，但值有多个）的数据
            if (ArrayUtil.isNotEmpty(fieldValues)){
                Object fieldValue;
                if (fieldValues.length == 1){
                    fieldValue = fieldValues[0];
                }else{
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<fieldValues.length; i++){
                        sb.append(fieldValues[i]);
                        if (i != fieldValues.length - 1){
                            sb.append(StringUtil.SEPARATOR);
                        }
                    }
                    fieldValue = sb.toString();
                }
                formParamList.add(new FormParam(fieldName, fieldValue));
            }
        }
        return formParamList;
    }

    /**
     * 解析流中的参数
     * @param request
     * @return
     * @throws IOException
     */
    private static Collection<? extends FormParam> parseInputStream(HttpServletRequest request) throws IOException {
        ArrayList<FormParam> formParamList = new ArrayList<FormParam>();
        String body = CodecUtil.decodeURL(StreamUtil.getString(request.getInputStream()));
        if (StringUtil.isNotEmpty(body)){
            String[] kvs = StringUtil.splitString("body","&");
            if (ArrayUtil.isNotEmpty(kvs)){
                for (String kv : kvs){
                    String[] s = StringUtil.splitString(kv,"=");
                    if (ArrayUtil.isNotEmpty(s) && s.length == 2){
                        String fieldName = s[0];
                        String fieldValue = s[1];
                        formParamList.add(new FormParam(fieldName, fieldValue));
                    }
                }
            }
        }
        return formParamList;
    }



}
