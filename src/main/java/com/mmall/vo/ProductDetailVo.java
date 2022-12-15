package com.mmall.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by guolin
 */
@Getter
@Setter
public class ProductDetailVo {
    private Integer id;

    private Integer categoryId;

    private String name;

    private String subtitle;

    private String mainImage;

    private String subImages;

    private String detail;

    private BigDecimal price;

    private Integer stock;

    private Integer status;

    private String createTime;

    private String updateTime;

    // 图片服务器的url的前缀，只需要url前缀+图片的地址，既可以把图片找出来
    private String imageHost;

    // 父分类
    private Integer parentCategoryId;
}
