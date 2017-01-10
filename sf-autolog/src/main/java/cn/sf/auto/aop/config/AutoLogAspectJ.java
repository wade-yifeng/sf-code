package cn.sf.auto.aop.config;

import cn.sf.auto.aop.annotations.AutoLog;
import cn.sf.auto.aop.annotations.AutoLogSkip;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

@Aspect
@Order(value = 0)
@Slf4j
public class AutoLogAspectJ {

    //@within 用于匹配所以持有指定注解类型内的方法；代理织入
    @Pointcut("@within(cn.sf.auto.aop.annotations.AutoLog) && execution(public * *(..))")
    public void AutoLogAspectClass() {
    }

    //@annotation 用于匹配当前执行方法持有指定注解的方法；运行切入
    @Pointcut("@annotation(cn.sf.auto.aop.annotations.AutoLog)")
    public void AutoLogAspectMethod() {
    }

    //[测试模块][测试]Param List-->cn.sf.auto.log.clazz.LogClass#test1:["input"]
    @Before(value = "AutoLogAspectClass() || AutoLogAspectMethod()")
    public void doServiceBefore(final JoinPoint point) {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Class clazz = point.getTarget().getClass();

        //方法上有SkipAutoLog注解,不打log
        if (isSkipAutoLogWork(method)) {
            return;
        }

        //优先使用方法上的注解
        AutoLog autoLog = getAutoLogAnno(method, clazz);
        if (null == autoLog) {
            //方法或类上都没有AutoLog注解,不打log
            return;
        }

        if (autoLog.logParam()) {
            logBefore(method, clazz, autoLog, point.getArgs());
        }
    }

    //[测试模块][测试]Return List-->cn.sf.auto.log.clazz.LogClass#test1:"output"
    @AfterReturning(returning = "rtObj", value = "AutoLogAspectClass() || AutoLogAspectMethod()")
    public void doServiceAfter(final JoinPoint point, final Object rtObj) {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Class clazz = point.getTarget().getClass();

        //方法上有SkipAutoLog注解,不打log
        if (isSkipAutoLogWork(method)) {
            return;
        }

        //优先使用方法上的注解
        AutoLog autoLog = getAutoLogAnno(method, clazz);
        if (null == autoLog) {
            //方法或类上都没有AutoLog注解,不打log
            return;
        }

        if (autoLog.logResult()) {
            logAfter(method, clazz, autoLog, rtObj);
        }
    }


    private void logBefore(final Method method, final Class clazz, final AutoLog autoLog, final Object[] args) {

        StringBuilder sb = new StringBuilder();

        //按注解参数拼装log
        appendAnnoParams(autoLog, sb);

        //打上类名
        sb.append("Param List-->");
        sb.append(clazz.getName());
        sb.append("#");

        /*打上方法名*/
        sb.append(method.getName());
        sb.append(":");

        /*打上入参*/
        String jsonStr = JSONObject.toJSONString(args);
        sb.append(jsonStr);

        log.info(sb.toString());
    }

    private void logAfter(final Method method, final Class clazz, final AutoLog autoLog, final Object rtObj) {

        StringBuilder sb = new StringBuilder();

        //按注解参数拼装log
        appendAnnoParams(autoLog, sb);

        //打上类名
        sb.append("Return List-->");
        sb.append(clazz.getName());
        sb.append("#");

        /*打上方法名*/
        sb.append(method.getName());
        sb.append(":");

        //打上返回
        String jsonStr = JSONObject.toJSONString(rtObj);
        sb.append(jsonStr);

        log.info(sb.toString());
    }

    private boolean isSkipAutoLogWork(Method method) {
        AutoLogSkip autoLogSkip = method.getAnnotation(AutoLogSkip.class);
        if (null != autoLogSkip) {
            return true;
        } else {
            return false;
        }
    }

    private AutoLog getAutoLogAnno(Method method, Class clazz) {

        //优先使用方法上的注解
        AutoLog autoLog = method.getAnnotation(AutoLog.class);
        if (null == autoLog) {
            //类上的注解次之
            autoLog = (AutoLog) clazz.getAnnotation(AutoLog.class);
        }

        return autoLog;
    }

    private void appendAnnoParams(AutoLog autoLog, StringBuilder sb) {
        if (autoLog.module() != null && !autoLog.module().trim().equals("")) {
            sb.append("[");
            sb.append(autoLog.module());
            sb.append("]");
        }

        if (autoLog.tag() != null && !autoLog.tag().trim().equals("")) {
            sb.append("[");
            sb.append(autoLog.tag());
            sb.append("]");
        }
    }

}
