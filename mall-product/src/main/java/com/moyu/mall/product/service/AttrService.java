package com.moyu.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.common.utils.PageUtils;
import com.moyu.mall.product.bo.AttrEntityBo;
import com.moyu.mall.product.bo.AttrRelationBo;
import com.moyu.mall.product.entity.AttrEntity;
import com.moyu.mall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author flandre
 * @email fxmaoyuzzz@126.com
 * @date 2022-08-01 20:52:08
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrEntityBo attrEntityBo);

    PageUtils queryBaseAttrList(Map<String, Object> params, Long catelogId);

    AttrVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    PageUtils saleAttrList(Map<String, Object> params, Long catelogId);

    /**
     * 根据分组 ID 查询关联的所有属性
     * @param attrgroupId
     * @return
     */
    List<AttrEntity> getAttrRelation(Long attrgroupId);

    void deleteRelation(AttrRelationBo[] attrRelationBos);
}

