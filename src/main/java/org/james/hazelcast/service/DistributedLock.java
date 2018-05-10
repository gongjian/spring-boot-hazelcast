package org.james.hazelcast.service;

import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class DistributedLock {

	private static final Logger logger = LoggerFactory.getLogger(DistributedLock.class);

	
	@Autowired
	private ConcurrentMap<Integer, String> lockCache;
	 
	//private static ConcurrentMap<Integer, String> lockCache = new ConcurrentHashMap<>();

	@Autowired
	private JedisPool jedisPool;

	public void lockAndContinue(Integer id) {
		boolean waiting = false;
		while (true) {
			logger.info("lockCache: {}", lockCache.hashCode());
			
			String put = lockCache.putIfAbsent(id, String.valueOf(id));

			if (put == null) {
				logger.info("[lock] locked {}", id);
				scheduleAfterTransaction(id);
				return;
			}

			try {
				logger.warn("[lock] waiting to lock {} locked by {}", id, "");
				if (!waiting) {
					waiting = true;
				}
				Thread.sleep(100);// unusual case
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void scheduleAfterTransaction(Integer id) {
		doSomething(id);
		
		lockCache.remove(id);
		jedisPool.getResource().del("lock::" + id);
		
		logger.info("done >>>>>>> ");
	}
	
	private boolean doSomething(int id) {
		Jedis jedisClient = jedisPool.getResource();
		String lockKey = "lock::" + id;

		try {
			boolean lock = jedisClient.setnx(lockKey, "###") == 1 ? true : false;

			if (!lock) {
				logger.error("duplicated error.\n\n error\n error");
			} else {
				Thread.sleep(300);
				logger.info("do something successfully.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedisClient.close();
		}

		return false;
	}

}
