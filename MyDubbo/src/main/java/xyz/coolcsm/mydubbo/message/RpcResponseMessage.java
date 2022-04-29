package xyz.coolcsm.mydubbo.message;

import lombok.Data;
import lombok.ToString;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.message
 * @since 2022/4/23 22:45
 */
@Data
@ToString(callSuper = true)
public class RpcResponseMessage extends Message{

    /**
     * 返回值
     */
    private Object returnValue;
    /**
     * 异常值
     */
    private Exception exceptionValue;

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }
}
