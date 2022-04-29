package xyz.coolcsm.mydubbo.spring;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.spring
 * @since 2022/4/29 16:16
 */

public interface ApplicationContext {

    /**
     * 获取一个bean
     * @param name name
     * @return 类
     */
    Object getBean(String name);
}
