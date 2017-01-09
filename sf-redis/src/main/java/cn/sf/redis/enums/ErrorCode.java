package cn.sf.redis.enums;

import com.google.common.collect.Maps;

import java.util.Map;

public enum ErrorCode {
    /**
     * 异常
     */
    NULL(-1, "异常状态"),
    /**
     * 成功code
     */
    SUCCESS(200, "success"),
    /**
     * 系统错误
     */
    SERVICE_ERROR(500, "service.unknown.exception"),
    /**
     * 常用异常1000-1500
     */
    PARAM_ERROR(1000, "param.error"),
    ;

    /**
     * 值
     */
    private final int value;

    /**
     * 描述
     */
    private final String desc;

    private static Map<Integer, ErrorCode> map = Maps.newHashMap();

    static {
        for (ErrorCode item : ErrorCode.values()) {
            map.put(item.getIntValue(), item);
        }
    }

    /**
     * 构造函数
     *
     * @param v
     * @param d
     */
    ErrorCode(int v, String d) {
        value = v;
        desc = d;
    }

    public static ErrorCode genEnumByKey(int key) {
        return map.get(key) == null ? map.get(-1) : map.get(key);
    }

    public int getIntValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

}
