package org.james.hazelcast.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class JedisConfiguration {

	private static Logger logger = LoggerFactory.getLogger(JedisConfiguration.class);

	private String hostName;

	private int port;

	private String password;

	private int timeout;

	@Bean
	public JedisPool getJedisPool() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(-1);
		JedisPool pool = new JedisPool(config, hostName, port, timeout, password);
		logger.info("init JredisPool ...");
		return pool;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}
