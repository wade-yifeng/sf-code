package cn.sf.auto.aop.config;

import cn.sf.auto.aop.annotations.AutoExcp;
import cn.sf.auto.aop.annotations.AutoExcpSkip;
import cn.sf.auto.aop.excps.IPrintErrorLog;
import cn.sf.auto.aop.excps.IPrintInfoLog;
import cn.sf.auto.aop.excps.IReThrowException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

@Aspect
@Order(value = 0)
@Slf4j
public class AutoExcpAspectJ {

    //@within 用于匹配所以持有指定注解类型内的方法；代理织入
    @Pointcut("@within(cn.sf.auto.aop.annotations.AutoExcp) && execution(public * *(..))")
    public void AutoLogAspectClass() {
    }

    //@annotation 用于匹配当前执行方法持有指定注解的方法；运行切入
    @Pointcut("@annotation(cn.sf.auto.aop.annotations.AutoExcp)")
    public void AutoLogAspectMethod() {
    }

    //[测试模块][测试]Exception List-->cn.sf.auto.log.clazz.ExcpClass#test1:java.lang.RuntimeException
    @Around("AutoLogAspectClass() || AutoLogAspectMethod()")
    public Object doAroundForExcp(final ProceedingJoinPoint pjp) {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Class clazz = pjp.getTarget().getClass();
        //方法上有SkipAutoLog注解,不进行捕获异常
        AutoExcpSkip autoExcpSkip = method.getAnnotation(AutoExcpSkip.class);
        if (null != autoExcpSkip) {
            return checkReturn(pjp);
        }
        //优先使用方法上的注解
        AutoExcp autoExcp = method.getAnnotation(AutoExcp.class);
        if (null == autoExcp) {
            //类上的注解次之
            autoExcp = (AutoExcp) clazz.getAnnotation(AutoExcp.class);
        }
        //没啥用,留着只代表谨慎
        //方法或类上都没有AutoLog注解,不进行捕获异常
        if (null == autoExcp) {
            return checkReturn(pjp);
        }

        StringBuilder sb = new StringBuilder();

        //按注解参数拼装log
        appendAnnoParams(autoExcp, sb);

        //打上类名
        sb.append("Exception List-->");
        sb.append(clazz.getName());
        sb.append("#");

        /*打上方法名*/
        sb.append(method.getName());
        sb.append(":");

        Throwable whatIsExcp = null;

        try {
            return pjp.proceed();
        } catch (RuntimeException e) {
            whatIsExcp = e;
            String tempStr = sb+getStackTraceAsString(e);
            if (e instanceof IPrintInfoLog) {
                log.info(tempStr);
            }else if (e instanceof IPrintErrorLog) {
                log.error(tempStr);
            }else if (e instanceof IReThrowException) {
                log.error(tempStr);
                throw e;
            }else{
                log.error(tempStr);
            }
        } catch (Exception e) {
            whatIsExcp = e;
            String tempStr = sb+getStackTraceAsString(e);
            log.error(tempStr);
        } catch (Throwable throwable) {
            whatIsExcp = throwable;
            String tempStr = sb+getStackTraceAsString(throwable);
            log.error(tempStr);
        }
        return failReturn(whatIsExcp);
    }

    //根据框架不同自由发挥
    protected Object failReturn(Throwable throwable){
//        if (throwable instanceof TimeoutException) {
//            Response result = new Response();
//            result.setError(SysErrorCode.DUBBO_TIMEOUT_EXCEPTION.getDesc());
//            result.setSuccess(false);
//            return result;
//        }
        //默认返回null
        return null;
    }

    private String getStackTraceAsString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    private Object checkReturn(final ProceedingJoinPoint pjp) {
        try {
            return pjp.proceed();
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    private void appendAnnoParams(AutoExcp autoExcp, StringBuilder sb) {
        if (autoExcp.module() != null && !autoExcp.module().trim().equals("")) {
            sb.append("[");
            sb.append(autoExcp.module());
            sb.append("]");
        }

        if (autoExcp.tag() != null && !autoExcp.tag().trim().equals("")) {
            sb.append("[");
            sb.append(autoExcp.tag());
            sb.append("]");
        }
    }
}
