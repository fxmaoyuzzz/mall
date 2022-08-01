package com.moyu.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.common.utils.PageUtils;
import com.moyu.mall.coupon.entity.HomeAdvEntity;

import java.util.Map;

/**
 * 首页轮播广告
 *
 * @author flandre
 * @email fxmaoyuzzz@126.com
 * @date 2022-08-01 21:32:16
 */
public interface HomeAdvService extends IService<HomeAdvEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

