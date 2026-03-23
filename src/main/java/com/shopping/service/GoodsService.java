package com.shopping.service;

import com.shopping.entity.Goods;

import java.util.List;

public interface GoodsService {
    Goods save(Goods goods);
    Goods findById(String id);
    List<Goods> findAll();
    void deleteById(String id);
    Goods update(Goods goods);
    Goods findByCode(String code);
}
