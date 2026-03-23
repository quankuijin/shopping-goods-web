package com.shopping.goods.service;

import com.shopping.goods.pojo.dto.GoodsQueryRequest;
import com.shopping.goods.pojo.dto.PageResult;
import com.shopping.goods.pojo.entity.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String GOODS_KEY_PREFIX = "goods:";
    private static final String GOODS_ID_KEY = "goods:id";
    private static final String GOODS_SET_KEY = "goods:set";

    public Goods createGoods(Goods goods) {
        // 生成ID
        Long id = redisTemplate.opsForValue().increment(GOODS_ID_KEY);
        goods.setId(id);

        // 设置时间
        LocalDateTime now = LocalDateTime.now();
        goods.setCreateTime(now);
        goods.setUpdateTime(now);

        // 设置默认用户ID
        if (goods.getCreateUserId() == null) {
            goods.setCreateUserId(0L);
        }
        if (goods.getUpdateUserId() == null) {
            goods.setUpdateUserId(0L);
        }

        // 保存到Redis
        String key = GOODS_KEY_PREFIX + id;
        redisTemplate.opsForValue().set(key, goods);

        // 添加到Set集合
        redisTemplate.opsForSet().add(GOODS_SET_KEY, id);

        return goods;
    }

    public Goods updateGoods(Goods goods) {
        if (goods.getId() == null) {
            throw new RuntimeException("商品ID不能为空");
        }

        String key = GOODS_KEY_PREFIX + goods.getId();
        Goods existingGoods = (Goods) redisTemplate.opsForValue().get(key);
        if (existingGoods == null) {
            throw new RuntimeException("商品不存在");
        }

        // 更新字段
        if (goods.getName() != null) {
            existingGoods.setName(goods.getName());
        }
        if (goods.getCode() != null) {
            existingGoods.setCode(goods.getCode());
        }
        if (goods.getPrice() != null) {
            existingGoods.setPrice(goods.getPrice());
        }
        if (goods.getDescription() != null) {
            existingGoods.setDescription(goods.getDescription());
        }
        if (goods.getImage() != null) {
            existingGoods.setImage(goods.getImage());
        }
        if (goods.getStatus() != null) {
            existingGoods.setStatus(goods.getStatus());
        }
        if (goods.getUnit() != null) {
            existingGoods.setUnit(goods.getUnit());
        }
        if (goods.getUpdateUserId() != null) {
            existingGoods.setUpdateUserId(goods.getUpdateUserId());
        }

        existingGoods.setUpdateTime(LocalDateTime.now());

        // 保存到Redis
        redisTemplate.opsForValue().set(key, existingGoods);

        return existingGoods;
    }

    public void deleteGoods(Long id) {
        String key = GOODS_KEY_PREFIX + id;
        Goods goods = (Goods) redisTemplate.opsForValue().get(key);
        if (goods == null) {
            throw new RuntimeException("商品不存在");
        }

        // 删除商品
        redisTemplate.delete(key);

        // 从Set集合中移除
        redisTemplate.opsForSet().remove(GOODS_SET_KEY, id);
    }

    public Goods getGoodsById(Long id) {
        String key = GOODS_KEY_PREFIX + id;
        return (Goods) redisTemplate.opsForValue().get(key);
    }

    public PageResult<Goods> queryGoods(GoodsQueryRequest request) {
        // 获取所有商品ID
        Set<Object> idSet = redisTemplate.opsForSet().members(GOODS_SET_KEY);
        if (idSet == null || idSet.isEmpty()) {
            return PageResult.of(new ArrayList<>(), 0L, request.getPageNum(), request.getPageSize());
        }

        // 获取所有商品
        List<Goods> allGoods = new ArrayList<>();
        for (Object idObj : idSet) {
            Long id = Long.valueOf(idObj.toString());
            String key = GOODS_KEY_PREFIX + id;
            Goods goods = (Goods) redisTemplate.opsForValue().get(key);
            if (goods != null) {
                allGoods.add(goods);
            }
        }

        // 过滤
        List<Goods> filteredGoods = allGoods.stream()
                .filter(goods -> {
                    if (request.getName() != null && !request.getName().isEmpty()) {
                        if (!goods.getName().contains(request.getName())) {
                            return false;
                        }
                    }
                    if (request.getCode() != null && !request.getCode().isEmpty()) {
                        if (!goods.getCode().contains(request.getCode())) {
                            return false;
                        }
                    }
                    if (request.getStatus() != null) {
                        if (!goods.getStatus().equals(request.getStatus())) {
                            return false;
                        }
                    }
                    return true;
                })
                .sorted(Comparator.comparing(Goods::getCreateTime).reversed())
                .collect(Collectors.toList());

        // 分页
        long total = filteredGoods.size();
        int start = (request.getPageNum() - 1) * request.getPageSize();
        int end = Math.min(start + request.getPageSize(), filteredGoods.size());

        List<Goods> pageList = start < filteredGoods.size()
                ? filteredGoods.subList(start, end)
                : new ArrayList<>();

        return PageResult.of(pageList, total, request.getPageNum(), request.getPageSize());
    }

    public List<Goods> getAllGoods() {
        Set<Object> idSet = redisTemplate.opsForSet().members(GOODS_SET_KEY);
        if (idSet == null || idSet.isEmpty()) {
            return new ArrayList<>();
        }

        List<Goods> allGoods = new ArrayList<>();
        for (Object idObj : idSet) {
            Long id = Long.valueOf(idObj.toString());
            String key = GOODS_KEY_PREFIX + id;
            Goods goods = (Goods) redisTemplate.opsForValue().get(key);
            if (goods != null) {
                allGoods.add(goods);
            }
        }

        return allGoods.stream()
                .sorted(Comparator.comparing(Goods::getCreateTime).reversed())
                .collect(Collectors.toList());
    }
}
