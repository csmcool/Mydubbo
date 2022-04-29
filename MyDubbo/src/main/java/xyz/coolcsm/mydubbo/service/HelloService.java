package xyz.coolcsm.mydubbo.service;

import xyz.coolcsm.mydubbo.annotation.extension.SPI;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.service
 * @since 2022/4/24 14:59
 */

@SPI
public interface HelloService {
    String sayHello(String name);
}
