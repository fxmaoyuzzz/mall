package com.moyu.mall.ware.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/09/22
 */

@Data
public class SkuHasStockVo implements Serializable {


    private static final long serialVersionUID = 1527811099490855598L;

    private Long skuId;

    private Boolean hasStock;
}
