package com.moyu.mall.ware.bo;

import lombok.Data;

import java.util.List;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/08/27
 */
@Data
public class MergeBo {

    private Long purchaseId;

    private List<Long> items;
}
