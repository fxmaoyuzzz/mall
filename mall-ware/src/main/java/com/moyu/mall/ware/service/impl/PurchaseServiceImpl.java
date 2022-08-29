package com.moyu.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.common.enums.PurchaseDetailStatusEnum;
import com.moyu.common.enums.PurchaseStatusEnum;
import com.moyu.common.utils.PageUtils;
import com.moyu.common.utils.Query;
import com.moyu.mall.ware.bo.MergeBo;
import com.moyu.mall.ware.bo.PurchaseDoneBo;
import com.moyu.mall.ware.bo.PurchaseDoneItemBo;
import com.moyu.mall.ware.dao.PurchaseDao;
import com.moyu.mall.ware.entity.PurchaseDetailEntity;
import com.moyu.mall.ware.entity.PurchaseEntity;
import com.moyu.mall.ware.service.PurchaseDetailService;
import com.moyu.mall.ware.service.PurchaseService;
import com.moyu.mall.ware.service.WareSkuService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        LambdaQueryWrapper<PurchaseEntity> wrapper = new LambdaQueryWrapper<>();

        wrapper.and((w) -> {
                    w.eq(PurchaseEntity::getStatus, 0)
                            .or()
                            .eq(PurchaseEntity::getStatus, 1);
                });

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mergePurchase(MergeBo mergeBo) {
        Long purchaseId = mergeBo.getPurchaseId();

        PurchaseEntity purchase = new PurchaseEntity();
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();

            purchaseEntity.setStatus(PurchaseStatusEnum.CREATE.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);

            purchaseId = purchaseEntity.getId();
            purchase = purchaseEntity;
        }else {
            PurchaseEntity entity = this.getById(purchaseId);
            purchase = entity;
        }

        if (purchase != null
                && (purchase.getStatus().equals(PurchaseStatusEnum.CREATE.getCode())
                || purchase.getStatus().equals(PurchaseStatusEnum.ASSIGNED.getCode()))) {
            List<Long> items = mergeBo.getItems();
            if (CollectionUtils.isNotEmpty(items)) {
                Long finalPurchaseId = purchaseId;
                List<PurchaseDetailEntity> detailEntityList = items.stream().map(id -> {
                    PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                    detailEntity.setId(id);
                    detailEntity.setPurchaseId(finalPurchaseId);
                    detailEntity.setStatus(PurchaseDetailStatusEnum.ASSIGNED.getCode());

                    return detailEntity;
                }).collect(Collectors.toList());

                purchaseDetailService.updateBatchById(detailEntityList);

                PurchaseEntity entity = new PurchaseEntity();
                entity.setId(finalPurchaseId);
                entity.setUpdateTime(new Date());
                this.updateById(entity);
            }
        }else {
            log.error("采购单状态不正确");
            throw new IllegalStateException("采购单状态不正确");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receivedPurchase(List<Long> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            List<PurchaseEntity> purchaseList = ids.stream().map(item -> {
                PurchaseEntity purchase = this.getById(item);

                return purchase;
            }).filter(item -> item.getStatus().equals(PurchaseStatusEnum.CREATE.getCode())
                    || item.getStatus().equals(PurchaseStatusEnum.ASSIGNED.getCode()))
                    .map(item -> {
                        item.setStatus(PurchaseStatusEnum.RECEIVED.getCode());
                        item.setUpdateTime(new Date());

                        return item;
                    }).collect(Collectors.toList());



            if (CollectionUtils.isNotEmpty(purchaseList)) {
                this.updateBatchById(purchaseList);
                purchaseList.forEach(item -> {
                    List<PurchaseDetailEntity> list = purchaseDetailService.listDetailByPurchaseId(item.getId());
                    if (CollectionUtils.isNotEmpty(list)) {
                        List<PurchaseDetailEntity> updateList = list.stream().map(purchaseDetail -> {
                            PurchaseDetailEntity detail = new PurchaseDetailEntity();
                            detail.setId(purchaseDetail.getId());
                            detail.setStatus(PurchaseDetailStatusEnum.BUYING.getCode());

                            return detail;
                        }).collect(Collectors.toList());

                        purchaseDetailService.updateBatchById(updateList);
                    }
                });
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void donePurchase(PurchaseDoneBo doneBo) {
        Long purchaseId = doneBo.getId();

        //改变采购单内的采购项状态
        Boolean flag = true;
        List<PurchaseDoneItemBo> itemBoList = doneBo.getItems();
        List<PurchaseDetailEntity> doneItemList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(itemBoList)) {
            for (PurchaseDoneItemBo item : itemBoList) {
                PurchaseDetailEntity purchaseDetail = new PurchaseDetailEntity();

                if (item.getStatus().equals(PurchaseDetailStatusEnum.ERROR.getCode())){
                    flag = false;
                    purchaseDetail.setStatus(PurchaseDetailStatusEnum.ERROR.getCode());
                }else {
                    purchaseDetail.setStatus(PurchaseDetailStatusEnum.FINISHED.getCode());
                    //查询采购项详情
                    PurchaseDetailEntity purchaseDetailEntity = purchaseDetailService.getById(item.getItemId());
                    if (purchaseDetailEntity != null) {
                        //将成功采购的入库

                        wareSkuService.addStock(purchaseDetailEntity.getSkuId(), purchaseDetailEntity.getWareId(), purchaseDetailEntity.getSkuNum());
                    }
                }
                purchaseDetail.setId(item.getItemId());

                doneItemList.add(purchaseDetail);
            }
            purchaseDetailService.updateBatchById(doneItemList);
        }

        //改变采购单状态
        PurchaseEntity purchase = new PurchaseEntity();
        purchase.setId(purchaseId);
        purchase.setStatus(flag ? PurchaseStatusEnum.FINISHED.getCode() : PurchaseStatusEnum.EXCEPTION.getCode());
        purchase.setUpdateTime(new Date());
        this.updateById(purchase);
    }
}