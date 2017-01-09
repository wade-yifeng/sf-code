package cn.sf.redis.test;

import cn.sf.redis.aops.base.RedisHandle;
import cn.sf.redis.enums.RedisType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by nijianfeng on 16/12/9.
 */
@Component
public class TestService {

    @RedisHandle(redisType = RedisType.USER_MODULE)
    public List<Integer> getList(Integer param){
        System.out.println("进入真实方法");
        return Arrays.asList(1,2,3,4,5);
    }
}
