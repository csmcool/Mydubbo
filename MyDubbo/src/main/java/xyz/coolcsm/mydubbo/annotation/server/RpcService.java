package xyz.coolcsm.mydubbo.annotation.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.annotation.server
 * @since 2022/4/23 23:48
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {

    public String name() default "";

}

