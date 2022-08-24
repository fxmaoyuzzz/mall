package com.moyu.mall.product.controller;

import com.moyu.common.utils.PageUtils;
import com.moyu.common.utils.R;
import com.moyu.mall.product.entity.BrandEntity;
import com.moyu.mall.product.entity.CategoryBrandRelationEntity;
import com.moyu.mall.product.service.CategoryBrandRelationService;
import com.moyu.mall.product.vo.BrandVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 品牌分类关联
 *
 * @author flandre
 * @email fxmaoyuzzz@126.com
 * @date 2022-08-01 21:08:01
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;


    /**
     * 列表
     */
    @GetMapping(value = "/brands/list")
    public R relationBrandList(@RequestParam(value = "catId", required = true) Long catId) {
        List<BrandEntity> data = categoryBrandRelationService.getBrandByCatId(catId);

        List<BrandVo> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(data)) {
            list = data.stream().map(item -> {
                BrandVo brandVo = new BrandVo();
                brandVo.setBrandId(item.getBrandId());
                brandVo.setBrandName(item.getName());

                return brandVo;
            }).collect(Collectors.toList());
        }

        return R.ok().put("data", list);
    }

    /**
     * 列表
     */
    @GetMapping(value = "/catelog/list")
    public R catelogList(@RequestParam("brandId") Long brandId) {
        List<CategoryBrandRelationEntity> data = categoryBrandRelationService.catelogList(brandId);

        return R.ok().put("data", data);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.saveDetail(categoryBrandRelation);

        //categoryBrandRelationService.save(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
