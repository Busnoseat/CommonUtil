package com.houbank.mls.core.common.file;

import com.houbank.mls.core.common.dictionary.LoanDicConstant;
import com.houbank.mls.core.common.util.DateUtils;
import com.houbank.runtime.config.PropertiesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The type FileOperation.
 *
 * @author xubo
 * @Description: 文件操作相关的类
 * @Date 2016/5/23
 */
public class FileOperation {
    private static final Logger logger = LoggerFactory.getLogger(FileOperation.class);


    /**
     * 获取文件绝对路径(url+filename)
     *
     * @param fileUrl
     * @param fileName
     * @return url+filename
     */
    public static String getFileUrl(String fileUrl, String fileName) {
        String _temp = "";
        if (!"/".equals(fileUrl.charAt(fileUrl.length() - 1))) {
            _temp = "/";
        }
        return fileUrl + _temp + fileName;
    }

    /**
     * 创建文件
     *
     * @param url 文件存放路径
     * @return boolean true表示创建文件成功
     */
    public static boolean createFile(String url) {
        boolean flag = false;
        File fileName = new File(url);
        try {
            if (!fileName.exists()) {
                fileName.createNewFile();
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 将List<实体类> 转换为List<String> 中间以指定字符串相隔
     *@param cutApart 分隔符
     * @param sourceList 源list
     * @return List<String>
     */
    public static List<String> ConvertToListStr(List<?> sourceList, String cutApart) {
        List<String> list = new LinkedList<>();
        Iterator _i = sourceList.iterator();
        while (_i.hasNext()) {
            StringBuffer sb = new StringBuffer();
            Object o = _i.next();
            Field[] fields = o.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (i != 0)
                    sb.append(cutApart);
                Class clazz = o.getClass();
                PropertyDescriptor pd;
                try {
                    pd = new PropertyDescriptor(field.getName(), clazz);
                    Method m = pd.getReadMethod();
                    Object val = m.invoke(o);
                    if(val!=null){
                        sb.append(String.valueOf(val));
                    }else if(field.getType().getName().equals("java.lang.String")){
                        sb.append("");
                    }else if(field.getType().getName().equals("java.math.BigDecimal")){
                        sb.append(0);
                    }else if(field.getType().getName().equals("java.util.Date")){
                        sb.append("");
                    }

                } catch (Exception e) {
                    logger.info("转换为List<String>出现异常");
                }
            }
            list.add(sb.toString());
        }
        return list;
    }

    /**
     * 将List<String> 转换为List<Entity>
     *
     * @param list
     * @param cutApart String里的分隔符
     * @param tarType  目标class
     * @return List<?>
     */
    public static <T> List<T> ConvertToListEntity(List<String> list, String cutApart, Class<T> tarType) {
        if (list == null || cutApart == null || tarType == null) {
            return null;
        }
        List<T> listT = new LinkedList<>();
        T t = null;
        final Field[] fields = tarType.getDeclaredFields();
        Iterator<String> _i = list.iterator();
        while (_i.hasNext()) {
            try {
                t = tarType.newInstance();
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            }
            String str = _i.next();
            String[] strs = str.split(cutApart);
            for (int i = 0; i < strs.length; i++) {
                Field _field = fields[i];
                try {
                    PropertyDescriptor pd = new PropertyDescriptor(_field.getName(), tarType);
                    Method method = pd.getWriteMethod();
                    method.invoke(t, strs[i]);
                } catch (IntrospectionException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            listT.add(t);
        }
        return listT;
    }

    /**
     * 用流行读文件并且转换为指定的实体类
     * @param url  文件全路径
     * @param cutApart  文件内容分隔符
     * @param tarType   指定返回实体类
     * @param <T>
     * @return
     */
    public static <T> List<T> ReadTxtAndReturnListEntity(String url, String cutApart, Class<T> tarType) {
        if (url == null || cutApart == null || tarType == null) {
            return null;
        }
        String str = "";
        List<String> listStr = new LinkedList<>();
        List<T> listT = new LinkedList<>();
        FileInputStream fis = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(url);
            br = new BufferedReader(new InputStreamReader(fis, "utf-8"));
            while ((str = br.readLine()) != null) {
                listStr.add(str);
            }
            listT = ConvertToListEntity(listStr, cutApart, tarType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return listT;
    }

    /**
     * 指定文件 行写入内容
     * @param listStr
     * @return
     */
    public static boolean createLoanTxt(List<String> listStr, String url) {
        boolean flag = false;
        if (listStr == null || listStr.size() == 0 || url == null || url.length() == 0)
            return flag;
        BufferedWriter bw = null;
        try {
            FileOperation.createFile(url);
            File file = new File(url);
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"));
            for (int i = 0; i < listStr.size(); i++) {
                bw.write(listStr.get(i));
                bw.newLine();
            }
            bw.flush();
            flag = true;
        } catch (Exception e) {
            logger.info("生成待放款文件出现异常");
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    logger.info("生成待放款文件时关闭bw异常");
                }
            }
        }
        return flag;
    }

 
}