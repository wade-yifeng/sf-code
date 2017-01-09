package cn.sf.redis.enums;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

public enum RedisType {
    /**
     * 异常
     */
    NULL(-1, "异常状态", -1),
    DEFAULT(0, "zcy_default",60*60*24*30),
    /**
     * 用户模块1-100
     */
    USER_MODULE(1, "user_module",60*60*24*30),

    /**
     * 定点模块101-200
     */

    ;

    /**
     * 值
     */
    private final int value;

    /**
     * 描述
     */
    private final String groupName;
    private final int expireTime;

    private static Map<Integer, RedisType> map = Maps.newHashMap();

    static {
        for (RedisType item : RedisType.values()) {
            map.put(item.getIntValue(), item);
        }
    }

    /**
     * 构造函数
     *
     * @param v
     * @param d
     */
    RedisType(int v, String d, int e) {
        value = v;
        groupName = d;
        expireTime = e;
    }

    public static RedisType genEnumByKey(int key) {
        return map.get(key) == null ? map.get(-1) : map.get(key);
    }

    public int getIntValue() {
        return value;
    }
    public String getGroupName() {
        return groupName;
    }
    public int getExpireTime() {
        return expireTime;
    }

    public Set<String> getAllKeyPrefix() {
        Set<String> strs = Sets.newHashSet();
        for (RedisType item : RedisType.values()) {
            if (!item.equals(RedisType.NULL)){
                strs.add(item.getGroupName()+"-"+item.getExpireTime());
            }
        }
        return strs;
    }

}
