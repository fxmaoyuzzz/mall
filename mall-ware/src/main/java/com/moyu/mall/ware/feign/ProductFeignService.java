package com.moyu.mall.ware.feign;

import com.moyu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/08/29
 */
@FeignClient("mall-product")
public interface ProductFeignService {

    @RequestMapping("product/skuinfo/info/{skuId}")
    R info(@RequestParam Long skuId);
}
