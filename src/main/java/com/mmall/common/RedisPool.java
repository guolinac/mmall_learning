package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by guolin
 * redis连接池
 */
public class RedisPool {
    // jedis连接池
    private static JedisPool pool;

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

    private static String redisIp = PropertiesUtil.getProperty("redis.ip");

    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis.port"));

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
        pool = new JedisPool(config,redisIp,redisPort,1000*2);
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
    public static Jedis getJedis(){
        return pool.getResource();
    }

    /**
     * 如果是损坏的jedis，则放入损坏池
     * @param jedis
     */
    public static void returnBrokenResource(Jedis jedis){
        pool.returnBrokenResource(jedis);
    }

    /**
     * 把用完的jedis放回去
     * @param jedis
     */
    public static void returnResource(Jedis jedis){
        pool.returnResource(jedis);
    }

    /**
     * 主函数测试jedispoll
     * @param args
     */
    public static void main(String[] args) {
        // 拿一个jedis
        Jedis jedis = pool.getResource();
        // 插入一个数据
        jedis.set("guokey","guovalue");
        // 放回连接池
        returnResource(jedis);

        // 临时调用，一般业务代码中不这样用。作用是销毁连接池中的所有连接
        pool.destroy();

        System.out.println("program is end");
    }
}
