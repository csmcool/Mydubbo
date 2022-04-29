package xyz.coolcsm.mydubbo.annotation.spring;

import java.lang.annotation.*;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.annotation.spring
 * @since 2022/4/29 16:12
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface MyAutowired {
}
