package com.moyu.mall.product.exception;

import com.moyu.common.enums.ExceptionCodeEnum;
import com.moyu.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 集中处理异常
 *
 * @Author: zenghai.li
 * @Description:
 * @Date: 2022/08/18
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.moyu.mall.product.controller")
public class MallException {

    @ExceptionHandler(value = Throwable.class)
    public R handlerValidException(Throwable throwable){
        log.error("未知异常：{}, 异常类型:{}", throwable.getMessage(), throwable.getClass());

        return R.error(ExceptionCodeEnum.UNKNOWN_EXCEPTION.getCode(), ExceptionCodeEnum.UNKNOWN_EXCEPTION.getMsg());
    }
}
