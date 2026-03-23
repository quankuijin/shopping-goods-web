package com.shopping.service;

import com.shopping.ShoppingGoodsApplication;
import com.shopping.entity.Goods;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class GoodsServiceTest {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ShoppingGoodsApplication.class, args);
        GoodsService goodsService = context.getBean(GoodsService.class);
        LoginService loginService = context.getBean(LoginService.class);

        System.out.println("=== 开始测试Service层接口 ===");

        // 测试登录功能
        System.out.println("\n1. 测试登录功能:");
        boolean loginSuccess = loginService.login("admin", "123");
        System.out.println("   用户名=admin, 密码=123, 登录结果: " + (loginSuccess ? "成功" : "失败"));

        boolean loginFail = loginService.login("admin", "wrong");
        System.out.println("   用户名=admin, 密码=wrong, 登录结果: " + (loginFail ? "成功" : "失败"));

        // 测试新增商品
        System.out.println("\n2. 测试新增商品:");
        Goods goods1 = new Goods();
        String testCode = "TEST" + UUID.randomUUID().toString().substring(0, 6);
        goods1.setCode(testCode);
        goods1.setName("测试商品1");
        goods1.setPrice(new BigDecimal("99.99"));
        goods1.setDescription("这是一个测试商品");
        goods1.setUnit("件");
        goods1.setImage("http://example.com/image1.jpg");
        goods1.setStatus(1);
        Goods savedGoods1 = goodsService.save(goods1);
        System.out.println("   新增商品结果: ID=" + savedGoods1.getId() + ", 名称=" + savedGoods1.getName());

        Goods goods2 = new Goods();
        goods2.setCode("TEST" + UUID.randomUUID().toString().substring(0, 6));
        goods2.setName("测试商品2");
        goods2.setPrice(new BigDecimal("199.99"));
        goods2.setDescription("这是第二个测试商品");
        goods2.setUnit("个");
        goods2.setImage("http://example.com/image2.jpg");
        goods2.setStatus(0);
        Goods savedGoods2 = goodsService.save(goods2);
        System.out.println("   新增商品结果: ID=" + savedGoods2.getId() + ", 名称=" + savedGoods2.getName());

        // 测试根据ID查询商品
        System.out.println("\n3. 测试根据ID查询商品:");
        Goods foundGoods = goodsService.findById(savedGoods1.getId());
        System.out.println("   查询结果: " + (foundGoods != null ? "找到商品, 名称=" + foundGoods.getName() : "未找到商品"));

        // 测试查询所有商品
        System.out.println("\n4. 测试查询所有商品:");
        List<Goods> goodsList = goodsService.findAll();
        System.out.println("   商品总数: " + goodsList.size());
        for (Goods g : goodsList) {
            System.out.println("     - " + g.getName() + " (" + g.getCode() + ")");
        }

        // 测试根据编码查询商品
        System.out.println("\n5. 测试根据编码查询商品:");
        Goods goodsByCode = goodsService.findByCode(savedGoods1.getCode());
        System.out.println("   查询结果: " + (goodsByCode != null ? "找到商品, 编码=" + goodsByCode.getCode() : "未找到商品"));

        // 测试更新商品
        System.out.println("\n6. 测试更新商品:");
        savedGoods1.setName("测试商品1-更新后");
        savedGoods1.setPrice(new BigDecimal("88.88"));
        Goods updatedGoods = goodsService.update(savedGoods1);
        System.out.println("   更新结果: " + (updatedGoods != null ? "更新成功, 新名称=" + updatedGoods.getName() : "更新失败"));

        // 测试删除商品
        System.out.println("\n7. 测试删除商品:");
        goodsService.deleteById(savedGoods1.getId());
        goodsService.deleteById(savedGoods2.getId());
        System.out.println("   删除商品1和商品2");

        List<Goods> afterDeleteList = goodsService.findAll();
        System.out.println("   删除后商品总数: " + afterDeleteList.size());

        System.out.println("\n=== 测试完成 ===");
        System.exit(0);
    }
}
