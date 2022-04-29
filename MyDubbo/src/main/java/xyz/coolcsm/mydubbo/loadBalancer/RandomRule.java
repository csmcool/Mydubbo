package xyz.coolcsm.mydubbo.loadBalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.Random;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.loadBalancer
 * @since 2022/4/23 21:43
 */

public class RandomRule implements LoadBalancer {
    private final Random random=new Random();


    /**
     * 负载均衡随机算法
     * @param instances 一个服务的所有实例
     * @return 返回一个实例
     */
    @Override
    public Instance getInstance(List<Instance> instances) {
        return instances.get(random.nextInt(instances.size()));
    }

}
