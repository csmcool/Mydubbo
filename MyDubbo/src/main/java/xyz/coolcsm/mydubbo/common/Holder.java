package xyz.coolcsm.mydubbo.common;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.common
 * @since 2022/4/28 17:39
 */

public class Holder<T> {

    private volatile T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
