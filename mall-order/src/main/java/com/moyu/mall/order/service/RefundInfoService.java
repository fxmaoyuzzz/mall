package com.moyu.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.common.utils.PageUtils;
import com.moyu.mall.order.entity.RefundInfoEntity;

import java.util.Map;

/**
 * 退款信息
 *
 * @author flandre
 * @email fxmaoyuzzz@126.com
 * @date 2022-08-01 21:41:23
 */
public interface RefundInfoService extends IService<RefundInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

