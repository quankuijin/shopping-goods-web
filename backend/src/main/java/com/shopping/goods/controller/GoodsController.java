package com.shopping.goods.controller;

import com.shopping.goods.common.PageResult;
import com.shopping.goods.common.Result;
import com.shopping.goods.dto.GoodsDTO;
import com.shopping.goods.dto.GoodsQueryDTO;
import com.shopping.goods.entity.Goods;
import com.shopping.goods.service.GoodsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    private final GoodsService goodsService;

    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @PostMapping
    public Result<Goods> create(@RequestBody GoodsDTO dto) {
        try {
            Goods goods = goodsService.create(dto);
            return Result.success(goods);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Goods> update(@PathVariable String id, @RequestBody GoodsDTO dto) {
        try {
            dto.setId(id);
            Goods goods = goodsService.update(dto);
            return Result.success(goods);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        try {
            goodsService.delete(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Goods> getById(@PathVariable String id) {
        Goods goods = goodsService.getById(id);
        if (goods == null) {
            return Result.error(404, "商品不存在");
        }
        return Result.success(goods);
    }

    @GetMapping
    public Result<PageResult<Goods>> list(GoodsQueryDTO query) {
        PageResult<Goods> result = goodsService.list(query);
        return Result.success(result);
    }

    @PutMapping("/{id}/status")
    public Result<Goods> updateStatus(@PathVariable String id, @RequestParam Integer status) {
        try {
            GoodsDTO dto = new GoodsDTO();
            dto.setId(id);
            dto.setStatus(status);
            Goods goods = goodsService.update(dto);
            return Result.success(goods);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
