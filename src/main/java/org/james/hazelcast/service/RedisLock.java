package org.james.hazelcast.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import redis.clients.jedis.JedisPool;

@Service
public class RedisLock {

	private static final Logger logger = LoggerFactory.getLogger(RedisLock.class);

	@Autowired
	private JedisPool jedisPool;

	public void lockAndContinue(Integer id) {
		boolean waiting = false;
		while (true) {
			boolean flag = jedisPool.getResource().setnx(String.valueOf(id), "***") == 1 ? true : false;

			if (flag) {
				logger.info("[lock] locked {}", id);
				scheduleAfterTransaction(id);
				return;
			}

			try {
				logger.warn("[lock] waiting to lock {} locked by {}", id, "");
				if (!waiting) {
					waiting = true;
				}
				Thread.sleep(500);// unusual case
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void scheduleAfterTransaction(Integer id) {
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {

			@Override
			public void afterCommit() {
				jedisPool.getResource().del(String.valueOf(id));
				jedisPool.getResource().del("lock::" + id);
			}

			@Override
			public void afterCompletion(int status) {

			}
		});
	}

}
