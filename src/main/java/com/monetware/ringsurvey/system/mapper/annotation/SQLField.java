package com.monetware.ringsurvey.system.mapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Simo
 * @date 2019-06-10
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLField {

    /**
     * 数据表中对应的列名
     * @return
     */
    String column() default "";

    /**
     * 数据表对应的列的类型
     * int varchar text number datetime etc.
     * @return
     */
    String type() default "";

    /**
     * 数据表对应的列的长度 默认为0
     * @return
     */
    int len() default 0;

    /**
     * 数据表对应字段的默认值
     * @return
     */
    String defaultValue() default "";

    /**
     * 数据数字字段是否自增 默认false
     * @return
     */
    boolean auto() default false;

    /**
     * 数据字段是否添加索引 默认false
     * @return
     */
    boolean index() default false;

    /**
     * 数据字段约束
     * @return
     */
    Constraints constraint() default @Constraints;
}
