package xyz.coolcsm.mydubbo.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.factory
 * @since 2022/4/24 12:10
 */

public class ServiceFactory {


    /**
     * 存所有有注解@RpcService的集合
     */
    public static final Map<String, Object> SERVICE_FACTORY = new ConcurrentHashMap<>();

    /**
     * 注册服务
     */
    public <T> void addServiceProvider(T service, String serviceName) {
        if (SERVICE_FACTORY.containsKey(serviceName)) {
            return;
        }
        SERVICE_FACTORY.put(serviceName, service);
    }

    /**
     * 获取服务
     * @return
     */
    public Object getServiceProvider(String serviceName) {
        Object service = SERVICE_FACTORY.get(serviceName);
        if (service == null) {
            throw new RuntimeException("未发现该服务");
        }
        return service;
    }


}
