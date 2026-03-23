package com.shopping.goods.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Goods implements Serializable {
    private static final long serialVersionUID = 1L;

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
    private Long creatorId;
    private Long modifierId;

    public Goods() {
        this.creatorId = 0L;
        this.modifierId = 0L;
    }
}
