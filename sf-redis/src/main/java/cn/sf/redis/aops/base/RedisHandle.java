package cn.sf.redis.aops.base;

import cn.sf.redis.enums.RedisType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedisHandle {

    RedisType redisType() default RedisType.DEFAULT;
}
