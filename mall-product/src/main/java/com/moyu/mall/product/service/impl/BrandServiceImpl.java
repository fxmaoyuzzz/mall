package com.moyu.mall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.common.utils.PageUtils;
import com.moyu.common.utils.Query;
import com.moyu.mall.product.dao.BrandDao;
import com.moyu.mall.product.entity.BrandEntity;
import com.moyu.mall.product.service.BrandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;


@Slf4j
@Service
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                new QueryWrapper<BrandEntity>()
        );
        log.info("查询品牌数据:{}", JSONObject.toJSONString(page));

        return new PageUtils(page);
    }

}