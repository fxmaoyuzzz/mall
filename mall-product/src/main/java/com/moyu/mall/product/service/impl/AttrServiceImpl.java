package com.moyu.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.common.enums.AttrEnum;
import com.moyu.common.utils.PageUtils;
import com.moyu.common.utils.Query;
import com.moyu.mall.product.bo.AttrEntityBo;
import com.moyu.mall.product.bo.AttrRelationBo;
import com.moyu.mall.product.dao.AttrAttrgroupRelationDao;
import com.moyu.mall.product.dao.AttrDao;
import com.moyu.mall.product.dao.AttrGroupDao;
import com.moyu.mall.product.dao.CategoryDao;
import com.moyu.mall.product.entity.AttrAttrgroupRelationEntity;
import com.moyu.mall.product.entity.AttrEntity;
import com.moyu.mall.product.entity.AttrGroupEntity;
import com.moyu.mall.product.entity.CategoryEntity;
import com.moyu.mall.product.service.AttrService;
import com.moyu.mall.product.service.CategoryService;
import com.moyu.mall.product.vo.AttrVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationDao relationDao;

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryService categoryService;

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

        if (attrEntityBo.getAttrType().equals(AttrEnum.ATTR_TYPE_BASE.getCode())) {

            //保存关联关系
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attrEntityBo.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());

            relationDao.insert(relationEntity);
        }

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

    @Override
    public AttrVo getAttrInfo(Long attrId) {
        AttrVo attrVo = new AttrVo();

        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, attrVo);

        if (attrEntity.getAttrType().equals(AttrEnum.ATTR_TYPE_BASE.getCode())) {

            LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AttrAttrgroupRelationEntity::getAttrId, attrId);
            AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(wrapper);
            if (relationEntity != null && relationEntity.getAttrGroupId() != null) {
                attrVo.setAttrGroupId(relationEntity.getAttrGroupId());

                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                if (attrGroupEntity != null && StringUtils.isNotBlank(attrGroupEntity.getAttrGroupName())) {
                    attrVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }

        Long catelogId = attrEntity.getCatelogId();

        Long[] cateLogPath = categoryService.findCateLogPath(catelogId);
        attrVo.setCatelogPath(cateLogPath);

        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if (categoryEntity != null && StringUtils.isNotBlank(categoryEntity.getName())) {
            attrVo.setCatelogName(categoryEntity.getName());
        }

        return attrVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAttr(AttrVo attr) {
        this.updateById(attr);

        if (attr.getAttrType().equals(AttrEnum.ATTR_TYPE_BASE.getCode())) {
            LambdaUpdateWrapper<AttrAttrgroupRelationEntity> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(AttrAttrgroupRelationEntity::getAttrId, attr.getAttrId());

            Integer count = relationDao.selectCount(wrapper);
            AttrAttrgroupRelationEntity relation = new AttrAttrgroupRelationEntity();
            relation.setAttrGroupId(attr.getAttrGroupId());
            relation.setAttrId(attr.getAttrId());

            if (count > 0) {
                relationDao.update(relation, wrapper);
            } else {
                relationDao.insert(relation);
            }
        }
    }

    @Override
    public PageUtils saleAttrList(Map<String, Object> params, Long catelogId) {
        LambdaQueryWrapper<AttrEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttrEntity::getAttrType, AttrEnum.ATTR_TYPE_SALE.getCode());

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

                        //LambdaQueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
                        //queryWrapper.eq(AttrAttrgroupRelationEntity::getAttrId, item.getAttrId());
                        //AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(queryWrapper);
                        //if (relationEntity != null) {
                        //    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                        //    if (attrGroupEntity != null && StringUtils.isNotBlank(attrGroupEntity.getAttrGroupName())) {
                        //        attrVo.setGroupName(attrGroupEntity.getAttrGroupName());
                        //    }
                        //}

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

    @Override
    public List<AttrEntity> getAttrRelation(Long attrgroupId) {
        LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrgroupId);

        List<AttrAttrgroupRelationEntity> relationEntityList = relationDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(relationEntityList)) {
            List<Long> attrIdList = relationEntityList.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(attrIdList)) {
                Collection<AttrEntity> collection = this.listByIds(attrIdList);

                return (List<AttrEntity>) collection;
            }
        }

        return new ArrayList<AttrEntity>();
    }

    @Override
    public void deleteRelation(AttrRelationBo[] attrRelationBos) {
        List<AttrAttrgroupRelationEntity> entityList = Arrays.asList(attrRelationBos).stream().map(item -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);

            return relationEntity;
        }).collect(Collectors.toList());

        relationDao.deleteBatchRelation(entityList);

    }

    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();

        LambdaQueryWrapper<AttrGroupEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttrGroupEntity::getCatelogId, catelogId);
        List<AttrGroupEntity> list = attrGroupDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> attrGroupIdList = list.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
            LambdaQueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupIdList);

            List<AttrAttrgroupRelationEntity> relationEntityList = relationDao.selectList(queryWrapper);
            if (CollectionUtils.isNotEmpty(relationEntityList)){
                List<Long> attrIdList = relationEntityList.stream().map(AttrAttrgroupRelationEntity::getAttrId)
                        .collect(Collectors.toList());

                LambdaQueryWrapper<AttrEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(AttrEntity::getCatelogId, catelogId)
                        .eq(AttrEntity::getAttrType, AttrEnum.ATTR_TYPE_BASE.getCode());
                if (CollectionUtils.isNotEmpty(attrIdList)){
                    lambdaQueryWrapper.notIn(AttrEntity::getAttrId, attrIdList);
                }

                String key = (String) params.get("key");
                if (StringUtils.isNotBlank(key)) {
                    lambdaQueryWrapper.and( w -> {
                        w.eq(AttrEntity::getAttrId, key)
                                .or()
                                .like(AttrEntity::getAttrName, key);
                    });
                }

                IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), lambdaQueryWrapper);

                PageUtils pageUtils = new PageUtils(page);

                return pageUtils;
            }
        }

        return null;
    }

}