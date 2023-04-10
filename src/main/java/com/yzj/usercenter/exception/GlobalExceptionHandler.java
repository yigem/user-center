package com.yzj.usercenter.exception;

import com.yzj.usercenter.common.BaseResponse;
import com.yzj.usercenter.common.ErrorCode;
import com.yzj.usercenter.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
//使用Spring的切面功能，可以在任意代码的前后做一个封装
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //通过注解来定义，对什么异常做什么处理
    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e){
        log.error("businessException: " + e.getMessage(),e);
        return ResultUtils.error(e.getCode(),e.getMessage(),e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e){
        log.error("runtimeException",e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),"");
    }

}
