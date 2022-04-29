package xyz.coolcsm.mydubbo.rpc;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import xyz.coolcsm.mydubbo.message.PingMessage;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.rpc
 * @since 2022/4/24 16:51
 */

@Slf4j
@ChannelHandler.Sharable
public class PingMessageHandler extends SimpleChannelInboundHandler<PingMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PingMessage msg) throws Exception {
        log.debug("接收到心跳信号{}",msg.getMessageType());

    }

}
