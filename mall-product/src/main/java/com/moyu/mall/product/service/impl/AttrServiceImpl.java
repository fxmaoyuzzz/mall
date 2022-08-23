package com.moyu.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.common.utils.PageUtils;
import com.moyu.common.utils.Query;
import com.moyu.mall.product.bo.AttrEntityBo;
import com.moyu.mall.product.dao.AttrAttrgroupRelationDao;
import com.moyu.mall.product.dao.AttrDao;
import com.moyu.mall.product.dao.AttrGroupDao;
import com.moyu.mall.product.dao.CategoryDao;
import com.moyu.mall.product.entity.AttrAttrgroupRelationEntity;
import com.moyu.mall.product.entity.AttrEntity;
import com.moyu.mall.product.entity.AttrGroupEntity;
import com.moyu.mall.product.entity.CategoryEntity;
import com.moyu.mall.product.service.AttrService;
import com.moyu.mall.product.vo.AttrVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationDao relationDao;

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAttr(AttrEntityBo attrEntityBo) {
        //保存基本数据
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrEntityBo, attrEntity);
        this.save(attrEntity);

        //保存关联关系
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrGroupId(attrEntityBo.getAttrGroupId());
        relationEntity.setAttrId(attrEntity.getAttrId());

        relationDao.insert(relationEntity);

    }

    @Override
    public PageUtils queryBaseAttrList(Map<String, Object> params, Long catelogId) {
        LambdaQueryWrapper<AttrEntity> wrapper = new LambdaQueryWrapper<>();

        if (catelogId != 0){
            wrapper.eq(AttrEntity::getCatelogId, catelogId);
        }

        String key = (String) params.get("key");

        if (StringUtils.isNotBlank(key)){
            wrapper.and((item) -> {
                wrapper.eq(AttrEntity::getAttrId, key)
                        .or()
                        .like(AttrEntity::getAttrName, key);
            });
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> list = (List<AttrEntity>) pageUtils.getList();
        if (CollectionUtils.isNotEmpty(list)){
            List<AttrVo> attrVoList = list.stream()
                    .map(item -> {
                        AttrVo attrVo = new AttrVo();
                        BeanUtils.copyProperties(item, attrVo);

                        LambdaQueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
                        queryWrapper.eq(AttrAttrgroupRelationEntity::getAttrId, item.getAttrId());
                        AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(queryWrapper);
                        if (relationEntity != null) {
                            AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                            if (attrGroupEntity != null && StringUtils.isNotBlank(attrGroupEntity.getAttrGroupName())) {
                                attrVo.setGroupName(attrGroupEntity.getAttrGroupName());
                            }
                        }

                        CategoryEntity categoryEntity = categoryDao.selectById(item.getCatelogId());
                        if (categoryEntity != null && StringUtils.isNotBlank(categoryEntity.getName())) {
                            attrVo.setCatelogName(categoryEntity.getName());
                        }

                        return attrVo;
                    }).collect(Collectors.toList());

            pageUtils.setList(attrVoList);
        }


        return pageUtils;
    }

}