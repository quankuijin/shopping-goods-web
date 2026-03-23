package com.shopping.goods.service;

import com.shopping.goods.common.PageResult;
import com.shopping.goods.dto.GoodsDTO;
import com.shopping.goods.dto.GoodsQueryDTO;
import com.shopping.goods.entity.Goods;

public interface GoodsService {
    Goods create(GoodsDTO dto);
    Goods update(GoodsDTO dto);
    void delete(String id);
    Goods getById(String id);
    PageResult<Goods> list(GoodsQueryDTO query);
}
