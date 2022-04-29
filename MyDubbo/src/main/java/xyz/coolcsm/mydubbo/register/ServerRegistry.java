package xyz.coolcsm.mydubbo.register;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.register
 * @since 2022/4/22 16:27
 */

public interface ServerRegistry {

    /**
     * 将服务的名称和地址注册进服务注册中心
     * @param serviceName
     * @param inetSocketAddress
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);

    /**
     * 将服务的名称和地址注册已经元数据进服务注册中心
     * @param serviceName  服务名称
     * @param inetSocketAddress 地址
     * @param metadata 元数据
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress, Map<String,String> metadata);

    /**
     * 根据服务名称从注册中心获取到服务提供者的地址
     * @param serviceName
     * @return
     */
    InetSocketAddress getService(String serviceName);

}
