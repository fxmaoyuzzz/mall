package com.moyu.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/08/27
 */
@Data
public class SkuReductionTo {

    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPriceTo> memberPrice;
}
