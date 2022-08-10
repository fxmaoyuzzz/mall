package com.moyu.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.common.utils.PageUtils;
import com.moyu.common.utils.Query;
import com.moyu.mall.product.dao.CategoryDao;
import com.moyu.mall.product.entity.CategoryEntity;
import com.moyu.mall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        //组装树形结构
        if (CollectionUtils.isNotEmpty(entities)){
            List<CategoryEntity> level1Menus = entities.stream().filter(item ->
                item.getParentCid() == 0
            ).map(menu -> {
                menu.setChildren(getChildren(menu, entities));

                return menu;
            }).sorted((menu1, menu2) -> {
                return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
            }).collect(Collectors.toList());

            return level1Menus;
        }
        return entities;
    }

    @Override
    public void removeMenuBatchByIds(List<Long> asList) {
        // TODO: 2022/8/9 检查 ID 是否被引用
        log.info("删除菜单：{}", JSON.toJSON(asList));
        baseMapper.deleteBatchIds(asList);
    }

    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all){
        List<CategoryEntity> children = all.stream().filter(item -> {
            return item.getParentCid().equals(root.getCatId());
        }).map(category -> {
            category.setChildren(getChildren(category, all));

            return category;
        }).sorted((category1, category2) -> {
            return (category1.getSort() == null ? 0 : category1.getSort()) - (category2.getSort() == null ? 0 :category2.getSort());
        }).collect(Collectors.toList());

        return children;
    }

}