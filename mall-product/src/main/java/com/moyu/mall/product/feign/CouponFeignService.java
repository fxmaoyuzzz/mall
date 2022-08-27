package com.moyu.mall.product.feign;

import com.moyu.common.to.SkuReductionTo;
import com.moyu.common.to.SpuBoundsTo;
import com.moyu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/08/27
 */
@FeignClient("mall-coupon")
public interface CouponFeignService {

    /**
     * 只要字段属性名一致，双方服务无需使用同一个对象
     *   （springCloud使用json传输对象）
     *
     *
     * 保存spu的积分信息
     * @param spuBoundsTo
     */
    @PostMapping("coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);

    /**
     * 保存 sku积分信息
     * @param skuReductionTo
     */
    @PostMapping("coupon/skufullreduction/saveinfo")
    R saveSkuRedution(@RequestBody SkuReductionTo skuReductionTo);
}
