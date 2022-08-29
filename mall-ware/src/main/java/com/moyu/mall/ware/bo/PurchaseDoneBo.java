package com.moyu.mall.ware.bo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/08/29
 */
@Data
public class PurchaseDoneBo {

    @NotNull(message = "采购单 ID 不能为空")
    private Long id;

    private List<PurchaseDoneItemBo> items;
}
