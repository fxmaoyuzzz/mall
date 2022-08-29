package com.moyu.mall.ware.bo;

import lombok.Data;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/08/29
 */
@Data
public class PurchaseDoneItemBo {

    private Long itemId;

    private Integer status;

    private String reason;
}
