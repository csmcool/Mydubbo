package xyz.coolcsm.mydubbo.annotation.client;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.annotation.client
 * @since 2022/4/27 19:15
 */


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {
    String value() default "";
}
