package xyz.coolcsm.mydubbo.common;

import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author 什锦
 * @Package xyz.coolcsm.mydubbo.common
 * @since 2022/4/22 16:30
 */

public class YmlUtil {

    /**
     * 获取yml文件中的指定字段,返回一个map
     *
     * @param sourcename source name
     * @return
     */
    public static Map<String, Object> getResMap(String sourcename) {
        return YmlInit.getMapByName(YmlInit.ymlMap, sourcename);
    }


    private static class YmlInit {

        //初始化文件得到的map
        private static final Map<String, Object> ymlMap = getYml();

        private static Map<String, Object> getYml() {
            Yaml yml = new Yaml();
            InputStream inputStream = null;
            try {
                inputStream = new ClassPathResource("application.yml").getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            Map<String, Object> map = null;
            try {
               map = (Map<String, Object>) yml.load(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return map;
        }

        // //传入想要得到的字段
        private static Map<String, Object> getMapByName(Map<String, Object> map, String name) {
            Map<String, Object> maps = new HashMap<String, Object>();
            Set<Map.Entry<String, Object>> set = map.entrySet();
            for (Map.Entry<String, Object> entry : set) {// 遍历map
                Object obj = entry.getValue();
                String key = entry.getKey();
                boolean flag = key.equals(name);
                if (flag) {
                    // 递归结束条件
                    return (Map<String, Object>) obj;
                }
                if (entry.getValue() instanceof Map) {
                    //如果value是Map集合递归
                    maps = getMapByName((Map<String, Object>) obj, name);
                    if (maps == null) {
                        //递归的结果如果为空,继续遍历
                        continue;
                    }
                    //不为空返回
                    return maps;
                }
            }
            return null;
        }
    }
}
