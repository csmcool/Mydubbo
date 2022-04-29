package xyz.coolcsm.mydubbo.loadBalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.loadBalancer
 * @since 2022/4/23 21:45
 */

public class RoundRobinRule implements LoadBalancer {

    /**
     * 原子计数器，线程安全的，在高并发下实现计数是相当有用的。 可以用来记录当前调用的位置，
     */
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    /**
     * 防止Integer越界 超过Integer最大值
     *
     * @return
     */
    private final int getAndIncrement() {
        int current;
        int next;
        do {
            current = this.atomicInteger.get();
            next = current >= 2147483647 ? 0 : current + 1;
        } while (!this.atomicInteger.compareAndSet(current, next));
        return next;
    }


    @Override
    public Instance getInstance(List<Instance> list) {
        int index = getAndIncrement() % list.size();
        return list.get(index);
    }

}
