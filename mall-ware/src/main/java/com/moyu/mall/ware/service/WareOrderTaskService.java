package com.moyu.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.common.utils.PageUtils;
import com.moyu.mall.ware.entity.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author flandre
 * @email fxmaoyuzzz@126.com
 * @date 2022-08-01 21:44:20
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

