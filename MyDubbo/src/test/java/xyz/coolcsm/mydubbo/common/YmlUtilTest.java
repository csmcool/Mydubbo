package xyz.coolcsm.mydubbo.common;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class YmlUtilTest {
    public static void main(String[] args) {
        Map<String, Object> compression = YmlUtil.getResMap("compression");
        System.out.println(compression);
    }
}