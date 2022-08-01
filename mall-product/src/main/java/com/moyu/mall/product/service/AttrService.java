package com.moyu.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.common.utils.PageUtils;
import com.moyu.mall.product.entity.AttrEntity;

import java.util.Map;

/**
 * 商品属性
 *
 * @author flandre
 * @email fxmaoyuzzz@126.com
 * @date 2022-08-01 20:52:08
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

