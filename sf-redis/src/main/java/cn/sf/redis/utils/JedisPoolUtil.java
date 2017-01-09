package cn.sf.redis.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * cache连接工具类
 * 获取cache连接调用:getConnection()
 * 注意:用完时最终一定要调用releaseConnection方法返回连接到连接池
 */
@Component
public class JedisPoolUtil implements InitializingBean {

	private JedisPool pool;

	@Autowired
	private JedisConnectionFactory jedisConnectionFactory;

	public void afterPropertiesSet() throws Exception {
		// 根据配置实例化jedis池
		pool = new JedisPool(jedisConnectionFactory.getPoolConfig(), jedisConnectionFactory.getHostName(),
				jedisConnectionFactory.getPort(), jedisConnectionFactory.getTimeout(),
				jedisConnectionFactory.getPassword());

	}

	public Jedis getConnection() {
		Jedis jedis = pool.getResource();
		return jedis;
	}

	public void releaseConnection(Jedis jedis) {
		if (jedis == null) {
			return;
		}
		jedis.close();
	}

}