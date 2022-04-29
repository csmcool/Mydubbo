package xyz.coolcsm.mydubbo.config;

import lombok.Data;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.config
 * @since 2022/4/22 15:51
 */

@Data
public class ServiceConfig<T> {

    /**
     * 服务接口类型
     */
    private Class type;

    /**
     * 服务实现类
     */
    private T instance;

    public ServiceConfig(Class type, T instance) {
        this.type = type;
        this.instance = instance;
    }
}
