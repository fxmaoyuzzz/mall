package com.moyu.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.common.utils.PageUtils;
import com.moyu.common.utils.Query;
import com.moyu.mall.product.dao.AttrGroupDao;
import com.moyu.mall.product.entity.AttrGroupEntity;
import com.moyu.mall.product.service.AttrGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Slf4j
@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        log.info("查询属性分组信息,入参:{}, {}", JSON.toJSONString(params), catelogId);
        if (catelogId == 0){
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    new QueryWrapper<AttrGroupEntity>());

            return new PageUtils(page);
        }else{
            String key = (String) params.get("key");
            LambdaQueryWrapper<AttrGroupEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AttrGroupEntity::getCatelogId, catelogId);
            if (StringUtils.isNotBlank(key)){
                wrapper.and((item) -> {
                    item.eq(AttrGroupEntity::getAttrGroupId, key)
                            .or()
                            .like(AttrGroupEntity::getAttrGroupName, key);
                });
            }
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    wrapper);

            return new PageUtils(page);
        }
    }
}