/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

/**
 *
 * @author tuhalang
 */
public class JedisConnectionPool {

    private static JedisSentinelPool jedisSentinelPool;
    private static Object mutex = new Object();

    public static JedisSentinelPool getPoolConnection() {
        JedisSentinelPool localRef = jedisSentinelPool;
        if (localRef == null) {
            synchronized (mutex) {
                localRef = jedisSentinelPool;
                if (localRef == null) {
                    GenericObjectPoolConfig config = new GenericObjectPoolConfig();
                    config.setMaxTotal(1);
                    config.setBlockWhenExhausted(false);

                    String[] ipRedis = GameConfig.IP_ADDRESS.split(";");
                    Set<String> sentinels = new HashSet<>();
                    sentinels.addAll(Arrays.asList(ipRedis));
                    localRef = jedisSentinelPool = new JedisSentinelPool(GameConfig.MASTER_NAME, sentinels, config);
                }
            }
        }
        return localRef;
    }
}
