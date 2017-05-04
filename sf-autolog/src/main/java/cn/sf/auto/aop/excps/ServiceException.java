package cn.sf.auto.aop.excps;

import lombok.Data;

import java.util.Map;

@Data
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 自定义errorCode,可根据这个errorCode做异常的筛选、特殊处理
     */
    private int errorCode = SysErrorCode.SERVICE_ERROR.getIntValue();

    /**
     * 异常错误信息
     */
    private String errorMessage = SysErrorCode.SERVICE_ERROR.getDesc();

    /**
     * 异常上下文，可以设置一些关键业务参数
     */
    private Map<String, Object> context;

    public ServiceException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ServiceException(SysErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode.getIntValue();
        this.errorMessage = errorCode.getDesc();
    }

    public ServiceException(SysErrorCode errorCode, Map<String, Object> context, Throwable cause) {
        super(cause);
        this.errorCode = errorCode.getIntValue();
        this.errorMessage = errorCode.getDesc();
        this.context = context;
    }

}
