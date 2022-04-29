package xyz.coolcsm.mydubbo.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import xyz.coolcsm.mydubbo.loadBalancer.LoadBalancer;
import xyz.coolcsm.mydubbo.loadBalancer.RoundRobinRule;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.register
 * @since 2022/4/23 21:41
 */

public class NacosServerDiscovery implements ServerDiscovery {

    private final LoadBalancer loadBalancer;

    /**
     * 默认是轮询
     * @param loadBalancer 负载均衡算法
     */
    public NacosServerDiscovery(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer == null ? new RoundRobinRule() : loadBalancer;
    }

    @Override
    public InetSocketAddress getService(String serviceName) throws NacosException {
        List<Instance> instanceList = NacosUtils.getAllInstance(serviceName);
        if (instanceList.size() == 0) {
            throw new RuntimeException("找不到对应服务");
        }
        Instance instance = loadBalancer.getInstance(instanceList);
        return new InetSocketAddress(instance.getIp(), instance.getPort());
    }

    @Override
    public Instance getServiceList(String serviceName) throws NacosException {
        List<Instance> instanceList = NacosUtils.getAllInstance(serviceName);
        if (instanceList.size() == 0) {
            throw new RuntimeException("找不到对应服务");
        }
        Instance instance = loadBalancer.getInstance(instanceList);
        return instance;
    }
}
