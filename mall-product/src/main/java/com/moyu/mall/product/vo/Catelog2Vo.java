package com.moyu.mall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/09/28
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Catelog2Vo {

    private String catalog1Id;

    private List<Catelog3Vo> catalog3List;

    private String id;

    private String name;

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    public static class Catelog3Vo{
        private String catalog2Id;

        private String id;

        private String name;
    }
}
