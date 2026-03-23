package com.shopping.goods.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Goods implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String code;

    private BigDecimal price;

    private String description;

    private String image;

    private Integer status;

    private String unit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private Long createUserId;

    private Long updateUserId;
}
