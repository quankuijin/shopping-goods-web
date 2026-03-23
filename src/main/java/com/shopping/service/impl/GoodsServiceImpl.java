package com.shopping.service.impl;

import com.shopping.entity.Goods;
import com.shopping.repository.GoodsRepository;
import com.shopping.service.GoodsService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsRepository goodsRepository;

    @Override
    public Goods save(Goods goods) {
        if (!StringUtils.hasText(goods.getId())) {
            goods.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        goods.setCreateTime(LocalDateTime.now());
        goods.setUpdateTime(LocalDateTime.now());
        if (goods.getStatus() == null) {
            goods.setStatus(1);
        }
        return goodsRepository.save(goods);
    }

    @Override
    public Goods findById(String id) {
        return goodsRepository.findById(id);
    }

    @Override
    public List<Goods> findAll() {
        return goodsRepository.findAll();
    }

    @Override
    public void deleteById(String id) {
        goodsRepository.deleteById(id);
    }

    @Override
    public Goods update(Goods goods) {
        Goods existingGoods = goodsRepository.findById(goods.getId());
        if (existingGoods != null) {
            goods.setCreateTime(existingGoods.getCreateTime());
            goods.setCreateBy(existingGoods.getCreateBy());
            goods.setUpdateTime(LocalDateTime.now());
            return goodsRepository.save(goods);
        }
        return null;
    }

    @Override
    public Goods findByCode(String code) {
        return goodsRepository.findByCode(code);
    }
}
