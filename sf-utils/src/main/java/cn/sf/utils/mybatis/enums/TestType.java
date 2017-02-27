package cn.sf.utils.mybatis.enums;

import com.google.common.collect.Maps;

import java.util.Map;

public enum TestType {
    /**
     * 异常
     */
    NULL(-1, "异常状态"),
    /**
     * 状态枚举
     */
    NO(0, "停用"),
    YES(1, "启用"),


    ;

    /**
     * 值
     */
    private final int value;

    /**
     * 描述
     */
    private final String desc;

    private static Map<Integer, TestType> map = Maps.newHashMap();

    static {
        for (TestType item : TestType.values()) {
            map.put(item.value(), item);
        }
    }

    /**
     * 构造函数
     *
     * @param v
     * @param d
     */
    TestType(int v, String d) {
        value = v;
        desc = d;
    }

    public static TestType genEnumByKey(Integer key) {
        return map.get(key)==null?map.get(-1):map.get(key);
    }

    public int value() {
        return value;
    }

    public String desc() {
        return desc;
    }
}
