package xyz.coolcsm.mydubbo;

import xyz.coolcsm.mydubbo.annotation.server.RpcServiceScan;
import xyz.coolcsm.mydubbo.common.YmlUtil;
import xyz.coolcsm.mydubbo.config.Config;
import xyz.coolcsm.mydubbo.manager.RpcServiceManager;

import java.util.Map;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo
 * @since 2022/4/24 14:36
 */
@RpcServiceScan
public class RpcServer {
    public static void main(String[] args) {
        try {
            new RpcServiceManager().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
