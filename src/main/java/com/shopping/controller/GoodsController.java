package com.shopping.controller;

import com.shopping.common.Result;
import com.shopping.entity.Goods;
import com.shopping.service.GoodsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    @PostMapping
    public Result<Goods> create(@RequestBody Goods goods) {
        Goods existingGoods = goodsService.findByCode(goods.getCode());
        if (existingGoods != null) {
            return Result.error("商品编码已存在");
        }
        Goods savedGoods = goodsService.save(goods);
        return Result.success(savedGoods);
    }

    @PutMapping("/{id}")
    public Result<Goods> update(@PathVariable String id, @RequestBody Goods goods) {
        goods.setId(id);
        Goods updatedGoods = goodsService.update(goods);
        if (updatedGoods != null) {
            return Result.success(updatedGoods);
        } else {
            return Result.error("商品不存在");
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        goodsService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Goods> findById(@PathVariable String id) {
        Goods goods = goodsService.findById(id);
        if (goods != null) {
            return Result.success(goods);
        } else {
            return Result.error("商品不存在");
        }
    }

    @GetMapping
    public Result<List<Goods>> findAll() {
        List<Goods> goodsList = goodsService.findAll();
        return Result.success(goodsList);
    }
}
