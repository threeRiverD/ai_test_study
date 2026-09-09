package com.demo.util;

import com.demo.entity.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常统一处理
 */
@RestControllerAdvice
public class GlobalExceptionHandle {

    /**
     * 捕获空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public Result HandleNP(NullPointerException ep){
        return Result.fail("空指针异常");
    }
    /**
     * 捕获通用异常
     */
    @ExceptionHandler(Exception.class)
    public Result HandleOthers(Exception ep){
        return Result.fail(ep.getMessage());
    }
}
