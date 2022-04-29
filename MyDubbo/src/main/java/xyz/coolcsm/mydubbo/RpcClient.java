package xyz.coolcsm.mydubbo;

import xyz.coolcsm.mydubbo.annotation.spring.MyComponentScan;
import xyz.coolcsm.mydubbo.controller.HelloController;
import xyz.coolcsm.mydubbo.spring.ApplicationContext;
import xyz.coolcsm.mydubbo.spring.MyAnnotationConfigApplicationContext;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo
 * @since 2022/4/24 17:22
 */

@MyComponentScan("xyz.coolcsm.mydubbo.controller")
public class RpcClient {

    public static void main(String[] args) throws ClassNotFoundException {
        ApplicationContext applicationContext = new MyAnnotationConfigApplicationContext(RpcClient.class);
        HelloController helloController = (HelloController)applicationContext.getBean("helloController");
        System.out.println(helloController.sayHello("什锦"));
    }
}
