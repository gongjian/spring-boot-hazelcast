package org.james.hazelcast.configuration;

import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.HazelcastInstance;

@Configuration
public class HazelcastConfiguration {
	private static final String LOCK_CACHE = "lock_cache";
	
	@Bean
	@Profile({ "master" })
	public Config masterConfig() {
		Config config = new Config();

		config.setInstanceName("master-hazelcast-instance");

		NetworkConfig network = config.getNetworkConfig();
		network.setPort(5701).setPortAutoIncrement(false);
		JoinConfig join = network.getJoin();
		join.getMulticastConfig().setEnabled(false);
		//join.getTcpIpConfig().addMember("127.0.0.1:5701").addMember("127.0.0.1:6701").setEnabled(true);
		join.getTcpIpConfig().addMember("10.100.19.182:5701").addMember("10.100.19.186:5701").setEnabled(true);
		
		MapConfig mapConfig = new MapConfig();
		mapConfig.setName(LOCK_CACHE);
		mapConfig.setTimeToLiveSeconds(600);
		config.addMapConfig(mapConfig);

		return config;
	}

	@Bean
	@Profile({ "slave" })
	public Config slaveConfig() {
		Config config = new Config();

		config.setInstanceName("slave-hazelcast-instance");

		NetworkConfig network = config.getNetworkConfig();
		network.setPort(6701).setPortAutoIncrement(false);

		JoinConfig join = network.getJoin();
		join.getMulticastConfig().setEnabled(false);
		//join.getTcpIpConfig().addMember("127.0.0.1:5701").addMember("127.0.0.1:6701").setEnabled(true);
		join.getTcpIpConfig().addMember("10.100.19.182:5701").addMember("10.100.19.186:5701").setEnabled(true);
		MapConfig mapConfig = new MapConfig();
		mapConfig.setName(LOCK_CACHE);
		mapConfig.setTimeToLiveSeconds(600);
		config.addMapConfig(mapConfig);

		return config;
	}
	
	@Autowired
	private HazelcastInstance hazelcastInstance;
	
	@Bean
	public ConcurrentMap<Integer, String> lockCache() {
		return hazelcastInstance.getMap(LOCK_CACHE);
	}
}