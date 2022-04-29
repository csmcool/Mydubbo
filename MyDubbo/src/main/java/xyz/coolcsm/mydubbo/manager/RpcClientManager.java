package xyz.coolcsm.mydubbo.manager;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import xyz.coolcsm.mydubbo.common.YmlUtil;
import xyz.coolcsm.mydubbo.loadBalancer.RoundRobinRule;
import xyz.coolcsm.mydubbo.message.RpcRequestMessage;
import xyz.coolcsm.mydubbo.protocol.MessageCodecSharable;
import xyz.coolcsm.mydubbo.protocol.ProcotolFrameDecoder;
import xyz.coolcsm.mydubbo.register.NacosServerDiscovery;
import xyz.coolcsm.mydubbo.register.ServerDiscovery;
import xyz.coolcsm.mydubbo.rpc.HeartBeatClientHandler;
import xyz.coolcsm.mydubbo.rpc.RpcResponseMessageHandler;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.manager
 * @since 2022/4/24 16:57
 */

@Slf4j
public class RpcClientManager {

    /**
     * 单例channel
     */
    private static final Bootstrap bootstrap;
    static NioEventLoopGroup group;
    private final ServerDiscovery serviceDiscovery;


    /**
     * 根据序号key来判断是哪个请求的消息      value是用来接收结果的 promise 对象
     */
    public static final Map<Integer, Promise<Object>> PROMISES;

    public static long currentTime;

    public static List<Instance> instances;

    /**
     * channel集合  可能请求多个服务  每个服务都有一个channel
     */
    public static Map<String, Channel> channels;
    private static final Object LOCK = new Object();

    static {
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        currentTime = 0;
        instances = new Vector<>();
        initChannel();
        channels = new ConcurrentHashMap<>();
        PROMISES = new ConcurrentHashMap<Integer, Promise<Object>>();
    }


    public RpcClientManager() {
        group = new NioEventLoopGroup();
        this.serviceDiscovery = new NacosServerDiscovery(new RoundRobinRule());
    }

    private static Bootstrap initChannel() {
        //日志handler
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        //消息处理handler
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        //处理相应handler
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
        //心跳处理器
        HeartBeatClientHandler HEATBEAT_CLIENT = new HeartBeatClientHandler();

        bootstrap.channel(NioSocketChannel.class)
                .group(group)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, 15, 0, TimeUnit.SECONDS));
                        //定长解码器
                        pipeline.addLast(new ProcotolFrameDecoder());
                        pipeline.addLast(MESSAGE_CODEC);
                        pipeline.addLast(LOGGING_HANDLER);
                        pipeline.addLast(HEATBEAT_CLIENT);
                        pipeline.addLast(RPC_HANDLER);
                    }
                });
        return bootstrap;
    }

    /**
     * 发送消息根据用户名 服务发现 找到地址
     * @param msg
     */
    public void sendRpcRequest(RpcRequestMessage msg) throws NacosException, InterruptedException {
        Map<String, Object> dubbo = YmlUtil.getResMap("dubbo");
        Map<String, Object> cloud = (Map<String, Object>) dubbo.get("cloud");
        String s = cloud.get("subscribed-services").toString();

        String[] split = s.split(",");

        long currentTimeMillis = System.currentTimeMillis();
        if ((currentTimeMillis - currentTime)/1000/60 > 5) {
            instances = new Vector<>();
            for (String s1 : split) {
                Instance instance = serviceDiscovery.getServiceList(s1);
                instances.add(instance);
            }
            currentTime = currentTimeMillis;
        }

        InetSocketAddress service = null;

        for (Instance instance : instances) {
            Map<String, String> metadata = instance.getMetadata();
            String host = metadata.get("MyDubbo");
            boolean contains = host.contains(msg.getInterfaceName());
            if (contains) {
                service = new InetSocketAddress(instance.getIp(), instance.getPort());
                break;
            }
        }

        Channel channel = get(service);
        if (!channel.isActive() || !channel.isRegistered()) {
            group.shutdownGracefully();
            log.info("channel is not active or registered");
            return;
        }
        channel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                System.out.println("发送消息----------------------");
                log.debug("客户端发送消息成功");
            } else {
                System.out.println("发送消息失败----------------------");
            }
        });

    }

    /**
     * 获取channel  没有就建立链接
     * @param inetSocketAddress
     * @return
     */
    public static Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        //判断是否存在
        if (channels.containsKey(key)) {
            Channel channel = channels.get(key);
            if (channels != null && channel.isActive()) {
                return channel;
            }
            channels.remove(key);
        }
        //建立连接
        Channel channel = null;
        try {
            channel = bootstrap.connect(inetSocketAddress).sync().channel();
            channel.closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    System.out.println("channel关闭");
                    log.debug("断开连接");
                }
            });
        } catch (InterruptedException e) {
            channel.close();
            log.debug("连接客户端出错" + e);
            return null;
        }
        channels.put(key, channel);
        return channel;
    }
}
