package xyz.coolcsm.mydubbo.register;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import xyz.coolcsm.mydubbo.common.YmlUtil;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.register
 * @since 2022/4/22 16:48
 */

public class NacosUtils {

    private final static NamingService namingService;

    private static final Set<String> serviceNames = new HashSet<>();

    private static InetSocketAddress address;

    private final static Map<String, Object> resMap = YmlUtil.getResMap("nacos");

    private final static String GROUP_NAME = "DEFAULT_GROUP";

    private final static String CLUSTER_NAME = "DEFAULT";

    static {
        namingService = getNacosNamingService();
    }

    //初始化
    public static NamingService getNacosNamingService() {
        try {
            String serverAddr = (String) resMap.get("server-addr");
            Properties prop = System.getProperties();
            prop.put("serverAddr", serverAddr);
            return NamingFactory.createNamingService(prop);
        } catch (NacosException e) {
            throw new RuntimeException("连接到Nacos时发生错误");
        }
    }

    /**
     * 注册服务
     *
     * @param serverName
     * @param address
     * @throws NacosException
     */
    public static void registerServer(String serverName, InetSocketAddress address) throws NacosException {
        namingService.registerInstance(serverName, address.getHostName(), address.getPort());
        NacosUtils.address = address;
        serviceNames.add(serverName);
    }

    /**
     * 注册服务
     *
     * @param serverName 服务名
     * @param address   地址
     * @param metadata  元数据
     * @throws NacosException 异常
     */
    public static void registerServer(String serverName, InetSocketAddress address,Map<String,String> metadata) throws NacosException {
        Instance instance = new Instance();
        instance.setIp(address.getHostName());
        instance.setPort(address.getPort());
        instance.setWeight(1.0D);
        instance.setClusterName(CLUSTER_NAME);
        instance.setMetadata(metadata);
        namingService.registerInstance(serverName, GROUP_NAME, instance);
        NacosUtils.address = address;
        serviceNames.add(serverName);
    }

    /**
     * 获取当前服务名中的所有实例
     *
     * @param serverName
     * @return
     * @throws NacosException
     */
    public static List<Instance> getAllInstance(String serverName) throws NacosException {
        return namingService.getAllInstances(serverName);
    }

    /**
     * 获取当前服务名中的所有实例
     *
     * @param serviceName
     * @return
     * @throws NacosException
     */
    public static List<Instance> getAllInstance(String serviceName, List<String> clusters) throws NacosException {
        return namingService.getAllInstances(serviceName,clusters);
    }

    /**
     * 注销服务
     */
    public static void clearRegister() {
        if (!serviceNames.isEmpty() && address != null) {
            String host = address.getHostName();
            int port = address.getPort();
            Iterator<String> iterator = serviceNames.iterator();
            while (iterator.hasNext()) {
                String serviceName = iterator.next();
                try {
                    namingService.deregisterInstance(serviceName, host, port);
                } catch (NacosException e) {
                    new RuntimeException("注销服务失败");
                }
            }
        }

    }
}
