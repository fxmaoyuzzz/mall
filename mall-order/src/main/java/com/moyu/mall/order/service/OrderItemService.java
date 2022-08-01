package com.moyu.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.common.utils.PageUtils;
import com.moyu.mall.order.entity.OrderItemEntity;

import java.util.Map;

/**
 * 订单项信息
 *
 * @author flandre
 * @email fxmaoyuzzz@126.com
 * @date 2022-08-01 21:41:23
 */
public interface OrderItemService extends IService<OrderItemEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

