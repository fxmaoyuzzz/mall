package com.moyu.mall.product.vo;

import com.moyu.mall.product.entity.AttrEntity;
import com.moyu.mall.product.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/08/24
 */
@Data
public class AttrGroupWithAttrsVo extends AttrGroupEntity {

    private List<AttrEntity> attrs;
}
