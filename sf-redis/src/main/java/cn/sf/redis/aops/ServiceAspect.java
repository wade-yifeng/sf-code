package cn.sf.redis.aops;

import cn.sf.redis.domain.BaseJsonVO;
import cn.sf.redis.enums.ErrorCode;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Order(value = 0)
@Slf4j
public class ServiceAspect {

    @Pointcut("execution(cn.sf.redis.domain.BaseJsonVO cn.sf.redis.service..*.*(..))")
    public void serviceMethod() {
    }

    @Around("serviceMethod()")
    public Object doAround(ProceedingJoinPoint pjp) {

        //打印入参日志
        String paramPre = "Service Param List--> packageName:" + pjp.getSignature().getDeclaringTypeName() + ",methodName:" + pjp.getSignature().getName() + "\t";
        String paramJson = JSONObject.toJSONString(pjp.getArgs());
        log.info(paramPre + paramJson);

        //异常捕获
        BaseJsonVO result = new BaseJsonVO();
        try {
            result = (BaseJsonVO) pjp.proceed();
        } catch (RuntimeException e) {
            log.error("RuntimeException---------------------------------->");
            log.error(Throwables.getStackTraceAsString(e));
            result.setCode(ErrorCode.SERVICE_ERROR.getIntValue());
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Exception---------------------------------->");
            log.error(Throwables.getStackTraceAsString(e));
            result.setCode(ErrorCode.SERVICE_ERROR.getIntValue());
            result.setMessage(e.getMessage());
        } catch (Throwable throwable) {
            log.error("Throwable---------------------------------->");
            log.error(Throwables.getStackTraceAsString(throwable));
            result.setCode(ErrorCode.SERVICE_ERROR.getIntValue());
            result.setMessage(throwable.getMessage());
        }

        //打印响应日志
        String retPre = "Service Return List--> packageName:" + pjp.getSignature().getDeclaringTypeName() + ",methodName:" + pjp.getSignature().getName() + "\t";
        String retJson = JSONObject.toJSONString(result);
        log.info(retPre + retJson);

        return result;
    }


}