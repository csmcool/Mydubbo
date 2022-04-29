package xyz.coolcsm.mydubbo.rpc;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import xyz.coolcsm.mydubbo.message.Message;
import xyz.coolcsm.mydubbo.message.PingMessage;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.rpc
 * @since 2022/4/24 16:46
 */
@Slf4j
@ChannelHandler.Sharable
public class HeartBeatClientHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            IdleState state = event.state();
            //长时间没有写入数据  发送心跳包
            if (state == IdleState.WRITER_IDLE) {
                //获取ip
                log.debug("发送心跳包 {}", ctx.channel().remoteAddress());
                PingMessage message = new PingMessage();
                message.setSequenceId(0);
                message.setMessageType(Message.PingMessage);
                ctx.writeAndFlush(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }

        }
        return;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端异常", cause);
        cause.printStackTrace();
        ctx.close();
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.debug("客户端断开连接 {}", ctx.channel().remoteAddress());
        ctx.close();
        super.channelUnregistered(ctx);
    }
}
