package com.mmall.pojo;

import lombok.*;

import java.util.Date;

//@Data 包含Getter,Setter,ToString,EqualsAndHashCode和protected的canEqual方法
@Getter
@Setter
@NoArgsConstructor // 无参构造器
@AllArgsConstructor // 全参构造器
public class Cart {
    private Integer id;

    private Integer userId;

    private Integer productId;

    private Integer quantity;

    private Integer checked;

    private Date createTime;

    private Date updateTime;
}