package com.mmall.util;

import com.mmall.common.RedisShardedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ShardedJedis;

/**
 * Created by guolin
 * ShardedJedis用到的api
 */
@Slf4j
public class RedisShardedPoolUtil {

    /**
     * 封装redis的set方法
     * @param key
     * @param value
     * @return
     */
    public static String set(String key,String value){
        ShardedJedis ShardedJedis = null;
        String result = null;

        try {
            // 从连接池中拿一个ShardedJedis
            ShardedJedis = RedisShardedPool.getJedis();

            // 插入返回的结果
            result = ShardedJedis.set(key,value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error",key,value,e);

            // 放入损坏池
            RedisShardedPool.returnBrokenResource(ShardedJedis);
            return result;
        }

        // 放入正常池
        RedisShardedPool.returnResource(ShardedJedis);
        return result;
    }

    /**
     * 封装redis的get方法
     * @param key
     * @return
     */
    public static String get(String key){
        ShardedJedis ShardedJedis = null;
        String result = null;
        try {
            ShardedJedis = RedisShardedPool.getJedis();
            result = ShardedJedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error",key,e);
            RedisShardedPool.returnBrokenResource(ShardedJedis);
            return result;
        }
        RedisShardedPool.returnResource(ShardedJedis);
        return result;
    }

    /**
     * 有过期时间的set方法
     * @param key
     * @param value
     * @param exTime exTime的单位是秒
     * @return
     */
    public static String setEx(String key,String value,int exTime){
        ShardedJedis ShardedJedis = null;
        String result = null;
        try {
            ShardedJedis = RedisShardedPool.getJedis();
            result = ShardedJedis.setex(key,exTime,value);
        } catch (Exception e) {
            log.error("setex key:{} value:{} error",key,value,e);
            RedisShardedPool.returnBrokenResource(ShardedJedis);
            return result;
        }
        RedisShardedPool.returnResource(ShardedJedis);
        return result;
    }

    /**
     * 设置key的有效期，单位是秒
     * @param key
     * @param exTime
     * @return 设置成功返回1，否则返回0
     */
    public static Long expire(String key,int exTime){
        ShardedJedis ShardedJedis = null;
        Long result = null;
        try {
            ShardedJedis = RedisShardedPool.getJedis();
            result = ShardedJedis.expire(key,exTime);
        } catch (Exception e) {
            log.error("expire key:{} error",key,e);
            RedisShardedPool.returnBrokenResource(ShardedJedis);
            return result;
        }
        RedisShardedPool.returnResource(ShardedJedis);
        return result;
    }

    /**
     * 删除数据
     * @param key
     * @return
     */
    public static Long del(String key){
        ShardedJedis ShardedJedis = null;
        Long result = null;
        try {
            ShardedJedis = RedisShardedPool.getJedis();
            result = ShardedJedis.del(key);
        } catch (Exception e) {
            log.error("del key:{} error",key,e);
            RedisShardedPool.returnBrokenResource(ShardedJedis);
            return result;
        }
        RedisShardedPool.returnResource(ShardedJedis);
        return result;
    }

    /**
     * 测试函数
     * @param args
     */
    public static void main(String[] args) {
        ShardedJedis ShardedJedis = RedisShardedPool.getJedis();

        RedisShardedPoolUtil.set("keyTest","value");

        String value = RedisShardedPoolUtil.get("keyTest");

        RedisShardedPoolUtil.setEx("keyex","valueex",60*10);

        RedisShardedPoolUtil.expire("keyTest",60*20);

        RedisShardedPoolUtil.del("keyTest");

        System.out.println("end");
    }
}
