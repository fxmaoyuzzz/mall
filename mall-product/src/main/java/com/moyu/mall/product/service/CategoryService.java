package com.moyu.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.common.utils.PageUtils;
import com.moyu.mall.product.entity.CategoryEntity;
import com.moyu.mall.product.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author flandre
 * @email fxmaoyuzzz@126.com
 * @date 2022-08-01 20:52:08
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查出所有分类及子分类，并组装树形结构
     * @return
     */
    List<CategoryEntity> listWithTree();

    /**
     * 批量删除菜单
     * @param asList
     */
    void removeMenuBatchByIds(List<Long> asList);

    /**
     * 查询 catelogId完整路径
     * @param catelogId
     * @return
     */
    Long[] findCateLogPath(Long catelogId);

    void updateCascade(CategoryEntity category);

    /**
     * 查询所有一级分类
     * @return
     */
    List<CategoryEntity> getLevel1Category();

    /**
     * 查询所有分类并封装
     * @return
     */
    Map<String, List<Catelog2Vo>> getCatalogJson();
}

