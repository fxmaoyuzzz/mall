package com.moyu.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.common.utils.PageUtils;
import com.moyu.mall.coupon.entity.CouponHistoryEntity;

import java.util.Map;

/**
 * 优惠券领取历史记录
 *
 * @author flandre
 * @email fxmaoyuzzz@126.com
 * @date 2022-08-01 21:32:16
 */
public interface CouponHistoryService extends IService<CouponHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

