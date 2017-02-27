package cn.sf.utils.mybatis;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * value 替换 ordinal
 */
public class EnumValueTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
    private Class<E> type;
    private final Map<Integer, E> enumsMap = new HashMap<>();

    private int getEnumValue(E parameter) {
        try {
            return (int) type.getMethod("value").invoke(parameter);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("enum type haven't well value", e);
        }
    }

    public EnumValueTypeHandler(Class<E> type) {
        if (type == null) throw new IllegalArgumentException("Type argument cannot be null");
        this.type = type;
        E[] enums = type.getEnumConstants();
        if (enums == null) throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
        for (E e : enums) {
            enumsMap.put(getEnumValue(e), e);
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, getEnumValue(parameter));
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int i = rs.getInt(columnName);
        if (rs.wasNull()) {
            return null;
        } else {
            return enumsMap.get(i);
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int i = rs.getInt(columnIndex);
        if (rs.wasNull()) {
            return null;
        } else {
            return enumsMap.get(i);
        }
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int i = cs.getInt(columnIndex);
        if (cs.wasNull()) {
            return null;
        } else {
            return enumsMap.get(i);
        }
    }
}
