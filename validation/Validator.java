package com.houbank.mls.test.validation;

import java.lang.annotation.*;

/**
 * 自定义注解
 * @Retention:定义注解的保留策略
 *             SOURCE注解仅存在于源码中，在class字节码文件中不包含
 *             CLASS默认的保留策略，注解会在class字节码文件中存在，但运行时无法获得
 *             RUNTIME注解会在class字节码文件中存在，在运行时可以通过反射获取到
 *@Target:定义注解使用目标
 *             ElementType.FIELD代表字段  ElementType.METHOD代表方法
 *@Documented:说明该注解将被包含在javadoc中
 *@Inherited:允许子类继承父类中的注解
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Validator {
    /**字段名称*/
    public String name();

    /**不可空 默认不限制*/
    public boolean isNotNull() default false;

    /**最大值 默认不限制*/
    public int maxLength() default -1;

    /**最小值 默认不限制*/
    public int minLength() default -1 ;

    /**取值范围 多个以,相隔*/
    public String range() default "";

    /**正则验证*/
    public String pattern() default  "";

}
