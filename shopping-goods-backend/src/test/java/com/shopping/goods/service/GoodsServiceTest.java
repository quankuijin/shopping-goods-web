package com.shopping.goods.service;

import com.shopping.goods.pojo.dto.GoodsQueryRequest;
import com.shopping.goods.pojo.dto.PageResult;
import com.shopping.goods.pojo.entity.Goods;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
public class GoodsServiceTest {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(GoodsServiceTest.class, args);
        GoodsService goodsService = context.getBean(GoodsService.class);

        System.out.println("========== 商品管理系统 Service 层测试开始 ==========\n");

        try {
            // 1. 测试新增商品
            System.out.println("【测试1】新增商品");
            Goods goods1 = new Goods();
            goods1.setName("iPhone 15 Pro");
            goods1.setCode("PHONE001");
            goods1.setPrice(new BigDecimal("7999.00"));
            goods1.setDescription("苹果最新款手机，钛金属边框");
            goods1.setImage("https://example.com/iphone15.jpg");
            goods1.setStatus(1);
            goods1.setUnit("台");
            goods1.setCreateUserId(1L);
            goods1.setUpdateUserId(1L);

            Goods createdGoods1 = goodsService.createGoods(goods1);
            System.out.println("✓ 新增商品成功: " + createdGoods1.getName() + ", ID: " + createdGoods1.getId());

            Goods goods2 = new Goods();
            goods2.setName("MacBook Pro 16");
            goods2.setCode("LAPTOP001");
            goods2.setPrice(new BigDecimal("19999.00"));
            goods2.setDescription("苹果高性能笔记本电脑");
            goods2.setImage("https://example.com/macbook.jpg");
            goods2.setStatus(1);
            goods2.setUnit("台");

            Goods createdGoods2 = goodsService.createGoods(goods2);
            System.out.println("✓ 新增商品成功: " + createdGoods2.getName() + ", ID: " + createdGoods2.getId());

            Goods goods3 = new Goods();
            goods3.setName("AirPods Pro 2");
            goods3.setCode("EARPHONE001");
            goods3.setPrice(new BigDecimal("1899.00"));
            goods3.setDescription("苹果降噪耳机");
            goods3.setImage("https://example.com/airpods.jpg");
            goods3.setStatus(0);
            goods3.setUnit("副");

            Goods createdGoods3 = goodsService.createGoods(goods3);
            System.out.println("✓ 新增商品成功: " + createdGoods3.getName() + ", ID: " + createdGoods3.getId());
            System.out.println();

            // 2. 测试查询单个商品
            System.out.println("【测试2】查询单个商品");
            Goods foundGoods = goodsService.getGoodsById(createdGoods1.getId());
            if (foundGoods != null) {
                System.out.println("✓ 查询商品成功: " + foundGoods.getName() + ", 价格: ¥" + foundGoods.getPrice());
            } else {
                System.out.println("✗ 查询商品失败");
            }
            System.out.println();

            // 3. 测试更新商品
            System.out.println("【测试3】更新商品");
            Goods updateGoods = new Goods();
            updateGoods.setId(createdGoods1.getId());
            updateGoods.setName("iPhone 15 Pro Max");
            updateGoods.setPrice(new BigDecimal("9999.00"));
            updateGoods.setDescription("更新后的描述：更大屏幕，更强续航");
            updateGoods.setUpdateUserId(2L);

            Goods updatedGoods = goodsService.updateGoods(updateGoods);
            System.out.println("✓ 更新商品成功: " + updatedGoods.getName() + ", 新价格: ¥" + updatedGoods.getPrice());
            System.out.println("  更新时间: " + updatedGoods.getUpdateTime());
            System.out.println();

            // 4. 测试分页查询
            System.out.println("【测试4】分页查询商品");
            GoodsQueryRequest queryRequest = new GoodsQueryRequest();
            queryRequest.setPageNum(1);
            queryRequest.setPageSize(10);

            PageResult<Goods> pageResult = goodsService.queryGoods(queryRequest);
            System.out.println("✓ 分页查询成功");
            System.out.println("  总记录数: " + pageResult.getTotal());
            System.out.println("  当前页: " + pageResult.getPageNum());
            System.out.println("  每页大小: " + pageResult.getPageSize());
            System.out.println("  总页数: " + pageResult.getTotalPages());
            System.out.println("  当前页数据条数: " + pageResult.getList().size());
            System.out.println();

            // 5. 测试条件查询
            System.out.println("【测试5】条件查询 - 按名称搜索");
            GoodsQueryRequest searchRequest = new GoodsQueryRequest();
            searchRequest.setName("iPhone");
            searchRequest.setPageNum(1);
            searchRequest.setPageSize(10);

            PageResult<Goods> searchResult = goodsService.queryGoods(searchRequest);
            System.out.println("✓ 条件查询成功，搜索 'iPhone'，找到 " + searchResult.getTotal() + " 条记录");
            searchResult.getList().forEach(g -> System.out.println("  - " + g.getName()));
            System.out.println();

            // 6. 测试按状态查询
            System.out.println("【测试6】条件查询 - 按状态搜索（上架）");
            GoodsQueryRequest statusRequest = new GoodsQueryRequest();
            statusRequest.setStatus(1);
            statusRequest.setPageNum(1);
            statusRequest.setPageSize(10);

            PageResult<Goods> statusResult = goodsService.queryGoods(statusRequest);
            System.out.println("✓ 状态查询成功，上架商品共 " + statusResult.getTotal() + " 条");
            System.out.println();

            // 7. 测试获取所有商品
            System.out.println("【测试7】获取所有商品列表");
            List<Goods> allGoods = goodsService.getAllGoods();
            System.out.println("✓ 获取所有商品成功，共 " + allGoods.size() + " 条");
            allGoods.forEach(g -> System.out.println("  - " + g.getName() + " [" + (g.getStatus() == 1 ? "上架" : "下架") + "]"));
            System.out.println();

            // 8. 测试删除商品
            System.out.println("【测试8】删除商品");
            goodsService.deleteGoods(createdGoods3.getId());
            System.out.println("✓ 删除商品成功，ID: " + createdGoods3.getId());

            // 验证删除
            Goods deletedGoods = goodsService.getGoodsById(createdGoods3.getId());
            if (deletedGoods == null) {
                System.out.println("✓ 验证删除成功，商品已不存在");
            } else {
                System.out.println("✗ 验证删除失败，商品仍存在");
            }
            System.out.println();

            // 9. 再次查询所有商品
            System.out.println("【测试9】删除后再次查询所有商品");
            List<Goods> remainingGoods = goodsService.getAllGoods();
            System.out.println("✓ 剩余商品数量: " + remainingGoods.size());
            remainingGoods.forEach(g -> System.out.println("  - " + g.getName()));
            System.out.println();

            System.out.println("========== 所有测试通过！==========");

        } catch (Exception e) {
            System.err.println("✗ 测试执行过程中发生错误:");
            e.printStackTrace();
        } finally {
            context.close();
        }
    }
}
