package com.moyu.mall.product.vo;

import com.moyu.mall.product.bo.AttrEntityBo;
import lombok.Data;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/08/23
 */
@Data
public class AttrVo extends AttrEntityBo {

    private String catelogName;

    private String groupName;

    private Long[] catelogPath;
}
