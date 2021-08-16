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
public @interface Constraints {

    /**
     * 是否主键约束 默认false
     * @return
     */
    boolean primaryKey() default false;

    /**
     * 是否允许空约束 默认true
     * @return
     */
    boolean allowNull() default true;

}
