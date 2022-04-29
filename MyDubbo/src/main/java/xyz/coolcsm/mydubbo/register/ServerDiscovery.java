package xyz.coolcsm.mydubbo.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.register
 * @since 2022/4/23 21:39
 */

public interface ServerDiscovery {

    /**
     * 根据服务得到服务的地址
     * @param serviceName 服务名称
     * @return 返回服务得地址
     * @throws NacosException nacos异常
     */
    InetSocketAddress getService(String serviceName) throws NacosException;

    /**
     * 根据服务得到服务的地址
     * @param serviceName 服务名称
     * @return 返回服务的实例
     * @throws NacosException  nacos异常
     */
    Instance getServiceList(String serviceName) throws NacosException;
}
