package com.moyu.common.enums;

import lombok.Getter;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/08/23
 */
@Getter
public enum AttrEnum {

    ATTR_TYPE_BASE(1, "基本属性"),
    ATTR_TYPE_SALE(0, "销售属性"),
    ;

    private int code;

    private String msg;


    AttrEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
