package cn.sf.redis.aops;

import cn.sf.redis.aops.base.RedisHandle;
import cn.sf.redis.manager.RedisHelperManager;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Order(value = 0)
@Slf4j
public class RedisAspect {

    @Autowired
    private RedisHelperManager redisHelperManager;

    @Pointcut("execution(@cn.sf.redis.aops.base.RedisHandle * cn.sf.redis.test..*.*(..))")
    public void redisMethod() {
    }

    @Around("redisMethod()&&@annotation(redisHandle)")
    public Object doAround(ProceedingJoinPoint pjp, RedisHandle redisHandle) {

        String groupName = redisHandle.redisType().getGroupName();
        int expireTime = redisHandle.redisType().getExpireTime();

        //打印入参日志
        String paramPre = "Redis Param List--> packageName:" + pjp.getSignature().getDeclaringTypeName() + ",methodName:" + pjp.getSignature().getName() + "\t";
        String paramJson = JSONObject.toJSONString(pjp.getArgs());
        log.info(paramPre + paramJson);

        //缓存处理
        String mapKey = pjp.getSignature().getDeclaringTypeName()+"_"+
                        pjp.getSignature().getName()+"_";
        for(Object obj : pjp.getArgs()){
            //只支持long,string,ing等拥有不变性等基础信息入参有效,也可重写to string方式可以适配
            mapKey += obj.toString()+"_";
        }
        Object var = redisHelperManager.getKM(groupName,mapKey,pjp.getSignature().getName());
        if(var!=null){
            log.info("命中缓存groupName="+ groupName +"	,key="+mapKey+",value="+JSONObject.toJSONString(var));
        }else {
            try {
                var = pjp.proceed();
                log.info("存入缓存groupName="+ groupName +"	,key="+mapKey+",value="+JSONObject.toJSONString(var));
                redisHelperManager.setKM(groupName,mapKey,var,pjp.getSignature().getName(), expireTime);
            } catch (Throwable throwable) {
                log.error("Throwable---------------------------------->");
                log.error(Throwables.getStackTraceAsString(throwable));
                throw new RuntimeException(throwable);
            }
        }
        return var;
    }

}