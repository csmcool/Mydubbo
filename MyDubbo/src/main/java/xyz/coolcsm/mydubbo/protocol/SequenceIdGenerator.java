package xyz.coolcsm.mydubbo.protocol;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.protocol
 * @since 2022/4/24 14:28
 */

public abstract class SequenceIdGenerator {

    private static final AtomicInteger id = new AtomicInteger();


    /**
     * 获取下一个id unsafe method
     * @return int
     */
    public static int nextId() {
        return id.incrementAndGet();
    }
}
