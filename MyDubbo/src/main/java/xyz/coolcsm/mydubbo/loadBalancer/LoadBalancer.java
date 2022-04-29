package xyz.coolcsm.mydubbo.loadBalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.loadBalancer
 * @since 2022/4/23 21:42
 */

public interface LoadBalancer {

    /**
     * 负载均衡算法
     * @param list 传入的服务实例列表
     * @return 返回负载均衡后的服务实例
     */
    Instance getInstance(List<Instance> list);

}
