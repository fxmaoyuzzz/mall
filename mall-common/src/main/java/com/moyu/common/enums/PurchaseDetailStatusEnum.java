package com.moyu.common.enums;

import lombok.Getter;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/08/27
 */
@Getter
public enum PurchaseDetailStatusEnum {

    CREATE(0, "新建"),
    ASSIGNED(1, "已分配"),
    BUYING(2, "正在采购"),
    FINISHED(3, "已完成"),
    ERROR(4, " 采购失败"),
            ;

    private int code;

    private String msg;


    PurchaseDetailStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
