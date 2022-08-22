package com.moyu.mall.product.exception;

import com.moyu.common.enums.ExceptionCodeEnum;
import com.moyu.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

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

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handlerValidException(MethodArgumentNotValidException exception){
        log.error("数据校验错误：{}，异常类型:{}", exception.getMessage(), exception.getClass());

        HashMap<String, String> map = new HashMap<>();
        BindingResult bindingResult = exception.getBindingResult();

        bindingResult.getFieldErrors().forEach( e -> {
            map.put(e.getField(), e.getDefaultMessage());
        });

        return R.error(ExceptionCodeEnum.VALID_EXCEPTION.getCode(), ExceptionCodeEnum.VALID_EXCEPTION.getMsg()).put("data", map);
    }

    @ExceptionHandler(value = Throwable.class)
    public R handlerValidException(Throwable throwable){
        log.error("未知异常：{}, 异常类型:{}", throwable.getMessage(), throwable.getClass());

        return R.error(ExceptionCodeEnum.UNKNOWN_EXCEPTION.getCode(), ExceptionCodeEnum.UNKNOWN_EXCEPTION.getMsg());
    }
}
