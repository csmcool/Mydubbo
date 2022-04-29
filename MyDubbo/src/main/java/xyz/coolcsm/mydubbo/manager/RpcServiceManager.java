package xyz.coolcsm.mydubbo.manager;

import com.alibaba.nacos.api.exception.NacosException;
import com.google.common.collect.Maps;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.lang3.StringUtils;
import xyz.coolcsm.mydubbo.annotation.server.RpcService;
import xyz.coolcsm.mydubbo.annotation.server.RpcServiceScan;
import xyz.coolcsm.mydubbo.common.PackageScanUtils;
import xyz.coolcsm.mydubbo.common.YmlUtil;
import xyz.coolcsm.mydubbo.config.Config;
import xyz.coolcsm.mydubbo.factory.ServiceFactory;
import xyz.coolcsm.mydubbo.protocol.MessageCodecSharable;
import xyz.coolcsm.mydubbo.protocol.ProcotolFrameDecoder;
import xyz.coolcsm.mydubbo.register.NacosServerRegistry;
import xyz.coolcsm.mydubbo.register.ServerRegistry;
import xyz.coolcsm.mydubbo.rpc.HeartBeatServerHandler;
import xyz.coolcsm.mydubbo.rpc.PingMessageHandler;
import xyz.coolcsm.mydubbo.rpc.RpcRequestMessageHandler;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.manager
 * @since 2022/4/24 13:54
 */

public class RpcServiceManager {

    protected String host;
    protected int port;
    protected ServerRegistry serverRegistry;
    protected ServiceFactory serviceFactory;
    NioEventLoopGroup worker = new NioEventLoopGroup();
    NioEventLoopGroup boss = new NioEventLoopGroup();
    ServerBootstrap bootstrap = new ServerBootstrap();
    Set<String> hashSet = new HashSet<>();

    public RpcServiceManager() {
        String hostIp = "127.0.0.1";
        int serverPort = 8080;
        try {
            hostIp = Config.getHostIp();
            serverPort = Config.getServerPort();
        } catch (Exception e) {
            e.printStackTrace();
        }
        init(hostIp, serverPort);
    }

    public void init(String host, int port) {
        this.host = host;
        this.port = port;
        serverRegistry = new NacosServerRegistry();
        serviceFactory = new ServiceFactory();
        autoRegistry();
    }


    /**
     * 开启服务
     */
    public void start() {
        //日志
        LoggingHandler LOGGING = new LoggingHandler(LogLevel.DEBUG);
        //消息节码器
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        //RPC请求处理器
        RpcRequestMessageHandler RPC_HANDLER = new RpcRequestMessageHandler();
//        心跳处理器
        HeartBeatServerHandler HEATBEAT_SERVER = new HeartBeatServerHandler();
        //心跳请求的处理器
        PingMessageHandler PINGMESSAGE = new PingMessageHandler();
        try {
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));                            pipeline.addLast(new ProcotolFrameDecoder());//定长解码器
                            pipeline.addLast(MESSAGE_CODEC);
                            pipeline.addLast(LOGGING);
                            pipeline.addLast(HEATBEAT_SERVER);
                            pipeline.addLast(PINGMESSAGE);
                            pipeline.addLast(RPC_HANDLER);
                        }
                    });
            //绑定端口
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("服务器已启动");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.err.println("启动服务出错");
        }finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }

    /**
     * 自动扫描@RpcServer注解  注册服务
     */
    public void autoRegistry() {
        String mainClassPath = PackageScanUtils.getStackTrace();
        Class<?> mainClass;
        try {
            mainClass = Class.forName(mainClassPath);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("启动类为找到");
        }
        if (mainClass.isAnnotationPresent(RpcService.class)) {
            throw new RuntimeException("启动类缺少@RpcService 注解");
        }
        String annotationValue = null;

        try {
            Map<String, Object> dubbo = YmlUtil.getResMap("provider");
            Object basePackage = dubbo.get("base-package");
            annotationValue = basePackage.toString();
        } catch (Exception e) {
            annotationValue = mainClass.getAnnotation(RpcServiceScan.class).value();
        }

        //如果注解路径的值是空，则等于main父路径包下
        if ("".equals(annotationValue)) {
            annotationValue = mainClassPath.substring(0, mainClassPath.lastIndexOf("."));
        }
        //获取所有类的set集合
        Set<Class<?>> set = PackageScanUtils.getClasses(annotationValue);
        for (Class<?> c : set) {
            //只有有@RpcServer注解的才注册
            if (c.isAnnotationPresent(RpcService.class)) {
                String ServerNameValue = c.getAnnotation(RpcService.class).name();
                Object object;
                try {
                    object = c.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    System.err.println("创建对象" + c + "发生错误");
                    continue;
                }
                //注解的值如果为空，使用类名
                if ("".equals(ServerNameValue) || StringUtils.isBlank(ServerNameValue)) {
                    Class<?>[] interfaces = c.getInterfaces();
                    if (interfaces.length > 0) {
                        for (Class<?> anInterface : interfaces) {
                            addServer(object,anInterface.getCanonicalName());
                            hashSet.add(anInterface.getCanonicalName());
                        }
                    }
                } else {
                    addServer(object, ServerNameValue);
                }
            }
        }
        //注册服务
        Map<String, Object> dubbo = YmlUtil.getResMap("dubbo");
        Map<String, Object> application = (Map<String, Object>) dubbo.get("application");
        String applicationName = application.get("name").toString();
        //nacos元素据添加
        Map<String, String> metadata = Maps.newHashMapWithExpectedSize(1);

        StringBuffer sb = new StringBuffer();
        //添加服务名称
        for (String s : hashSet) {
            sb.append(s).append("&");
        }

        //这里存放比较简单，就一些版本号就行了
        String myDubboInfo = "[version=1.0.0&timestamp" + System.currentTimeMillis() + "&services=" + sb.toString() +"application=" + applicationName +"]";
        //注册服务
        metadata.put("MyDubbo", myDubboInfo);
        serverRegistry.register(applicationName, new InetSocketAddress(host, port), metadata);
    }

    /**
     * 添加对象到工厂和注册到注册中心
     *
     * @param server
     * @param serverName
     * @param <T>
     * @throws NacosException
     */
    public <T> void addServer(T server, String serverName) {
        System.out.println(serverName + "注册中");
        serviceFactory.addServiceProvider(server, serverName);
    }

}
