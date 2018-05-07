package org.james.hazelcast.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;

@Configuration
public class HazelcastConfiguration
{
  @Bean
  @Profile({"master"})
  public Config masterConfig()
  {
    Config config = new Config();

    config.setInstanceName("master-hazelcast-instance");
    config.getNetworkConfig().setPortAutoIncrement(false);

    NetworkConfig network = config.getNetworkConfig();
    network.setPort(5701).setPortAutoIncrement(false);
    JoinConfig join = network.getJoin();
    join.getMulticastConfig().setEnabled(false);
    join.getTcpIpConfig().addMember("10.100.19.182:5701").addMember("10.100.19.182:6701").setEnabled(true);

    return config;
  }
  @Bean
  @Profile({"slave"})
  public Config slaveConfig() {
    Config config = new Config();

    config.setInstanceName("slave-hazelcast-instance");
    config.getNetworkConfig().setPortAutoIncrement(false);

    NetworkConfig network = config.getNetworkConfig();
    network.setPort(6701).setPortAutoIncrement(false);

    JoinConfig join = network.getJoin();
    join.getMulticastConfig().setEnabled(false);
    join.getTcpIpConfig().addMember("10.100.19.182:5701").addMember("10.100.19.182:6701").setEnabled(true);

    return config;
  }
}