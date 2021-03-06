package xyz.coolcsm.mydubbo.rpc;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import xyz.coolcsm.mydubbo.manager.RpcClientManager;
import xyz.coolcsm.mydubbo.message.RpcResponseMessage;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.rpc
 * @since 2022/4/24 13:22
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        try {
            log.debug("{}", msg);
            // 每次使用完都要移除
            Promise<Object> promise = RpcClientManager.PROMISES.remove(msg.getSequenceId());
            if (promise != null) {
                Object returnValue = msg.getReturnValue();
                Exception exceptionValue = msg.getExceptionValue();
                if (exceptionValue != null) {
                    promise.setFailure(exceptionValue);
                } else {
                    promise.setSuccess(returnValue);
                }
            } else {
                promise.setFailure(new Exception("promise不存在"));
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("出现异常"+cause);
        ctx.close();
    }
}
