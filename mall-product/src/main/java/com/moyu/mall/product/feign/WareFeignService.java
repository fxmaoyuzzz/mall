package com.moyu.mall.product.feign;

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

@FeignClient("mall-ware")
public interface WareFeignService {


    /**
     * 查询是否有库存
     */
    @PostMapping("ware/waresku/hashStock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);
}
