package com.moyu.common.enums;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/09/22
 */
public enum StatusEnum {

    NEW(0, "新建"),
    UP(1, "上架"),
    DOWN(2, "下架"),
    ;

    private int code;

    private String msg;

    StatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
