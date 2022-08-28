package com.moyu.common.enums;

import lombok.Getter;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/08/27
 */
@Getter
public enum PurchaseStatusEnum {

    CREATE(0, "新建"),
    ASSIGNED(1, "已分配"),
    RECEIVED(2, "已领取"),
    FINISHED(3, "已完成"),
    EXCEPTION(4, "异常"),
    ;

    private int code;

    private String msg;


    PurchaseStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
