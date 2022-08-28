package com.moyu.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.common.utils.PageUtils;
import com.moyu.common.utils.Query;
import com.moyu.mall.ware.dao.PurchaseDetailDao;
import com.moyu.mall.ware.entity.PurchaseDetailEntity;
import com.moyu.mall.ware.service.PurchaseDetailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<PurchaseDetailEntity> wrapper = new LambdaQueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)){
            wrapper.and((item) -> {
                item.eq(PurchaseDetailEntity::getPurchaseId, key)
                        .or()
                        .eq(PurchaseDetailEntity::getSkuId, key);
            });
        }

        String status = (String) params.get("status");
        if (StringUtils.isNotBlank(status)){
            wrapper.eq(PurchaseDetailEntity::getStatus, status);
        }

        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotBlank(wareId)){
            wrapper.eq(PurchaseDetailEntity::getWareId, wareId);
        }

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<PurchaseDetailEntity> listDetailByPurchaseId(Long id) {
        LambdaQueryWrapper<PurchaseDetailEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurchaseDetailEntity::getPurchaseId, id);

        List<PurchaseDetailEntity> list = baseMapper.selectList(wrapper);

        return list;
    }

}