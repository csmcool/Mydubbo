package xyz.coolcsm.mydubbo.annotation.spring;

import java.lang.annotation.*;

/**
 * @author 什锦
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyComponentScan {

    String value() default "";
}