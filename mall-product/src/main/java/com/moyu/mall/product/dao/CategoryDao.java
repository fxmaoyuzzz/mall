package com.moyu.mall.product.dao;

import com.moyu.mall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author flandre
 * @email fxmaoyuzzz@126.com
 * @date 2022-08-01 20:52:08
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
