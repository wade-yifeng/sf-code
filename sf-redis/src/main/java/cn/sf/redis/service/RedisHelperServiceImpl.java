package cn.sf.redis.service;

import cn.sf.redis.domain.BaseJsonVO;
import cn.sf.redis.domain.CacheInfo;
import cn.sf.redis.enums.ErrorCode;
import cn.sf.redis.enums.RedisType;
import cn.sf.redis.manager.RedisHelperManager;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * Created by hznijianfeng on 2016/3/21.
 */

@Component
@Slf4j
public class RedisHelperServiceImpl implements RedisHelperService {

    private static StringRedisSerializer stringSerializer = new StringRedisSerializer();

    @Autowired
    private RedisHelperManager redisHelperManager;

    @Override
    public BaseJsonVO<Boolean> clearKMByGroupName(String groupName) {
        if (groupName != null) {
            redisHelperManager.delKM(groupName);
        }else{
            BaseJsonVO.fail(ErrorCode.PARAM_ERROR);
        }
        return BaseJsonVO.ok(Boolean.TRUE);
    }

    @Override
    public BaseJsonVO<Boolean> clearKMByKey(String groupName,String key) {
        if (groupName != null && key != null) {
                redisHelperManager.delKM(groupName,key);
        }else{
            BaseJsonVO.fail(ErrorCode.PARAM_ERROR);
        }
        return BaseJsonVO.ok(Boolean.TRUE);
    }

    @Override
    public BaseJsonVO<List<CacheInfo>> getKMCacheList() {
        BaseJsonVO<List<CacheInfo>> BaseJsonVO = new BaseJsonVO<List<CacheInfo>>();
        List<CacheInfo> cacheInfos = Lists.newArrayList();
        Set<String> strs = RedisType.NULL.getAllKeyPrefix();
        for (String str : strs) {
            String[] typeArray = str.split("-");
            if(typeArray.length==2) {
                Set<byte[]> fields = redisHelperManager.getKMFieldsByKey(typeArray[0]);
                if (!CollectionUtils.isEmpty(fields)) {
                    CacheInfo cacheInfo = new CacheInfo();
                    cacheInfo.setCount(fields.size());
                    cacheInfo.setKey(typeArray[0]);
                    cacheInfo.setExpireTime(typeArray[1]);
                    cacheInfo.setTtl(redisHelperManager.getKMKeyTtl(typeArray[0]) + "");
                    cacheInfo.setUrl("/redis/manager/clearKMByGroupName?groupName=" + typeArray[0]);
                    for (byte[] field : fields) {
                        String fd = stringSerializer.deserialize(field);
                        cacheInfo.addFieldInfo(fd, "/redis/manager/clearKMByKey?groupName=" + typeArray[0] + "&key=" + fd);
                    }
                    cacheInfos.add(cacheInfo);
                }
            }
        }
        return BaseJsonVO.ok(cacheInfos);
    }

    @Override
    public BaseJsonVO<Boolean> putKM(String key, String field, Object obj, int expireTime) {
        BaseJsonVO<Boolean> BaseJsonVO = new BaseJsonVO<Boolean>();
        redisHelperManager.setKM(key, field, obj, "RedisHelperServiceImpl.putKM", expireTime);
        BaseJsonVO.setResult(true);
        return BaseJsonVO;
    }
    @Override
    public BaseJsonVO<Boolean> delKM(String key, String field) {
        redisHelperManager.delKM(key, field);
        return BaseJsonVO.ok(Boolean.TRUE);
    }
    @Override
    public BaseJsonVO<Object> getKM(String key, String field) {
        BaseJsonVO<Object> BaseJsonVO = new BaseJsonVO<Object>();
        Object obj = redisHelperManager.getKM(key, field, "RedisHelperServiceImpl.getKM");
        BaseJsonVO.setResult(obj);
        return BaseJsonVO;
    }

}