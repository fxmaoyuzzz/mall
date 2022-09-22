package com.moyu.mall.search.service;

import com.moyu.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/09/22
 */
public interface ProductSaveService {

    /**
     * 商品上架
     * @param skuEsModels
     */
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
