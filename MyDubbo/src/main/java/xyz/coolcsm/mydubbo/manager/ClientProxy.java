package xyz.coolcsm.mydubbo.manager;

import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;
import xyz.coolcsm.mydubbo.message.RpcRequestMessage;
import xyz.coolcsm.mydubbo.protocol.SequenceIdGenerator;

import java.lang.reflect.Proxy;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.manager
 * @since 2022/4/24 17:21
 */

@Slf4j
public class ClientProxy {

    private final RpcClientManager RPC_CLIENT;

    public ClientProxy(RpcClientManager client){
        this.RPC_CLIENT=client;
    }

    //JDK动态代理创建代理类
    @SuppressWarnings("unchecked")
    public  <T> T getProxyService(Class<T> serviceClass) {
        ClassLoader loader = serviceClass.getClassLoader();
        Class<?>[] interfaces = new Class[]{serviceClass};
        //创建代理对象
        Object o = Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) -> {
            // 1. 将方法调用转换为 消息对象
            int sequenceId = SequenceIdGenerator.nextId();
            RpcRequestMessage msg = new RpcRequestMessage(
                    sequenceId,
                    serviceClass.getCanonicalName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );
            // 2. 准备一个空 Promise 对象，来接收结果 存入集合            指定 promise 对象异步接收结果线程
            DefaultPromise<Object> promise = new DefaultPromise<Object>(RpcClientManager.group.next());
            RpcClientManager.PROMISES.put(sequenceId, promise);
            // 3. 将消息对象发送出去
            RPC_CLIENT.sendRpcRequest(msg);
            // 4. 等待 promise 结果
            promise.await();
            if(promise.isSuccess()) {
                // 调用正常
                System.out.println("调用成功");
                return promise.getNow();
            } else {
                // 调用失败
                System.out.println("调用失败");
                throw new RuntimeException(promise.cause());
            }
        });
        return (T) o;
    }
}
