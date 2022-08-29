package com.moyu.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.common.utils.PageUtils;
import com.moyu.common.utils.Query;
import com.moyu.mall.product.dao.ProductAttrValueDao;
import com.moyu.mall.product.entity.ProductAttrValueEntity;
import com.moyu.mall.product.service.ProductAttrValueService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveBatchProductAttr(List<ProductAttrValueEntity> attrValueEntityList) {
        if (CollectionUtils.isNotEmpty(attrValueEntityList)) {
            this.saveBatch(attrValueEntityList);
        }
    }

    @Override
    public List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId) {
        LambdaQueryWrapper<ProductAttrValueEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductAttrValueEntity::getSpuId, spuId);
        List<ProductAttrValueEntity> list = baseMapper.selectList(wrapper);

        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> list) {
        LambdaQueryWrapper<ProductAttrValueEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductAttrValueEntity::getSpuId, spuId);

        baseMapper.delete(wrapper);

        if (CollectionUtils.isEmpty(list)) {
            List<ProductAttrValueEntity> entityList = list.stream().map(item -> {
                item.setSpuId(spuId);
                item.setId(null);

                return item;
            }).collect(Collectors.toList());

            this.saveBatch(entityList);
        }
    }

}