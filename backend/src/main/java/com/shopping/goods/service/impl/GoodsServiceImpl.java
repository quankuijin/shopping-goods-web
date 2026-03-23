package com.shopping.goods.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.shopping.goods.common.PageResult;
import com.shopping.goods.dto.GoodsDTO;
import com.shopping.goods.dto.GoodsQueryDTO;
import com.shopping.goods.entity.Goods;
import com.shopping.goods.service.GoodsService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoodsServiceImpl implements GoodsService {

    private static final String GOODS_LIST_KEY = "goods:list";
    private static final String GOODS_DETAIL_KEY_PREFIX = "goods:detail:";

    private final RedisTemplate<String, Object> redisTemplate;

    public GoodsServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Goods create(GoodsDTO dto) {
        Goods goods = new Goods();
        goods.setId(IdUtil.simpleUUID());
        goods.setName(dto.getName());
        goods.setCode(dto.getCode());
        goods.setPrice(dto.getPrice());
        goods.setDescription(dto.getDescription());
        goods.setImageUrl(dto.getImageUrl());
        goods.setStatus(dto.getStatus() != null ? dto.getStatus() : 0);
        goods.setUnit(dto.getUnit());
        goods.setCreateTime(LocalDateTime.now());
        goods.setUpdateTime(LocalDateTime.now());
        goods.setCreatorId(dto.getCreatorId() != null ? dto.getCreatorId() : 0L);
        goods.setModifierId(dto.getModifierId() != null ? dto.getModifierId() : 0L);

        redisTemplate.opsForHash().put(GOODS_LIST_KEY, goods.getId(), goods);
        redisTemplate.opsForValue().set(GOODS_DETAIL_KEY_PREFIX + goods.getId(), goods);
        
        return goods;
    }

    @Override
    public Goods update(GoodsDTO dto) {
        if (StrUtil.isEmpty(dto.getId())) {
            throw new RuntimeException("商品ID不能为空");
        }
        
        Object existing = redisTemplate.opsForHash().get(GOODS_LIST_KEY, dto.getId());
        if (existing == null) {
            throw new RuntimeException("商品不存在");
        }
        
        Goods goods = (Goods) existing;
        if (dto.getName() != null) goods.setName(dto.getName());
        if (dto.getCode() != null) goods.setCode(dto.getCode());
        if (dto.getPrice() != null) goods.setPrice(dto.getPrice());
        if (dto.getDescription() != null) goods.setDescription(dto.getDescription());
        if (dto.getImageUrl() != null) goods.setImageUrl(dto.getImageUrl());
        if (dto.getStatus() != null) goods.setStatus(dto.getStatus());
        if (dto.getUnit() != null) goods.setUnit(dto.getUnit());
        goods.setUpdateTime(LocalDateTime.now());
        if (dto.getModifierId() != null) goods.setModifierId(dto.getModifierId());

        redisTemplate.opsForHash().put(GOODS_LIST_KEY, goods.getId(), goods);
        redisTemplate.opsForValue().set(GOODS_DETAIL_KEY_PREFIX + goods.getId(), goods);
        
        return goods;
    }

    @Override
    public void delete(String id) {
        if (StrUtil.isEmpty(id)) {
            throw new RuntimeException("商品ID不能为空");
        }
        
        redisTemplate.opsForHash().delete(GOODS_LIST_KEY, id);
        redisTemplate.delete(GOODS_DETAIL_KEY_PREFIX + id);
    }

    @Override
    public Goods getById(String id) {
        if (StrUtil.isEmpty(id)) {
            return null;
        }
        
        Object obj = redisTemplate.opsForHash().get(GOODS_LIST_KEY, id);
        return obj != null ? (Goods) obj : null;
    }

    @Override
    public PageResult<Goods> list(GoodsQueryDTO query) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(GOODS_LIST_KEY);
        
        List<Goods> allGoods = entries.values().stream()
                .map(obj -> (Goods) obj)
                .collect(Collectors.toList());

        if (StrUtil.isNotEmpty(query.getName())) {
            allGoods = allGoods.stream()
                    .filter(g -> g.getName() != null && g.getName().contains(query.getName()))
                    .collect(Collectors.toList());
        }
        
        if (StrUtil.isNotEmpty(query.getCode())) {
            allGoods = allGoods.stream()
                    .filter(g -> g.getCode() != null && g.getCode().contains(query.getCode()))
                    .collect(Collectors.toList());
        }
        
        if (query.getStatus() != null) {
            allGoods = allGoods.stream()
                    .filter(g -> query.getStatus().equals(g.getStatus()))
                    .collect(Collectors.toList());
        }

        allGoods.sort((a, b) -> {
            if (a.getCreateTime() == null) return 1;
            if (b.getCreateTime() == null) return -1;
            return b.getCreateTime().compareTo(a.getCreateTime());
        });

        long total = allGoods.size();
        int pageNum = query.getPageNum() != null ? query.getPageNum() : 1;
        int pageSize = query.getPageSize() != null ? query.getPageSize() : 10;
        
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, allGoods.size());
        
        List<Goods> pageData = fromIndex < allGoods.size() 
                ? allGoods.subList(fromIndex, toIndex) 
                : new ArrayList<>();

        return PageResult.of(pageData, total, pageNum, pageSize);
    }
}
