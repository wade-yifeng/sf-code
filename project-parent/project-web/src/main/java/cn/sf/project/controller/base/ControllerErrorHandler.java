package cn.sf.project.controller.base;

import cn.sf.project.aop.excps.SysErrorCode;
import cn.sf.project.excp.JsonResponseException;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.remoting.TimeoutException;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.annotation.Resource;

/**
 * Created by nijianfeng on 16/10/10.
 */
@ControllerAdvice
@Slf4j
public class ControllerErrorHandler {

    @Resource
    private MessageSources messageSources;

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String processBindValidator(BindException ex) {
        //@Length等,Hibernate Validator 附加的 constraint抛出的异常
        BindingResult errors = ex.getBindingResult();
        String errorMessage = errors.getFieldError().getDefaultMessage();
        log.warn("arguments invalid {}", errors);
        if(!StringUtils.isBlank(errorMessage)){
            String ret = messageSources.get(errorMessage);
            if(!StringUtils.isBlank(ret)){
                return ret;
            }else {
                return errorMessage;
            }
        }
        return messageSources.get(SysErrorCode.METHOD_ARGUMENT_NOT_VALID_EXCEPTION.getDesc());
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String processRequiredValidatorError(ServletRequestBindingException ex) {
        log.warn(Throwables.getStackTraceAsString(ex));
        //@RequestParam(required=true)时,参数为空抛出的异常
        if (ex instanceof MissingServletRequestParameterException) {
            return messageSources.get(SysErrorCode.MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION.getDesc(),((MissingServletRequestParameterException) ex).getParameterName());
        } else if (ex instanceof MissingPathVariableException) {
            return messageSources.get(SysErrorCode.MISSING_PATH_VARIABLE_EXCEPTION.getDesc(),((MissingPathVariableException) ex).getVariableName());
        } else {
            return messageSources.get(SysErrorCode.SERVLET_REQUEST_BINDING_EXCEPTION.getDesc());
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String processJsr303ValidatorError(MethodArgumentNotValidException ex) {
        //JSR303注解参数校验异常
        BindingResult errors = ex.getBindingResult();
        String errorMessage = errors.getFieldError().getDefaultMessage();
        log.warn("arguments invalid {}", errors);
        if(!StringUtils.isBlank(errorMessage)){
            String ret = messageSources.get(errorMessage);
            if(!StringUtils.isBlank(ret)){
                return ret;
            }else {
                return errorMessage;
            }
        }
        return messageSources.get(SysErrorCode.METHOD_ARGUMENT_NOT_VALID_EXCEPTION.getDesc());
    }

    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String processTypeValidatorError(TypeMismatchException ex) {
        log.warn(Throwables.getStackTraceAsString(ex));
        //@RequestParam @PathValiable 参数非法时异常
        if (ex instanceof MethodArgumentTypeMismatchException) {
            return messageSources.get(SysErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION.getDesc(),((MethodArgumentTypeMismatchException) ex).getName());
        } else {
            return messageSources.get(SysErrorCode.TYPE_MISMATCH_EXCEPTION.getDesc());
        }
    }

    @ExceptionHandler(JsonResponseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String processJsonResponseException(JsonResponseException ex) {
        //controller抛出异常全局捕获
        String errorCode = ex.getMessage();
        log.warn("拦截到服务端异常的errorCode={}",errorCode);
        if (!StringUtils.isBlank(errorCode)) {
            String errorMessage = messageSources.get(errorCode);
            if (!StringUtils.isBlank(errorMessage)) {
                log.warn("异常信息:{}",errorMessage);
                return errorMessage;
            }
        }
        log.warn(Throwables.getStackTraceAsString(ex));
        return messageSources.get(SysErrorCode.JSON_RESPONSE_EXCEPTION.getDesc());
    }

    @ExceptionHandler(TimeoutException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String processTimeOutError(TimeoutException ex) {
        //controller抛出异常全局捕获
        log.warn(Throwables.getStackTraceAsString(ex));
        return messageSources.get(SysErrorCode.DUBBO_TIMEOUT_EXCEPTION.getDesc());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String processControllerError(Exception ex) {
        //controller抛出异常全局捕获
        String errorCode = ex.getMessage();
        if (!StringUtils.isBlank(errorCode)) {
            String errorMessage = messageSources.get(errorCode);
            if (!StringUtils.isBlank(errorMessage)) {
                log.warn("异常信息:{}",errorMessage);
                return errorMessage;
            }
        }
        log.error(Throwables.getStackTraceAsString(ex));
        return messageSources.get(SysErrorCode.CONTROLLER_ERROR.getDesc());
    }

}