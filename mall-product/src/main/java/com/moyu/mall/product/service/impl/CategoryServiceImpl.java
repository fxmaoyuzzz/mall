package com.moyu.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.common.utils.PageUtils;
import com.moyu.common.utils.Query;
import com.moyu.mall.product.dao.CategoryDao;
import com.moyu.mall.product.entity.CategoryEntity;
import com.moyu.mall.product.service.CategoryBrandRelationService;
import com.moyu.mall.product.service.CategoryService;
import com.moyu.mall.product.vo.Catelog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

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

    @Override
    public Long[] findCateLogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> cateLogPath = findParentCateLogPath(catelogId, paths);
        Collections.reverse(cateLogPath);

        return cateLogPath.toArray(new Long[cateLogPath.size()]);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);

        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    @Override
    public List<CategoryEntity> getLevel1Category() {
        LambdaQueryWrapper<CategoryEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryEntity::getParentCid, 0);
        List<CategoryEntity> list = baseMapper.selectList(wrapper);

        return list;
    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        List<CategoryEntity> level1Category = getLevel1Category();
        Map<String, List<Catelog2Vo>> listMap = level1Category.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //查询 1 级分类的二级分类
            LambdaQueryWrapper<CategoryEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CategoryEntity::getParentCid, v.getCatId());
            List<CategoryEntity> list = baseMapper.selectList(wrapper);

            List<Catelog2Vo> catelog2VoList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(list)) {
                catelog2VoList = list.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());

                    LambdaQueryWrapper<CategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(CategoryEntity::getParentCid, l2.getCatId());
                    List<CategoryEntity> level3List = baseMapper.selectList(queryWrapper);
                    if (CollectionUtils.isNotEmpty(level3List)) {
                        List<Catelog2Vo.Catelog3Vo> catelog3VoList = level3List.stream().map(item3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), item3.getCatId().toString(), item3.getName());

                            return catelog3Vo;
                        }).collect(Collectors.toList());

                        catelog2Vo.setCatalog3List(catelog3VoList);
                    }


                    return catelog2Vo;

                }).collect(Collectors.toList());
            }


            return catelog2VoList;
        }));

        return listMap;
    }

    private List<Long> findParentCateLogPath(Long catelogId, List<Long> paths) {
        paths.add(catelogId);

        CategoryEntity category = this.getById(catelogId);
        if (category.getParentCid() != 0){
            findParentCateLogPath(category.getParentCid(), paths);
        }

        return paths;
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