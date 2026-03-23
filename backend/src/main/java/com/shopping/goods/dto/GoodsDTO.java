package com.shopping.goods.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GoodsDTO {
    private String id;
    private String name;
    private String code;
    private Double price;
    private String description;
    private String imageUrl;
    private Integer status;
    private String unit;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long creatorId = 0L;
    private Long modifierId = 0L;
}
