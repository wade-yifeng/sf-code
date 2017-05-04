package cn.sf.auto.aop.excps;

import java.util.HashMap;
import java.util.Map;

public enum SysErrorCode {
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
    CONTROLLER_ERROR(501, "controller.unknown.exception"),
    /**
     * 常用异常
     */
    METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION(400, "method.argument.type.mismatch.exception"),
    MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION(401, "missing.servlet.request.parameter.exception"),
    MISSING_PATH_VARIABLE_EXCEPTION(402, "missing.path.variable.exception"),
    JSON_RESPONSE_EXCEPTION(403, "json.response..exception"),
    GET_LOGIN_USER_ERROR(404, "get.loginUser.error"),
    DUBBO_TIMEOUT_EXCEPTION(405, "dubbo.timeout.exception"),
    TYPE_MISMATCH_EXCEPTION(406, "type.mismatch.exception"),
    METHOD_ARGUMENT_NOT_VALID_EXCEPTION(407, "method.argument.not.valid.exception"),
    SERVLET_REQUEST_BINDING_EXCEPTION(408, "servlet.request.binding.exception"),
    BIND_EXCEPTION(409, "bind.exception"),
    DATA_NOT_EXIST(410, "data.not.exist"),
    IMPORT_ERROR(411, "import.data.invalid"),
    STATUS_MACHINE_FAIL_EXCEPTION(412, "next.status.is.invalid"),
    OPTYPE_INVALID(413, "operation.type.invalid"),
    /**
     * 逻辑检验错误 1000-1500
     */



    ;

    /**
     * 值
     */
    private final int value;

    /**
     * 描述
     */
    private final String desc;

    private static Map<Integer, SysErrorCode> map = new HashMap<>();

    static {
        for (SysErrorCode item : SysErrorCode.values()) {
            map.put(item.getIntValue(), item);
        }
    }

    /**
     * 构造函数
     *
     * @param v
     * @param d
     */
    SysErrorCode(int v, String d) {
        value = v;
        desc = d;
    }

    public static SysErrorCode genEnumByKey(int key) {
        return map.get(key) == null ? map.get(-1) : map.get(key);
    }

    public int getIntValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

}