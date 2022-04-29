package xyz.coolcsm.mydubbo;

import xyz.coolcsm.mydubbo.common.ExtensionLoader;
import xyz.coolcsm.mydubbo.service.HelloService;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo
 * @since 2022/4/28 18:25
 */

public class SPITest {
    public static void main(String[] args) {
        ExtensionLoader<HelloService> extensionLoader = ExtensionLoader.getExtensionLoader(HelloService.class);
        HelloService helloService = extensionLoader.getExtension("HelloServiceImpl");
        String sayHello = helloService.sayHello("什锦");
        System.out.println(sayHello);

//        // 获取实现类
//        HelloService defaultExtension = ExtensionLoader.getExtensionLoader(HelloService.class).getDefaultExtension();
//        System.out.println(defaultExtension.sayHello("什锦"));
//
//        // 获取实现类
//        HelloService hello2ServiceImpl = ExtensionLoader.getExtensionLoader(HelloService.class).getExtension("hello2ServiceImpl");
//        System.out.println(hello2ServiceImpl.sayHello("什锦"));
    }
}
