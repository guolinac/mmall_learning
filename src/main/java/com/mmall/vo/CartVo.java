package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by guolin
 */
@Getter
@Setter
public class CartVo {

    private List<CartProductVo> cartProductVoList;
    private BigDecimal cartTotalPrice; // 购物车的总价
    private Boolean allChecked; // 是否已经都勾选
    private String imageHost; // 图片前缀http://img.happymmall.com/
}
