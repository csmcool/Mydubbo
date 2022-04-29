package xyz.coolcsm.mydubbo.message;

import lombok.Data;
import lombok.ToString;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.message
 * @since 2022/4/23 22:47
 */

@Data
@ToString(callSuper = true)
public abstract class AbstractResponseMessage extends Message {
    private boolean success;
    private String reason;

    public AbstractResponseMessage() {
    }

    public AbstractResponseMessage(boolean success, String reason) {
        this.success = success;
        this.reason = reason;
    }
}
