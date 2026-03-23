package com.shopping.goods.pojo.dto;

import lombok.Data;

@Data
public class GoodsQueryRequest {
    private String name;
    private String code;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
