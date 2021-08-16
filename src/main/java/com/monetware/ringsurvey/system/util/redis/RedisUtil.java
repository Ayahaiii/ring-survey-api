package com.monetware.ringsurvey.system.util.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Cookie
 * @Date: 2019/1/17 12:53
 * @Description: redis共用方法
 */
@SuppressWarnings("unchecked")
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Resource(name = "authRedisTemplate")
    private RedisTemplate<String, Object> authRedisTemplate;

    // =============================common============================

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void remove(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object authGet(String key) {
        return key == null ? null : authRedisTemplate.opsForValue().get(key);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean authHasKey(String key) {
        try {
            return authRedisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void authRemove(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                authRedisTemplate.delete(key[0]);
            } else {
                authRedisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }
    // ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.MILLISECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @return true成功 false 失败
     */
    public boolean setLpush(String key, Object value) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 取出数据
     *
     * @param key
     * @return
     */
    public List<Object> getLpopAll(String key) {
        return key == null ? null : redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 模糊获取keys
     *
     * @param prex
     * @return
     */
    public Set<String> getKeysByPrex(String prex) {
        if (StringUtils.isEmpty(prex)) {
            return null;
        }
        return redisTemplate.keys(prex);
    }

    /**
     * 批量删除
     *
     * @param prex
     */
    public void deleteByPrex(String prex) {
        Set<String> keys = redisTemplate.keys(prex);
        if (!CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * hash set
     *
     * @return
     */
    public boolean hset(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * hash get
     *
     * @return
     */
    public Object hget(String key, String field) {
        if (hasKey(key)) {
            return field == null ? null : redisTemplate.opsForHash().get(key, field);
        } else {
            return null;
        }
    }

    /**
     * 是否存在
     *
     * @param key
     * @param field
     * @return
     */
    public boolean hexist(String key, String field) {
        if (hasKey(key)) {
            return redisTemplate.opsForHash().hasKey(key, field);
        }
        return false;
    }

    /**
     * hash del
     *
     * @param key
     * @param fields
     */
    public void hdel(String key, String... fields) {
        if (fields != null && fields.length > 0) {
            if (fields.length == 1) {
                redisTemplate.opsForHash().delete(key, fields[0]);
            } else {
                redisTemplate.opsForHash().delete(key, CollectionUtils.arrayToList(key));
            }
        }
    }

}
