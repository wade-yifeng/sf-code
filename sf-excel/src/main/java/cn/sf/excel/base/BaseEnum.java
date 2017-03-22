package cn.sf.excel.base;

/**
 * Created by nijianfeng on 17/3/22.
 */
public interface BaseEnum<T> {

    T getEnumByDesc(String descValue);
    String getDesc();
    int getValue();

}
