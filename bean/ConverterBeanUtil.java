package com.houbank.mls.test.util;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;
import org.springframework.util.DigestUtils;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type ConverterBeanUtil.
 *
 * @author xubo
 * @Description:
 * @Date 2017/1/19
 */
public class ConverterBeanUtil {
    private static ConcurrentHashMap<String, BeanCopier> cache = new ConcurrentHashMap();

    public ConverterBeanUtil() {
    }

    public static <T> T copyBeanProperties(Class source, Class<T> target, Object sourceObj, boolean useConverter) {
        if (sourceObj == null) {
            return null;
        } else {
            Object t;
            try {
                t = target.newInstance();
            } catch (Exception var7) {
                return null;
            }

            String key = source.getSimpleName() + target.getSimpleName();
            BeanCopier copier = (BeanCopier) cache.get(key);
            if (copier == null) {
                copier = createBeanCopier(source, target, useConverter, key);
            }

            copier.copy(sourceObj, t, (Converter) null);
            return (T) t;
        }
    }


    /**
     * copy  bean value from sourceObj to target
     *
     * @param sourceObj
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T copyBeanProperties(Object sourceObj, T target) {
        return copyBeanProperties(sourceObj, target, false);
    }

    public static <T> T copyBeanProperties(Object sourceObj, T target, boolean useConverter) {
        if (sourceObj != null && target != null) {
            String key = sourceObj.getClass().getSimpleName() + target.getClass().getSimpleName();
            BeanCopier copier = (BeanCopier) cache.get(key);
            if (copier == null) {
                copier = createBeanCopier(sourceObj.getClass(), target.getClass(), useConverter, key);
            }

            copier.copy(sourceObj, target, (Converter) null);
            return target;
        } else {
            return null;
        }
    }

    public static <T> List<T> copyListBeanPropertiesToList(List<?> sourceObjs, List<T> targets, Class<T> targetType) {
        if (sourceObjs != null && targets != null && targetType != null) {
            Iterator var4 = sourceObjs.iterator();

            while (var4.hasNext()) {
                Object o = var4.next();

                try {
                    Object t = targetType.newInstance();
                    targets.add((T) copyBeanProperties(o, t, false));
                } catch (InstantiationException var7) {
                    ;
                } catch (IllegalAccessException var8) {
                    ;
                }
            }

            return targets;
        } else {
            return null;
        }
    }

    private static String getHashKey(String str) {
        return str == null ? null : DigestUtils.md5DigestAsHex(str.getBytes());
    }

    private static BeanCopier createBeanCopier(Class sourceClass, Class targetClass, boolean useConverter, String cacheKey) {
        BeanCopier copier = BeanCopier.create(sourceClass, targetClass, useConverter);
        cache.putIfAbsent(cacheKey, copier);
        return copier;
    }


    /***
     * combine bean value between sourceBean and targetBean
     * based on sourceBean and if the value in sourceBean is null then get from targetBean
     *
     * @param sourceBean
     * @param targetBean
     * @return
     * @throws IllegalArgumentException
     */
    public static Object combineSydwCore(Object sourceBean, Object targetBean) {
        try {
            Class sourceBeanClass = sourceBean.getClass();
            if (sourceBeanClass.equals(targetBean.getClass()))
                throw new IllegalArgumentException("源对象和目标对象类型不一致");

            Field[] sourceFields = sourceBeanClass.getDeclaredFields();
            Field[] targetFields = sourceBeanClass.getDeclaredFields();
            for (int i = 0; i < sourceFields.length; i++) {
                Field sourceField = sourceFields[i];
                Field targetField = targetFields[i];
                sourceField.setAccessible(true);
                targetField.setAccessible(true);
                if (!(sourceField.get(sourceBean) == null) && !"serialVersionUID".equals(sourceField.getName().toString())) {
                    targetField.set(targetBean, sourceField.get(sourceBean));
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("");
        }
        return targetBean;
    }


}
