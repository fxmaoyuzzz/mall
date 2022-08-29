package com.moyu.mall.product.controller;

import com.alibaba.fastjson.JSON;
import com.moyu.common.utils.PageUtils;
import com.moyu.common.utils.R;
import com.moyu.mall.product.bo.AttrEntityBo;
import com.moyu.mall.product.entity.ProductAttrValueEntity;
import com.moyu.mall.product.service.AttrService;
import com.moyu.mall.product.service.ProductAttrValueService;
import com.moyu.mall.product.vo.AttrVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 商品属性
 *
 * @author flandre
 * @email fxmaoyuzzz@126.com
 * @date 2022-08-01 21:08:01
 */
@Slf4j
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    /**
     * 查询商品规格属性
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrListForSpu(@PathVariable("spuId") Long spuId) {
        List<ProductAttrValueEntity> list = productAttrValueService.baseAttrListForSpu(spuId);

        return R.ok().put("data", list);
    }


    /**
     * 列表
     */
    @GetMapping("/sale/list/{catelogId}")
    public R saleAttrList(@RequestParam Map<String, Object> params, @PathVariable("catelogId") Long catelogId) {
        PageUtils page = attrService.saleAttrList(params, catelogId);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @GetMapping("/base/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String, Object> params, @PathVariable("catelogId") Long catelogId) {
        PageUtils page = attrService.queryBaseAttrList(params, catelogId);

        log.info("baseAttrList:{}", JSON.toJSONString(page));

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
        public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
        public R info(@PathVariable("attrId") Long attrId){
		AttrVo attr = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
        public R save(@RequestBody AttrEntityBo attrEntityBo){
		attrService.saveAttr(attrEntityBo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
        public R update(@RequestBody AttrVo attr){
		attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 批量修改
     */
    @PostMapping("/update/{spuId}")
    public R updateSpuAttr(@PathVariable("spuId") Long spuId,
                           @RequestBody List<ProductAttrValueEntity> list){
        productAttrValueService.updateSpuAttr(spuId, list);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
        public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
