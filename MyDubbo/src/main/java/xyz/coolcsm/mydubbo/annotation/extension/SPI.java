package xyz.coolcsm.mydubbo.annotation.extension;

import java.lang.annotation.*;


/**
 * @author 什锦
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SPI {
    String value() default "";
}
