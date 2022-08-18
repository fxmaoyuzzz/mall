package com.moyu.common.enums;

import lombok.Getter;

/**
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/08/18
 */
@Getter
public enum ExceptionCodeEnum {

    UNKNOWN_EXCEPTION(10000, "UNKNOWN_EXCEPTION"),

    VALID_EXCEPTION(10001, "参数校验失败"),

    ;

    private int code;

    private String msg;


    ExceptionCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
