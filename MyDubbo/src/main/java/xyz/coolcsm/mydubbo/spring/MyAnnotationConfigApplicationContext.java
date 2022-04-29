package xyz.coolcsm.mydubbo.spring;

import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import xyz.coolcsm.mydubbo.annotation.client.RpcReference;
import xyz.coolcsm.mydubbo.annotation.spring.MyAutowired;
import xyz.coolcsm.mydubbo.annotation.spring.MyComponent;
import xyz.coolcsm.mydubbo.annotation.spring.MyComponentScan;
import xyz.coolcsm.mydubbo.common.PackageScanUtils;
import xyz.coolcsm.mydubbo.manager.ClientProxy;
import xyz.coolcsm.mydubbo.manager.RpcClientManager;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.spring
 * @since 2022/4/29 16:17
 */

public class MyAnnotationConfigApplicationContext implements ApplicationContext {

    /**
     * 单例池
     */
    private static ConcurrentHashMap<String, Object> singletonMap;

    public MyAnnotationConfigApplicationContext(final Class<?> configClass) {
        // 初始化
        // 判断是否有扫描注解
        RpcClientManager clientManager = new RpcClientManager();
        ClientProxy clientProxy = new ClientProxy(clientManager);
        try {
            if (configClass.isAnnotationPresent(MyComponentScan.class)) {
                singletonMap = new ConcurrentHashMap<>();
                //得到扫描路劲
                MyComponentScan componentScan = configClass.getAnnotation(MyComponentScan.class);
                String scc = componentScan.value();
                if (scc.equals("") || scc.trim().length() == 0) {
                    String mainClassPath = PackageScanUtils.getStackTrace();
                    scc = mainClassPath.substring(0, mainClassPath.lastIndexOf("."));
                }

                Set<Class<?>> classes = PackageScanUtils.getClasses(scc);
                for (Class<?> clazz : classes) {
                    if (clazz.isAnnotationPresent(MyComponent.class)) {
                        Object obj = clazz.getConstructor().newInstance();
                        Field[] fields = clazz.getDeclaredFields();
                        for (Field field : fields) {
                            if (field.isAnnotationPresent(RpcReference.class)) {
                                field.setAccessible(true);
                                Object proxyService = clientProxy.getProxyService(field.getType());
                                field.set(obj, proxyService);
                            }
                        }
                        String simpleName = clazz.getSimpleName();
                        String beanName = Introspector.decapitalize(simpleName);
                        singletonMap.put(beanName, obj);
                    }
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getBean(String name) {
        if (singletonMap.containsKey(name)) {
            return singletonMap.get(name);
        } else {
            Object obj = this.createBean(name);
            singletonMap.put(name,obj);
            return obj;
        }
    }

    private Object createBean(String beanName) {

        return null;
    }
}
