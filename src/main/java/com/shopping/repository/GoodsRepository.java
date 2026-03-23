package com.shopping.repository;

import com.shopping.entity.Goods;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public class GoodsRepository {
    private static final String KEY_PREFIX = "goods:";

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public Goods save(Goods goods) {
        String key = KEY_PREFIX + goods.getId();
        redisTemplate.opsForValue().set(key, goods);
        return goods;
    }

    public Goods findById(String id) {
        String key = KEY_PREFIX + id;
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj instanceof Goods) {
            return (Goods) obj;
        }
        return null;
    }

    public List<Goods> findAll() {
        Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");
        List<Goods> goodsList = new ArrayList<>();
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                Object obj = redisTemplate.opsForValue().get(key);
                if (obj instanceof Goods) {
                    goodsList.add((Goods) obj);
                }
            }
        }
        return goodsList;
    }

    public void deleteById(String id) {
        String key = KEY_PREFIX + id;
        redisTemplate.delete(key);
    }

    public boolean existsById(String id) {
        String key = KEY_PREFIX + id;
        Boolean exists = redisTemplate.hasKey(key);
        return exists != null && exists;
    }

    public Goods findByCode(String code) {
        Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                Object obj = redisTemplate.opsForValue().get(key);
                if (obj instanceof Goods) {
                    Goods goods = (Goods) obj;
                    if (code.equals(goods.getCode())) {
                        return goods;
                    }
                }
            }
        }
        return null;
    }
}
