package com.moyu.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.common.to.SkuReductionTo;
import com.moyu.common.to.SpuBoundsTo;
import com.moyu.common.to.es.SkuEsModel;
import com.moyu.common.utils.PageUtils;
import com.moyu.common.utils.Query;
import com.moyu.common.utils.R;
import com.moyu.mall.product.dao.SpuInfoDao;
import com.moyu.mall.product.entity.*;
import com.moyu.mall.product.feign.CouponFeignService;
import com.moyu.mall.product.service.*;
import com.moyu.mall.product.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService attrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        //保存spu基本信息
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        //保存spu描述图片
        List<String> decript = spuSaveVo.getDecript();
        if (CollectionUtils.isNotEmpty(decript)) {
            SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
            spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
            spuInfoDescEntity.setDecript(String.join(",", decript));
            spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);
        }

        //保存spu图片集
        List<String> images = spuSaveVo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);

        //保存spu规格参数
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        if (CollectionUtils.isNotEmpty(baseAttrs)) {
            List<ProductAttrValueEntity> attrValueEntityList = baseAttrs.stream().map(attr -> {
                ProductAttrValueEntity attrValueEntity = new ProductAttrValueEntity();
                attrValueEntity.setAttrId(attr.getAttrId());
                if (attr.getAttrId() != null) {
                    AttrEntity attrEntity = attrService.getById(attr.getAttrId());
                    attrValueEntity.setAttrName(attrEntity.getAttrName());
                }
                attrValueEntity.setAttrValue(attrValueEntity.getAttrValue());
                attrValueEntity.setQuickShow(attrValueEntity.getQuickShow());
                attrValueEntity.setSpuId(spuInfoEntity.getId());

                return attrValueEntity;
            }).collect(Collectors.toList());

            attrValueService.saveBatchProductAttr(attrValueEntityList);
        }

        //保存spu的积分信息
        Bounds spuBounds = spuSaveVo.getBounds();
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(spuBounds, spuBoundsTo);
        spuBoundsTo.setSpuId(spuInfoEntity.getId());
        R saveSpuBoundsResponse = couponFeignService.saveSpuBounds(spuBoundsTo);
        if (saveSpuBoundsResponse.getCode() != 0){
            log.error("远程保存 spu 优惠信息失败");
        }


        //保存 spu 对应 sku
        List<Skus> skuList = spuSaveVo.getSkus();
        if (CollectionUtils.isNotEmpty(skuList)){
            skuList.forEach(item -> {
                String defaultImg = StringUtils.EMPTY;
                if (CollectionUtils.isNotEmpty(item.getImages())){
                    for (Images image : item.getImages()) {
                        if (image.getDefaultImg() == 1){
                            defaultImg = image.getImgUrl();
                        }
                    }
                }
                //保存 sku 基本信息
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                log.info("保存 skuInfoEntity:{}", JSON.toJSONString(skuInfoEntity));
                skuInfoService.saveSkuInfo(skuInfoEntity);

                Long skuId = skuInfoEntity.getSkuId();

                if (CollectionUtils.isNotEmpty(item.getImages())){
                    List<SkuImagesEntity> skuImagesEntityList = item.getImages().stream().map(img -> {
                        SkuImagesEntity skuImages = new SkuImagesEntity();
                        skuImages.setSkuId(skuId);
                        skuImages.setImgUrl(img.getImgUrl());
                        skuImages.setDefaultImg(img.getDefaultImg());

                        return skuImages;
                    }).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(skuImagesEntityList)){
                        List<SkuImagesEntity> imagesEntityList = skuImagesEntityList.stream()
                                .filter(imagesEntity -> StringUtils.isNotBlank(imagesEntity.getImgUrl()))
                                .collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(imagesEntityList)){
                            //保存 sku 图片信息
                            skuImagesService.saveBatch(imagesEntityList);
                        }
                    }
                }

                //保存 sku 销售属性
                List<Attr> attrList = item.getAttr();
                if (CollectionUtils.isNotEmpty(attrList)) {
                    List<SkuSaleAttrValueEntity> skuSaleAttrValueList = attrList.stream().map(attr -> {
                        SkuSaleAttrValueEntity skuSaleAttrValue = new SkuSaleAttrValueEntity();
                        BeanUtils.copyProperties(attr, skuSaleAttrValue);
                        skuSaleAttrValue.setSkuId(skuId);

                        return skuSaleAttrValue;
                    }).collect(Collectors.toList());

                    skuSaleAttrValueService.saveBatch(skuSaleAttrValueList);
                }

                //保存 sku 优惠、满减信息

                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(BigDecimal.ZERO) > 0) {
                    R response = couponFeignService.saveSkuRedution(skuReductionTo);
                    if (response.getCode() != 0){
                        log.error("远程保存 sku 优惠信息失败");
                    }
                }
            });
        }


    }

    /**
     * 保存 spu 基本信息
     * @param spuInfoEntity
     */
    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        log.info("查询 spu 信息:{}", JSON.toJSONString(params));
        LambdaQueryWrapper<SpuInfoEntity> wrapper = new LambdaQueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            wrapper.and((item) -> {
                item.eq(SpuInfoEntity::getId, key)
                        .or()
                        .like(SpuInfoEntity::getSpuName, key);
            });
        }
        String status = (String) params.get("status");
        if (StringUtils.isNotBlank(status)) {
            wrapper.eq(SpuInfoEntity::getPublishStatus, status);
        }
        String brandId = (String) params.get("brandId");
        if (StringUtils.isNotBlank(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq(SpuInfoEntity::getBrandId, brandId);
        }
        String catelogId = (String) params.get("catelogId");
        if (StringUtils.isNotBlank(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq(SpuInfoEntity::getCatalogId, catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void spuUp(Long spuId) {
        ArrayList<SkuEsModel> upSkuList = new ArrayList<>();

        List<ProductAttrValueEntity> baseAttrListForSpu = attrValueService.baseAttrListForSpu(spuId);
        List<Long> attrIdList = baseAttrListForSpu.stream().map(ProductAttrValueEntity::getAttrId)
                .collect(Collectors.toList());
        List<Long> searchAttrIdList = attrService.listSearchAttrs(attrIdList);

        HashSet<Long> idSet = new HashSet<>(searchAttrIdList);

        List<ProductAttrValueEntity> attrValueList = new ArrayList<>();
        //if (CollectionUtils.isEmpty(baseAttrListForSpu)) {
            List<SkuEsModel.Attrs> attrsList = baseAttrListForSpu.stream()
                    .filter(item -> idSet.contains(item.getAttrId()))
                    .map(item -> {
                        SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
                        BeanUtils.copyProperties(item, attrs);

                        return attrs;
                    }).collect(Collectors.toList());
        //}


        SkuEsModel skuEsModel = new SkuEsModel();
        List<SkuInfoEntity> skuInfoEntityList = skuInfoService.getSkuBySpuId(spuId);

        List<SkuEsModel> esModelList = skuInfoEntityList.stream().map(item -> {
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(item, esModel);
            esModel.setSkuPrice(item.getPrice());
            esModel.setSkuImg(item.getSkuDefaultImg());

            //TODO:远程调用库存系统是否有库存


            BrandEntity brand = brandService.getById(esModel.getBrandId());
            if (brand != null) {
                esModel.setBrandName(brand.getName());
                esModel.setBrandImg(brand.getLogo());
            }
            CategoryEntity category = categoryService.getById(esModel.getCatalogId());
            if (category != null) {
                esModel.setCatalogName(category.getName());
            }

            if (CollectionUtils.isNotEmpty(attrValueList)){

            }

            esModel.setHotScore(0L);

            //设置检索属性
            esModel.setAttrs(attrsList);


            return esModel;
        }).collect(Collectors.toList());

        //发送 ES 保存数据
    }
}