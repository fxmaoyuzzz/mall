package com.moyu.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.moyu.common.constant.EsIndexConstant;
import com.moyu.common.to.es.SkuEsModel;
import com.moyu.mall.search.config.MallElasticSearchConfig;
import com.moyu.mall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/09/22
 */
@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {


    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {

        //建立索引、映射关系
        BulkRequest bulkRequest = new BulkRequest();
        skuEsModels.forEach(skuEsModel -> {
            IndexRequest indexRequest = new IndexRequest(EsIndexConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());

            String model = JSON.toJSONString(skuEsModel);
            indexRequest.source(model, XContentType.JSON);

            bulkRequest.add(indexRequest);
        });


        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, MallElasticSearchConfig.COMMON_OPTIONS);
        boolean hasFailures = bulk.hasFailures();
        List<String> list = Arrays.asList(bulk.getItems()).stream()
                .map(item -> item.getId())
                .collect(Collectors.toList());
        log.info("上架成功商品 ID:{}", list);

        return hasFailures;
    }
}
