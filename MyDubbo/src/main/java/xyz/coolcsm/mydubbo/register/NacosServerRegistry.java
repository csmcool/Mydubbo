package xyz.coolcsm.mydubbo.register;

import com.alibaba.nacos.api.exception.NacosException;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.register
 * @since 2022/4/22 16:47
 */

public class NacosServerRegistry implements ServerRegistry{

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtils.registerServer(serviceName,inetSocketAddress);
        } catch (NacosException e) {
            throw new RuntimeException("注册Nacos出现异常");
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress, Map<String, String> metadata) {
        try {
            NacosUtils.registerServer(serviceName,inetSocketAddress,metadata);
        } catch (NacosException e) {
            throw new RuntimeException("注册Nacos出现异常");
        }
    }

    @Override
    public InetSocketAddress getService(String serviceName) {
        return null;
    }
}
