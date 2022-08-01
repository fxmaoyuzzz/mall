package com.moyu.mall.coupon.dao;

import com.moyu.mall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author flandre
 * @email fxmaoyuzzz@126.com
 * @date 2022-08-01 21:32:16
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
