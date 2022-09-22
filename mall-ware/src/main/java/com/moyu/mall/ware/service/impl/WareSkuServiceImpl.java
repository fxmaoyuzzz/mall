package com.moyu.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyu.common.utils.PageUtils;
import com.moyu.common.utils.Query;
import com.moyu.common.utils.R;
import com.moyu.mall.ware.dao.WareSkuDao;
import com.moyu.mall.ware.entity.WareSkuEntity;
import com.moyu.mall.ware.feign.ProductFeignService;
import com.moyu.mall.ware.feign.entity.SkuInfoEntity;
import com.moyu.mall.ware.service.WareSkuService;
import com.moyu.mall.ware.vo.SkuHasStockVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (StringUtils.isNotBlank(skuId)){
            wrapper.eq(WareSkuEntity::getSkuId, skuId);
        }

        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotBlank(wareId)){
            wrapper.eq(WareSkuEntity::getWareId, wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WareSkuEntity::getSkuId, skuId);
        wrapper.eq(WareSkuEntity::getWareId, wareId);
        List<WareSkuEntity> wareSkuEntityList = baseMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(wareSkuEntityList)) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            try {
                R info = productFeignService.info(skuId);
                if (info.getCode() == 0) {

                    /**
                     * 此处远程调用结果 info 是一个 LinkedHashMap
                     *
                     * 因为rpc远程调用在底层还是使用的HTTPClient，所以在传递参数的时候，必定要有个顺序，
                     * 当你传递集合（list/map）的时候集合里面的值也要有顺序，不然服务层在接的时候就出问题了，
                     * 所以它才会从list转为LinkedHashMap
                     */
                    Object skuInfo = info.get("skuInfo");
                    ObjectMapper mapper = new ObjectMapper();
                    SkuInfoEntity skuInfoEntity = mapper.convertValue(skuInfo, SkuInfoEntity.class);
                    wareSkuEntity.setSkuName(skuInfoEntity.getSkuName());
                }
            }catch (Exception e) {
                log.error("远程调用商品服务查询 sku 名称失败", e);
            }

            wareSkuEntity.setStockLocked(0);
            baseMapper.insert(wareSkuEntity);
        }else {
            baseMapper.addStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> stockVoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(skuIds)) {
            stockVoList = skuIds.stream().map(skuId -> {
                SkuHasStockVo hasStockVo = new SkuHasStockVo();
                //查询当前 sku 的总库存
                QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
                wrapper.select("IFNULL(SUM(stock), 0) AS stockSum");
                wrapper.eq("id", skuId);

                WareSkuEntity skuEntity = baseMapper.selectOne(wrapper);
                hasStockVo.setSkuId(skuId);
                hasStockVo.setHasStock(skuEntity != null ? skuEntity.getStockSum() > 0 : false);

                return hasStockVo;
            }).collect(Collectors.toList());
        }

        return stockVoList;
    }
}