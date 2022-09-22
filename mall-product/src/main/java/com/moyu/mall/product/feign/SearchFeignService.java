package com.moyu.mall.product.feign;

import com.moyu.common.to.es.SkuEsModel;
import com.moyu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/09/22
 */
@FeignClient("mall-search")
public interface SearchFeignService {

    /**
     * 上架商品
     */
    @PostMapping("/search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
