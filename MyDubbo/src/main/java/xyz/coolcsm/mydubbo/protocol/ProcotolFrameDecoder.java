package xyz.coolcsm.mydubbo.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.protocol
 * @since 2022/4/24 14:26
 */

public class ProcotolFrameDecoder  extends LengthFieldBasedFrameDecoder {

    public ProcotolFrameDecoder() {
        this(1024, 12, 4, 0, 0);
    }

    public ProcotolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

}
