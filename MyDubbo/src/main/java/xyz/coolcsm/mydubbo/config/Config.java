package xyz.coolcsm.mydubbo.config;

import xyz.coolcsm.mydubbo.common.YmlUtil;
import xyz.coolcsm.mydubbo.protocol.Serializer;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.config
 * @since 2022/4/22 15:54
 */

public class Config {

    public static int getServerPort() {
        Map<String, Object> resMap = YmlUtil.getResMap("server");
        if(resMap == null) {
            return 8080;
        } else {
            return Integer.parseInt(resMap.get("port").toString());
        }
    }
    public static Serializer.Algorithm getSerializerAlgorithm() {
        Map<String, Object> resMap = YmlUtil.getResMap("serializer");
        if(resMap == null) {
            return Serializer.Algorithm.Java;
        } else {
            return Serializer.Algorithm.valueOf(resMap.get("algorithm").toString());
        }
    }

    /** 获取主机地址 */
    public static String getHostIp(){

        String realIp = null;

        try {
            InetAddress address = InetAddress.getLocalHost();

            // 如果是回环网卡地址, 则获取ipv4 地址
            if (address.isLoopbackAddress()) {
                address = getInet4Address();
            }

            realIp = address.getHostAddress();
            return address.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return realIp;
    }

    /** 获取IPV4网络配置 */
    private static InetAddress getInet4Address() throws SocketException {
        // 获取所有网卡信息
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = (NetworkInterface) networkInterfaces.nextElement();
            Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress ip = (InetAddress) addresses.nextElement();
                if (ip instanceof Inet4Address) {
                    return ip;
                }
            }
        }
        return null;
    }
}
