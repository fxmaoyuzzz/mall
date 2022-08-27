package com.moyu.mall.product.service.impl;

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

import java.util.List;
import java.util.Map;


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

}