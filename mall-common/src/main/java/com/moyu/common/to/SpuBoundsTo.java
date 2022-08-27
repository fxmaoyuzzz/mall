package com.moyu.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/08/27
 */
@Data
public class SpuBoundsTo {

    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
