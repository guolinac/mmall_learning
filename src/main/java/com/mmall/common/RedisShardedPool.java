package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guolin
 * redis分片连接池
 */
public class RedisShardedPool {
    // sharded jedis连接池
    private static ShardedJedisPool pool;

    // 最大连接数
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));

    // 在jedispool中最大的idle状态(空闲的)的jedis实例的个数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","20"));

    // 在jedispool中最小的idle状态(空闲的)的jedis实例的个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","20"));

    // 在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true。则得到的jedis实例肯定是可以用的。
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));

    // 在return一个jedis实例的时候，是否要进行验证操作，如果赋值true。则放回jedispool的jedis实例肯定是可以用的。
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","true"));

    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");

    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));

    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");

    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));

    /**
     * 初始化连接池
     */
    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        // 连接耗尽的时候，是否阻塞，false会抛出异常，true阻塞直到超时。默认为true。
        config.setBlockWhenExhausted(true);

        // timeout超时时间是毫秒为单位
        JedisShardInfo info1 = new JedisShardInfo(redis1Ip,redis1Port,1000*2);

        // 如果redis有密码，则serPassword
        // info1.setPassword();

        JedisShardInfo info2 = new JedisShardInfo(redis2Ip,redis2Port,1000*2);

        List<JedisShardInfo> jedisShardInfoList = new ArrayList<JedisShardInfo>(2);

        jedisShardInfoList.add(info1);
        jedisShardInfoList.add(info2);

        // Hashing.MURMUR_HASH就是一致性算法；还有一种MD5的算法，这里不用
        pool = new ShardedJedisPool(config,jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    /**
     * 静态代码块，保证这个类在加载到jvm的时候，就初始化连接池
     */
    static{
        initPool();
    }

    /**
     * 从连接池里面拿一个实例
     * @return
     */
    public static ShardedJedis getJedis(){
        return pool.getResource();
    }

    /**
     * 如果是损坏的jedis，则放入损坏池
     * @param jedis
     */
    public static void returnBrokenResource(ShardedJedis jedis){
        pool.returnBrokenResource(jedis);
    }

    /**
     * 把用完的jedis放回去
     * @param jedis
     */
    public static void returnResource(ShardedJedis jedis){
        pool.returnResource(jedis);
    }

    /**
     * 主函数测试ShardedJedisPool
     * @param args
     */
    public static void main(String[] args) {
        // 拿一个jedis
        ShardedJedis jedis = pool.getResource();

        // 插入数据
        for(int i = 0; i < 10; i++) {
            jedis.set("key"+i,"value"+i);
        }

        // 放回连接池
        returnResource(jedis);

        // 临时调用，一般业务代码中不这样用。作用是销毁连接池中的所有连接
//        pool.destroy();

        System.out.println("program is end");
    }
}
