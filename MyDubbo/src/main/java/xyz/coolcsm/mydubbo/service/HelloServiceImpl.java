package xyz.coolcsm.mydubbo.service;

import xyz.coolcsm.mydubbo.annotation.server.RpcService;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.service
 * @since 2022/4/24 14:59
 */
@RpcService
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }
}
