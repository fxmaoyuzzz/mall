package com.moyu.mall.coupon.controller;

import com.moyu.common.utils.PageUtils;
import com.moyu.common.utils.R;
import com.moyu.mall.coupon.entity.CouponEntity;
import com.moyu.mall.coupon.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * 优惠券信息
 *
 * @author flandre
 * @email fxmaoyuzzz@126.com
 * @date 2022-08-01 21:32:16
 */
@RefreshScope
@RestController
@RequestMapping("coupon/coupon")
public class CouponController {
    @Autowired
    private CouponService couponService;

    @Value("${coupon.user.name}")
    private String name;


    /**
     * Nacos 配置中心测试
     *
     * 步骤：
     * 1、引入依赖
     *         <dependency>
     *             <groupId>com.alibaba.cloud</groupId>
     *             <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
     *         </dependency>
     *
     * 2、创建 bootstrap.properties，配置上服务名和配置中心地址
     *          spring.application.name=mall-coupon
     *          spring.cloud.nacos.config.server-addr=127.0.0.1:8848
     * 3、配置中心添加数据集应用名.properties
     * 4、在数据集中添加配置内容
     * 5、动态获取配置：在 controller上面标注@RefreshScope注解
     *
     *      >>> 如果配置中心和应用的配置文件中同时配置了相同内容，优先使用配置中心的内容
     *
     * @return
     */
    @RequestMapping("/test")
    public R configTest() {

        return R.ok().put("name", name);
    }


    /**
     * 远程调用测试
     *
     * 步骤：
     * 1、引入 open-feign
     * 2、编写一个接口，告诉 springCloud 这个接口需要调用远程服务(见CouponFeignService)
     *      2-1、声明接口的方法是调用哪个远程服务的哪个请求（见feign.CouponFeignService#memberCoupon()）
     * 3、开启远程调用功能：启动类上标注@EnableFeignClients注解(注解上需要标注远程调用接口的包全类名)
     *
     */
    @RequestMapping("/member/list")
    public R memberCoupon(){
        CouponEntity couponEntity = new CouponEntity();
        couponEntity.setCouponName("满 100 减 10");

        return R.ok().put("coupons", Arrays.asList(couponEntity));
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
        public R list(@RequestParam Map<String, Object> params){
        PageUtils page = couponService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
        public R info(@PathVariable("id") Long id){
		CouponEntity coupon = couponService.getById(id);

        return R.ok().put("coupon", coupon);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
        public R save(@RequestBody CouponEntity coupon){
		couponService.save(coupon);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
        public R update(@RequestBody CouponEntity coupon){
		couponService.updateById(coupon);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
        public R delete(@RequestBody Long[] ids){
		couponService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
