package com.moyu.mall.search.controller;

import com.moyu.common.enums.ExceptionCodeEnum;
import com.moyu.common.to.es.SkuEsModel;
import com.moyu.common.utils.R;
import com.moyu.mall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/09/22
 */
@Slf4j
@RequestMapping("/search/save")
@RestController
public class ElasticSearchSaveController {

    @Autowired
    private ProductSaveService productSaveService;


    /**
     * 上架商品
     */
    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {

        boolean statusUp = false;
        try {
            statusUp = productSaveService.productStatusUp(skuEsModels);
        } catch (IOException e) {
            log.error("商品上架错误:{}", e);
            return R.error(ExceptionCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), ExceptionCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if (!statusUp) {

            return R.ok();
        }else {
            return R.error(ExceptionCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), ExceptionCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }
    }
}
