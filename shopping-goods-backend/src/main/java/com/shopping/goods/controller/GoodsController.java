package com.shopping.goods.controller;

import com.shopping.goods.pojo.dto.GoodsQueryRequest;
import com.shopping.goods.pojo.dto.PageResult;
import com.shopping.goods.pojo.dto.Result;
import com.shopping.goods.pojo.entity.Goods;
import com.shopping.goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @PostMapping
    public Result<Goods> createGoods(@RequestBody Goods goods) {
        Goods createdGoods = goodsService.createGoods(goods);
        return Result.success(createdGoods);
    }

    @PutMapping("/{id}")
    public Result<Goods> updateGoods(@PathVariable("id") Long id, @RequestBody Goods goods) {
        goods.setId(id);
        Goods updatedGoods = goodsService.updateGoods(goods);
        return Result.success(updatedGoods);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteGoods(@PathVariable("id") Long id) {
        goodsService.deleteGoods(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Goods> getGoodsById(@PathVariable("id") Long id) {
        Goods goods = goodsService.getGoodsById(id);
        if (goods == null) {
            return Result.error("商品不存在");
        }
        return Result.success(goods);
    }

    @GetMapping("/list")
    public Result<PageResult<Goods>> queryGoods(GoodsQueryRequest request) {
        PageResult<Goods> result = goodsService.queryGoods(request);
        return Result.success(result);
    }
}
