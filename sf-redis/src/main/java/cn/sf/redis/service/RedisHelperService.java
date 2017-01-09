package cn.sf.redis.service;

import cn.sf.redis.domain.BaseJsonVO;
import cn.sf.redis.domain.CacheInfo;

import java.util.List;

/**
 * Created by hznijianfeng on 2016/3/21.
 */

public interface RedisHelperService {

    //管理接口
    BaseJsonVO<Boolean> clearKMByGroupName(String groupName);
    BaseJsonVO<Boolean> clearKMByKey(String groupName,String key);
    BaseJsonVO<List<CacheInfo>> getKMCacheList();



    //map类型缓存操作接口  km->key map
    BaseJsonVO<Boolean> putKM(String key, String field, Object obj, int expireTime);
    BaseJsonVO<Boolean> delKM(String key,String field);
    BaseJsonVO<Object> getKM(String key,String field);
}