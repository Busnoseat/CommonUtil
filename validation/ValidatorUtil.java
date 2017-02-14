package com.houbank.mls.test.validation;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xubo
 * @date 2017/02/10
 */
public class ValidatorUtil {

    private static Class validatorAnno = Validator.class;

    /**
     * tell parameter is allowed or not by annotation.
     * if allowed then do nothing otherwise thorw exception and caller must try-catch to catch the errMsg
     *
     * @param o
     * @return
     */
    public static void validate(Object o) throws Exception {

        Class target = o.getClass();
        Field[] fields = target.getDeclaredFields();
        for (Field field : fields) {
            Validator a = (Validator) field.getAnnotation(validatorAnno);
            if (a == null)
                continue;

            //get field value
            PropertyDescriptor pd = new PropertyDescriptor(field.getName(), target);
            Method m = pd.getReadMethod();
            Object val = m.invoke(o);

            String name = a.name();
            boolean isNotNull = a.isNotNull();
            int maxLength = a.maxLength();
            int minLength = a.minLength();
            String range = a.range();
            String pattern = a.pattern();

            //tell field can be null
            if (isNotNull && (val == null || val.toString().length() == 0))
                throw new IllegalArgumentException(name + ":can not be null");

            //tell MaxLength and MinLength
            if (maxLength < minLength)
                throw new IllegalArgumentException(name + "：maxLength must larger than minLength");
            if (minLength >= 0) {
                if(val==null)
                    throw new IllegalArgumentException(name + ":can not be null then tell minLength");
                if (!(val instanceof String))
                    throw new IllegalArgumentException(name + ":must be String then tell minLength");
                if (val.toString().length() < minLength)
                    throw new IllegalArgumentException(name + ":minLength is" + minLength);
            }
            if (maxLength >= 0) {
                if(val==null)
                    break;
                if (!(val instanceof String))
                    throw new IllegalArgumentException(name + ":must be String then tell maxLength");
                if (val.toString().length() > maxLength)
                    throw new IllegalArgumentException(name + ":maxLength is" + maxLength);
            }

            //tell range
            if (range != null && range.length() > 0) {
                String[] ranges = range.split(",");
                List list = Arrays.asList(ranges);
                if (!list.contains(String.valueOf(val)))
                    throw new IllegalArgumentException(name + "： must range in " + range);
            }

            //tell pattern
            if (pattern.length() > 0 && !(val instanceof String))
                throw new IllegalArgumentException(name + "： must be String and can be patterned ");
            Pattern p=Pattern.compile(pattern);
            Matcher matcher=p.matcher(String.valueOf(val));
            if (pattern.length() > 0 && !matcher.matches())
                throw new IllegalArgumentException(name + "： is not accepted by pattern " + pattern);
        }
    }


    public static void main(String[] args) {
        Entity entity = new Entity();
        entity.setName("xubo");
        entity.setDesc("woshi");
        entity.setLevel("A");
        entity.setSalary(99.001);
        entity.setPhone("18252591025");
        try {
            ValidatorUtil.validate(entity);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
