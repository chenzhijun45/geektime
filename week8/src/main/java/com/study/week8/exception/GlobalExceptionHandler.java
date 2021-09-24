package com.study.week8.exception;

import com.study.week8.controller.response.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.TimeoutException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MyException.class)
    public R<R.ResultCode> globalExceptionResponse(MyException e) {
        log.error("MyException异常：", e);
        return R.fail(e.getMessage(), e.getCode());
    }

    @ExceptionHandler(value = Exception.class)
    public R<String> errorResponse(Exception e) {
        log.error("Exception异常：", e);
        //return R.fail(R.ResultCode.FAIL);
        //返回具体异常 便于处理
        return R.fail(e.getMessage());
    }

    /**
     * 405 - 当前请求方法不支持
     * 一般是前端没有按照规定的请求方法调用接口造成
     */
    @SuppressWarnings("rawtypes")
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("当前请求方法不支持", e);
        return R.fail("当前请求方法不支持");
    }

    /**
     * 408 - 请求超时
     */
    @SuppressWarnings("rawtypes")
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    @ExceptionHandler(TimeoutException.class)
    public R<String> handleTimeoutException(TimeoutException e) {
        log.error("请求超时", e);
        return R.fail("请求超时");
    }

    /**
     * 415 - 不支持当前的媒体类型
     * 一般是前端没有按照规定的请求方法调用接口造成
     */
    @SuppressWarnings("rawtypes")
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public R<String> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.error("不支持当前的媒体类型", e);
        return R.fail("不支持当前的媒体类型");
    }

}
