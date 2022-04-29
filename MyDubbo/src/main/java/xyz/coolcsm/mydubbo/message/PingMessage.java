package xyz.coolcsm.mydubbo.message;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.message
 * @since 2022/4/23 22:39
 */

public class PingMessage extends Message{
    @Override
    public int getMessageType() {
        return PingMessage;
    }
}
