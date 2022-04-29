package xyz.coolcsm.mydubbo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringEncoder;
import xyz.coolcsm.mydubbo.manager.ClientProxy;
import xyz.coolcsm.mydubbo.manager.RpcClientManager;
import xyz.coolcsm.mydubbo.message.RpcRequestMessage;
import xyz.coolcsm.mydubbo.service.HelloService;
import xyz.coolcsm.mydubbo.service.HelloServiceImpl;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo
 * @since 2022/4/25 19:23
 */

public class ClientTest {


    public static void main(String[] args) throws InterruptedException, ClassNotFoundException {
        RpcClientManager clientManager = new RpcClientManager();
        ClientProxy clientProxy = new ClientProxy(clientManager);
        //创建代理对象
        HelloService service = clientProxy.getProxyService(HelloService.class);
        System.out.println(service.sayHello("zhangsan"));
        System.out.println(service.sayHello("lisi"));
        System.out.println(service.sayHello("wangwu"));
    }
}
