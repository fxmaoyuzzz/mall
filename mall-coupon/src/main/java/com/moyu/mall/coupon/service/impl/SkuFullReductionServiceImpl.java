package com.moyu.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.common.to.MemberPriceTo;
import com.moyu.common.to.SkuReductionTo;
import com.moyu.common.utils.PageUtils;
import com.moyu.common.utils.Query;
import com.moyu.mall.coupon.dao.SkuFullReductionDao;
import com.moyu.mall.coupon.entity.MemberPriceEntity;
import com.moyu.mall.coupon.entity.SkuFullReductionEntity;
import com.moyu.mall.coupon.entity.SkuLadderEntity;
import com.moyu.mall.coupon.service.MemberPriceService;
import com.moyu.mall.coupon.service.SkuFullReductionService;
import com.moyu.mall.coupon.service.SkuLadderService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReductionTo(SkuReductionTo skuReductionTo) {
        if (skuReductionTo.getFullCount() > 0) {
            SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
            skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
            skuLadderEntity.setFullCount(skuReductionTo.getFullCount());
            skuLadderEntity.setDiscount(skuReductionTo.getDiscount());
            skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
            skuLadderService.save(skuLadderEntity);
        }

        if (skuReductionTo.getFullPrice().compareTo(BigDecimal.ZERO) > 0) {
            SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
            BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);

            this.save(skuFullReductionEntity);
        }

        //保存会员价格
        List<MemberPriceTo> memberPriceList = skuReductionTo.getMemberPrice();
        if (CollectionUtils.isNotEmpty(memberPriceList)) {
            List<MemberPriceEntity> priceEntityList = memberPriceList.stream().map(item -> {
                MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
                memberPriceEntity.setMemberLevelId(item.getId());
                memberPriceEntity.setMemberLevelName(item.getName());
                memberPriceEntity.setMemberPrice(item.getPrice());
                memberPriceEntity.setAddOther(1);

                return memberPriceEntity;
            }).filter(item -> {
                 return  item.getMemberPrice().compareTo(BigDecimal.ZERO) > 0;
            }).collect(Collectors.toList());

            memberPriceService.saveBatch(priceEntityList);
        }
    }
}