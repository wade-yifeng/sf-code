package cn.sf.redis.manager;

import cn.sf.redis.utils.JedisPoolUtil;
import cn.sf.redis.utils.SerialConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.io.Serializable;
import java.util.Set;

@Component
public class RedisHelperManager {

    private static final Logger logger = LoggerFactory.getLogger(RedisHelperManager.class);

    @Autowired
    private JedisPoolUtil jedisPoolUtil;

    /////////////////////////////////////////////////////////////////////////////////
    // Key-Object Opt ///////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    public void setKO(String key, Object obj, String funcName, int expireSecond) {
        if (obj == null)
            return;

        if (!(obj instanceof Serializable)) {
            throw new RuntimeException("Serializable Error");
        }

        Jedis jedis = null;
        try {
            jedis = jedisPoolUtil.getConnection();
            if (expireSecond != -1) {
                jedis.expire(key.getBytes(), expireSecond);
            }
            jedis.set(key.getBytes(), SerialConvertUtil.serialize(obj));
        } catch (Exception e) {
            throw new RuntimeException(funcName + " Throws Exception", e);
        } finally {
            jedisPoolUtil.releaseConnection(jedis);
        }
    }
    public Object getKO(String key, String funcName) {
        Jedis jedis = null;
        Object val = null;
        try {
            jedis = jedisPoolUtil.getConnection();
            byte[] buff = jedis.get(key.getBytes());
            if (buff != null) {
                val = SerialConvertUtil.unserialize(buff);
            }
        } catch (Exception e) {
            throw new RuntimeException(funcName + " Throws Exception", e);
        } finally {
            jedisPoolUtil.releaseConnection(jedis);
        }

        return val;
    }
    public void delKO(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPoolUtil.getConnection();
            if(jedis.get(key.getBytes())!=null) {
                jedis.del(key.getBytes());
                logger.info("+ end clean cacheElement for jedis:key=" + key);
            }
        } catch (Exception e) {
            throw new RuntimeException(key + " Throws Exception", e);
        } finally {
            jedisPoolUtil.releaseConnection(jedis);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////
    // Key-HashMap Opt ///////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    public void setKM(String key, String field, Object obj, String funcName, int expireSecond) {
        if (obj == null)
            return;
        if (!(obj instanceof Serializable)) {
            throw new RuntimeException("Serializable Error");
        }
        Jedis jedis = null;
        try {
            jedis = jedisPoolUtil.getConnection();
            jedis.hset(key.getBytes(), field.getBytes(),SerialConvertUtil.serialize(obj));
            jedis.expire(key.getBytes(), expireSecond);
        } catch (Exception e) {
            throw new RuntimeException(funcName + " Throws Exception", e);
        } finally {
            jedisPoolUtil.releaseConnection(jedis);
        }
    }
    public Object getKM(String key, String field, String funcName) {
        Jedis jedis = null;
        Object val = null;
        try {
            jedis = jedisPoolUtil.getConnection();
            byte[] buff = jedis.hget(key.getBytes(),field.getBytes());
            if (buff != null) {
                val = SerialConvertUtil.unserialize(buff);
            }
        } catch (Exception e) {
            throw new RuntimeException(funcName + " Throws Exception", e);
        } finally {
            jedisPoolUtil.releaseConnection(jedis);
        }

        return val;
    }
    public void delKM(String key, String... field) {
        Jedis jedis = null;
        try {
            jedis = jedisPoolUtil.getConnection();
            Set<byte[]> fields = jedis.hkeys(key.getBytes());
            if (fields==null||fields.size()==0) {
                return;
            }
            if(field==null||field.length==0){
                for(byte[] fd : fields){
                    jedis.hdel(key.getBytes(),fd);
                }
                logger.info("+ end clean cacheHashMap for jedis:key=" + key);
            }else{
                for(String str : field){
                    jedis.hdel(key.getBytes(),str.getBytes());
                    logger.info("+ end clean cacheHashMapElement for jedis:key=" + key + "  field=" + str);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(key + " Throws Exception", e);
        } finally {
            jedisPoolUtil.releaseConnection(jedis);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////
    // Key-HashMap Oper ///////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    /**
     * map key下的所有field
     */
    public Set<byte[]> getKMFieldsByKey(String key) {
        Jedis jedis = null;
        try {
        jedis = jedisPoolUtil.getConnection();
            return jedis.hkeys(key.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(" Throws Exception", e);
        } finally {
            jedisPoolUtil.releaseConnection(jedis);
        }
    }
    /**
     * 得到剩余缓存时间
     */
    public long getKMKeyTtl(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPoolUtil.getConnection();
            return jedis.ttl(key.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(" Throws Exception", e);
        } finally {
            jedisPoolUtil.releaseConnection(jedis);
        }
    }
    /////////////////////////////////////////////////////////////////////////////////
    // 排序元素集合 Opt ///////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    /**
     * key下的field个数
     */
    public long getCacheManagerFieldCount(byte[] key) {
        Jedis jedis = null;
        try {
            jedis = jedisPoolUtil.getConnection();
            return jedis.zcard(key);
        } catch (Exception e) {
            throw new RuntimeException(" Throws Exception", e);
        } finally {
            jedisPoolUtil.releaseConnection(jedis);
        }
    }
    /**
     * 序列下的key下的从0到end的所有field
     * @return
     */
    public Set<byte[]> getCacheManagerFieldsByKey(byte[] key, long end) {
        Jedis jedis = null;
        try {
            jedis = jedisPoolUtil.getConnection();
            return jedis.zrange(key,0,end);
        } catch (Exception e) {
            throw new RuntimeException(" Throws Exception", e);
        } finally {
            jedisPoolUtil.releaseConnection(jedis);
        }
    }

}