package com.moyu.mall.product.web;

import com.moyu.mall.product.entity.CategoryEntity;
import com.moyu.mall.product.service.CategoryService;
import com.moyu.mall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/09/28
 */

@Controller
public class IndexController {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    CategoryService categoryService;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model){
        List<CategoryEntity> categoryEntityList = categoryService.getLevel1Category();

        model.addAttribute("categorys", categoryEntityList);
        return "index";
    }


    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatalogJson(){
        Map<String, List<Catelog2Vo>> map = categoryService.getCatalogJson();

        return map;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        RLock lock = redissonClient.getLock("my-lock");

        lock.lock();
        try {
            System.out.println("加锁成功，执行业务" + Thread.currentThread().getId());
            Thread.sleep(10000);

        }catch (Exception e){

        }finally {
            System.out.println("解锁成功" + Thread.currentThread().getId());
            lock.unlock();
        }

        return "hello";
    }
}
