package xyz.coolcsm.mydubbo.controller;

import xyz.coolcsm.mydubbo.annotation.client.RpcReference;
import xyz.coolcsm.mydubbo.annotation.spring.MyComponent;
import xyz.coolcsm.mydubbo.service.HelloService;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.controller
 * @since 2022/4/29 16:07
 */

@MyComponent
public class HelloController {

    @RpcReference
    private HelloService helloService;

    public String sayHello(String name) {
        return helloService.sayHello(name);
    }
}
