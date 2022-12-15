package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by guolin
 */
@Getter
@Setter
public class CartProductVo {

//结合了产品和购物车的一个抽象对象

    // 购物车的字段
    private Integer id; // 购物车的id
    private Integer userId; // 用户id
    private Integer productId; // 商品id
    private Integer quantity; //购物车中此商品的数量

    // 产品的字段
    private String productName; // 名称
    private String productSubtitle; // 副标题
    private String productMainImage; // 主图
    private BigDecimal productPrice; // 价格
    private Integer productStatus; // 状态
    private BigDecimal productTotalPrice; // 总价
    private Integer productStock; // 库存

    private Integer productChecked; // 此商品是否勾选

    private String limitQuantity; // 限制数量的一个返回结果
}
