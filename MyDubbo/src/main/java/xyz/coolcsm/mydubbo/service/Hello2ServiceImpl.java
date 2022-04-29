package xyz.coolcsm.mydubbo.service;

import xyz.coolcsm.mydubbo.annotation.server.RpcService;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.service
 * @since 2022/4/28 18:24
 */

@RpcService
public class Hello2ServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "Hello2 " + name;
    }
}
