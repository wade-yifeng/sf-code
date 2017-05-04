package cn.sf.auto.aop.config;

import cn.sf.auto.aop.annotations.AutoExcp;
import cn.sf.auto.aop.annotations.AutoExcpSkip;
import cn.sf.auto.aop.excps.IPrintErrorLog;
import cn.sf.auto.aop.excps.IPrintInfoLog;
import cn.sf.auto.aop.excps.IReThrowException;
import cn.sf.auto.aop.excps.ServiceException;
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
import java.sql.SQLException;

@Aspect
@Order(value = 0)
@Slf4j
public class AutoExcpAspectJ {

    //@within 用于匹配所以持有指定注解类型内的方法；代理织入
    @Pointcut("@within(cn.sf.auto.aop.annotations.AutoExcp) && execution(public * *(..))")
    public void AutoExcpAspectClass() {
    }

    //@annotation 用于匹配当前执行方法持有指定注解的方法；运行切入
    @Pointcut("@annotation(cn.sf.auto.aop.annotations.AutoExcp)")
    public void AutoExcpAspectMethod() {
    }

    //[测试模块][测试]Exception List-->cn.sf.auto.log.clazz.ExcpClass#test1:java.lang.RuntimeException
    @Around("AutoExcpAspectClass() || AutoExcpAspectMethod()")
    public Object doAroundForExcp(final ProceedingJoinPoint pjp) {
        Class clazz = pjp.getTarget().getClass();
        Method iMethod = ((MethodSignature) pjp.getSignature()).getMethod();//interface或者class方法,实现类可用接口来调用
        Method method;//真实类的方法
        try {
            method = clazz.getMethod(iMethod.getName(),iMethod.getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
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

        Class returnType = ((MethodSignature) pjp.getSignature()).getReturnType();
        try {
            return pjp.proceed();
        } catch (ServiceException ex) {
            log.info("======================ServiceException======================");
            log.warn("code:{}, message:{}, context:{}", ex.getErrorCode(), ex.getErrorMessage(), ex.getContext());
            log.warn(sb + getStackTraceAsString(ex));
            return failReturn(ex.getErrorMessage(), returnType);
        } catch (SQLException ex) {
            log.error("======================SQLException======================");
            log.error(sb + getStackTraceAsString(ex));
            return failReturn("sql exception", returnType);
        }catch (RuntimeException ex) {
            if (ex instanceof IPrintInfoLog) {
                log.info("======================IPrintInfoLog======================");
                log.info(sb + getStackTraceAsString(ex));
                return failReturn("info exception!", returnType);
            }else if (ex instanceof IPrintErrorLog) {
                log.error("======================IPrintErrorLog======================");
                log.error(sb + getStackTraceAsString(ex));
                return failReturn("error exception!", returnType);
            }else if (ex instanceof IReThrowException) {
                log.error("======================IReThrowException======================");
                log.error(sb + getStackTraceAsString(ex));
                throw ex;
            }else{
                log.error("======================RuntimeException======================");
                log.error(sb + getStackTraceAsString(ex));
                return failReturn("run time exception!", returnType);
            }
        } catch (Exception ex) {
            log.error("======================Exception======================");
            log.error(sb + getStackTraceAsString(ex));
            return failReturn("exception!", returnType);
        } catch (Throwable ex) {
            log.error("======================Throwable======================");
            log.error(sb + getStackTraceAsString(ex));
            return failReturn("throwable!", returnType);
        }
    }

    //根据框架不同自由发挥
    protected Object failReturn(String errorMessage, Class returnType){
//        if (Response.class == returnType) {
//            return Response.fail(errorMessage);
//        }
//        if (OpenResponse.class == returnType) {
//            return OpenResponse.fail(errorMessage);
//        }
        //基础类型int boolean等的判断
        if (Boolean.TYPE == returnType) {
            return false;
        }
        if (Byte.TYPE == returnType) {
            return (byte) 0;
        }
        if (Short.TYPE == returnType) {
            return (short) 0;
        }
        if (Integer.TYPE == returnType) {
            return 0;
        }
        if (Float.TYPE == returnType) {
            return 0f;
        }
        if (Long.TYPE == returnType) {
            return 0L;
        }
        if (Double.TYPE == returnType) {
            return 0d;
        }
        if (Character.TYPE == returnType) {
            return (char) 0;
        }
        try {
            return returnType.newInstance();
        } catch (Exception ex) {
            log.warn("没有默认构造方法",ex);
            return null;
        }
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
