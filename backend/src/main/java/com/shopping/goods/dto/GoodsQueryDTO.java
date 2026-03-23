package com.shopping.goods.dto;

import lombok.Data;

@Data
public class GoodsQueryDTO {
    private String name;
    private String code;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
