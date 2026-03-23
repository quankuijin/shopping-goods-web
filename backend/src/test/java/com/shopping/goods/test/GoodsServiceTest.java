package com.shopping.goods.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shopping.goods.common.PageResult;
import com.shopping.goods.dto.GoodsDTO;
import com.shopping.goods.dto.GoodsQueryDTO;
import com.shopping.goods.entity.Goods;
import com.shopping.goods.service.GoodsService;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Scanner;

public class GoodsServiceTest {

    private static GoodsService goodsService;
    private static RedisTemplate<String, Object> redisTemplate;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("       商品管理系统 Service 层测试");
        System.out.println("========================================");
        System.out.println();

        try {
            initRedis();
            initService();
            
            System.out.println("Redis连接成功！");
            System.out.println();

            while (true) {
                printMenu();
                int choice = readInt("请选择操作：");
                
                switch (choice) {
                    case 1:
                        testCreate();
                        break;
                    case 2:
                        testUpdate();
                        break;
                    case 3:
                        testDelete();
                        break;
                    case 4:
                        testGetById();
                        break;
                    case 5:
                        testList();
                        break;
                    case 6:
                        testUpdateStatus();
                        break;
                    case 0:
                        System.out.println("退出测试程序");
                        return;
                    default:
                        System.out.println("无效的选择，请重新输入");
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.err.println("测试失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initRedis() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("127.0.0.1");
        config.setPort(6379);
        config.setPassword("foobared");
        
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.afterPropertiesSet();
        
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, 
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
        
        redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jsonSerializer);
        redisTemplate.setHashValueSerializer(jsonSerializer);
        redisTemplate.afterPropertiesSet();
    }

    private static void initService() {
        goodsService = new TestGoodsServiceImpl(redisTemplate);
    }

    private static void printMenu() {
        System.out.println("-------------- 测试菜单 --------------");
        System.out.println("1. 新增商品");
        System.out.println("2. 修改商品");
        System.out.println("3. 删除商品");
        System.out.println("4. 根据ID查询商品");
        System.out.println("5. 分页查询商品列表");
        System.out.println("6. 更新商品状态");
        System.out.println("0. 退出");
        System.out.println("------------------------------------");
    }

    private static void testCreate() {
        System.out.println("\n=== 测试新增商品 ===");
        GoodsDTO dto = new GoodsDTO();
        dto.setName(readString("请输入商品名称："));
        dto.setCode(readString("请输入商品编码："));
        dto.setPrice(readDouble("请输入商品价格："));
        dto.setUnit(readString("请输入计量单位："));
        dto.setDescription(readString("请输入商品说明："));
        dto.setImageUrl(readString("请输入图片链接（可选，直接回车跳过）："));
        dto.setStatus(readInt("请输入状态（0-下架，1-上架）："));
        dto.setCreatorId(0L);
        dto.setModifierId(0L);

        try {
            Goods goods = goodsService.create(dto);
            System.out.println("新增成功！商品信息：");
            printGoods(goods);
        } catch (Exception e) {
            System.err.println("新增失败：" + e.getMessage());
        }
    }

    private static void testUpdate() {
        System.out.println("\n=== 测试修改商品 ===");
        String id = readString("请输入要修改的商品ID：");
        
        Goods existing = goodsService.getById(id);
        if (existing == null) {
            System.out.println("商品不存在！");
            return;
        }
        
        System.out.println("当前商品信息：");
        printGoods(existing);
        System.out.println();
        
        GoodsDTO dto = new GoodsDTO();
        dto.setId(id);
        
        String name = readString("请输入商品名称（直接回车保持不变）：");
        dto.setName(name.isEmpty() ? existing.getName() : name);
        
        String code = readString("请输入商品编码（直接回车保持不变）：");
        dto.setCode(code.isEmpty() ? existing.getCode() : code);
        
        String priceStr = readString("请输入商品价格（直接回车保持不变）：");
        dto.setPrice(priceStr.isEmpty() ? existing.getPrice() : Double.parseDouble(priceStr));
        
        String unit = readString("请输入计量单位（直接回车保持不变）：");
        dto.setUnit(unit.isEmpty() ? existing.getUnit() : unit);
        
        String description = readString("请输入商品说明（直接回车保持不变）：");
        dto.setDescription(description.isEmpty() ? existing.getDescription() : description);
        
        String imageUrl = readString("请输入图片链接（直接回车保持不变）：");
        dto.setImageUrl(imageUrl.isEmpty() ? existing.getImageUrl() : imageUrl);
        
        String statusStr = readString("请输入状态（直接回车保持不变）：");
        dto.setStatus(statusStr.isEmpty() ? existing.getStatus() : Integer.parseInt(statusStr));
        
        dto.setModifierId(0L);

        try {
            Goods goods = goodsService.update(dto);
            System.out.println("修改成功！商品信息：");
            printGoods(goods);
        } catch (Exception e) {
            System.err.println("修改失败：" + e.getMessage());
        }
    }

    private static void testDelete() {
        System.out.println("\n=== 测试删除商品 ===");
        String id = readString("请输入要删除的商品ID：");
        
        Goods existing = goodsService.getById(id);
        if (existing == null) {
            System.out.println("商品不存在！");
            return;
        }
        
        System.out.println("将要删除的商品：");
        printGoods(existing);
        
        String confirm = readString("确认删除？(y/n)：");
        if ("y".equalsIgnoreCase(confirm)) {
            try {
                goodsService.delete(id);
                System.out.println("删除成功！");
            } catch (Exception e) {
                System.err.println("删除失败：" + e.getMessage());
            }
        } else {
            System.out.println("已取消删除");
        }
    }

    private static void testGetById() {
        System.out.println("\n=== 测试根据ID查询商品 ===");
        String id = readString("请输入商品ID：");
        
        Goods goods = goodsService.getById(id);
        if (goods == null) {
            System.out.println("商品不存在！");
        } else {
            System.out.println("查询结果：");
            printGoods(goods);
        }
    }

    private static void testList() {
        System.out.println("\n=== 测试分页查询商品列表 ===");
        GoodsQueryDTO query = new GoodsQueryDTO();
        
        query.setName(readString("请输入商品名称（模糊查询，直接回车跳过）："));
        query.setCode(readString("请输入商品编码（模糊查询，直接回车跳过）："));
        
        String statusStr = readString("请输入状态（直接回车跳过）：");
        query.setStatus(statusStr.isEmpty() ? null : Integer.parseInt(statusStr));
        
        query.setPageNum(readInt("请输入页码："));
        query.setPageSize(readInt("请输入每页条数："));

        try {
            PageResult<Goods> result = goodsService.list(query);
            System.out.println("\n查询结果：");
            System.out.println("总数：" + result.getTotal());
            System.out.println("当前页：" + result.getPageNum());
            System.out.println("每页条数：" + result.getPageSize());
            System.out.println();
            
            if (result.getList().isEmpty()) {
                System.out.println("暂无数据");
            } else {
                for (Goods goods : result.getList()) {
                    printGoods(goods);
                    System.out.println("--------------------");
                }
            }
        } catch (Exception e) {
            System.err.println("查询失败：" + e.getMessage());
        }
    }

    private static void testUpdateStatus() {
        System.out.println("\n=== 测试更新商品状态 ===");
        String id = readString("请输入商品ID：");
        
        Goods existing = goodsService.getById(id);
        if (existing == null) {
            System.out.println("商品不存在！");
            return;
        }
        
        System.out.println("当前商品信息：");
        printGoods(existing);
        System.out.println("当前状态：" + (existing.getStatus() == 1 ? "上架" : "下架"));
        
        int newStatus = readInt("请输入新状态（0-下架，1-上架）：");
        
        GoodsDTO dto = new GoodsDTO();
        dto.setId(id);
        dto.setStatus(newStatus);
        dto.setModifierId(0L);

        try {
            Goods goods = goodsService.update(dto);
            System.out.println("状态更新成功！");
            printGoods(goods);
        } catch (Exception e) {
            System.err.println("状态更新失败：" + e.getMessage());
        }
    }

    private static void printGoods(Goods goods) {
        System.out.println("ID: " + goods.getId());
        System.out.println("名称: " + goods.getName());
        System.out.println("编码: " + goods.getCode());
        System.out.println("价格: " + goods.getPrice());
        System.out.println("单位: " + goods.getUnit());
        System.out.println("状态: " + (goods.getStatus() == 1 ? "上架" : "下架"));
        System.out.println("说明: " + goods.getDescription());
        System.out.println("图片: " + goods.getImageUrl());
        System.out.println("创建时间: " + goods.getCreateTime());
        System.out.println("修改时间: " + goods.getUpdateTime());
        System.out.println("创建人ID: " + goods.getCreatorId());
        System.out.println("修改人ID: " + goods.getModifierId());
    }

    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("请输入有效的数字！");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("请输入有效的数字！");
            }
        }
    }

    private static class TestGoodsServiceImpl implements GoodsService {
        private static final String GOODS_LIST_KEY = "goods:list";
        private static final String GOODS_DETAIL_KEY_PREFIX = "goods:detail:";
        
        private final RedisTemplate<String, Object> redisTemplate;

        public TestGoodsServiceImpl(RedisTemplate<String, Object> redisTemplate) {
            this.redisTemplate = redisTemplate;
        }

        @Override
        public Goods create(GoodsDTO dto) {
            Goods goods = new Goods();
            goods.setId(java.util.UUID.randomUUID().toString().replace("-", ""));
            goods.setName(dto.getName());
            goods.setCode(dto.getCode());
            goods.setPrice(dto.getPrice());
            goods.setDescription(dto.getDescription());
            goods.setImageUrl(dto.getImageUrl());
            goods.setStatus(dto.getStatus() != null ? dto.getStatus() : 0);
            goods.setUnit(dto.getUnit());
            goods.setCreateTime(java.time.LocalDateTime.now());
            goods.setUpdateTime(java.time.LocalDateTime.now());
            goods.setCreatorId(dto.getCreatorId() != null ? dto.getCreatorId() : 0L);
            goods.setModifierId(dto.getModifierId() != null ? dto.getModifierId() : 0L);

            redisTemplate.opsForHash().put(GOODS_LIST_KEY, goods.getId(), goods);
            redisTemplate.opsForValue().set(GOODS_DETAIL_KEY_PREFIX + goods.getId(), goods);
            
            return goods;
        }

        @Override
        public Goods update(GoodsDTO dto) {
            if (dto.getId() == null || dto.getId().isEmpty()) {
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
            goods.setUpdateTime(java.time.LocalDateTime.now());
            if (dto.getModifierId() != null) goods.setModifierId(dto.getModifierId());

            redisTemplate.opsForHash().put(GOODS_LIST_KEY, goods.getId(), goods);
            redisTemplate.opsForValue().set(GOODS_DETAIL_KEY_PREFIX + goods.getId(), goods);
            
            return goods;
        }

        @Override
        public void delete(String id) {
            if (id == null || id.isEmpty()) {
                throw new RuntimeException("商品ID不能为空");
            }
            
            redisTemplate.opsForHash().delete(GOODS_LIST_KEY, id);
            redisTemplate.delete(GOODS_DETAIL_KEY_PREFIX + id);
        }

        @Override
        public Goods getById(String id) {
            if (id == null || id.isEmpty()) {
                return null;
            }
            
            Object obj = redisTemplate.opsForHash().get(GOODS_LIST_KEY, id);
            return obj != null ? (Goods) obj : null;
        }

        @Override
        public PageResult<Goods> list(GoodsQueryDTO query) {
            java.util.Map<Object, Object> entries = redisTemplate.opsForHash().entries(GOODS_LIST_KEY);
            
            java.util.List<Goods> allGoods = entries.values().stream()
                    .map(obj -> (Goods) obj)
                    .collect(java.util.stream.Collectors.toList());

            if (query.getName() != null && !query.getName().isEmpty()) {
                allGoods = allGoods.stream()
                        .filter(g -> g.getName() != null && g.getName().contains(query.getName()))
                        .collect(java.util.stream.Collectors.toList());
            }
            
            if (query.getCode() != null && !query.getCode().isEmpty()) {
                allGoods = allGoods.stream()
                        .filter(g -> g.getCode() != null && g.getCode().contains(query.getCode()))
                        .collect(java.util.stream.Collectors.toList());
            }
            
            if (query.getStatus() != null) {
                allGoods = allGoods.stream()
                        .filter(g -> query.getStatus().equals(g.getStatus()))
                        .collect(java.util.stream.Collectors.toList());
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
            
            java.util.List<Goods> pageData = fromIndex < allGoods.size() 
                    ? allGoods.subList(fromIndex, toIndex) 
                    : new java.util.ArrayList<>();

            return PageResult.of(pageData, total, pageNum, pageSize);
        }
    }
}
