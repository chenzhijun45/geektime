package com.study.week8.controller.response;

import lombok.Data;
import lombok.Getter;

/**
 * 统一返回值
 */
@Data
public class R<T> {

    private int code;
    private String msg;
    private T data;

    private R() {
    }

    private R(T data) {
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    private R(ResultCode resultCode) {
        this.data = (T) resultCode.getMsg();
    }

    @SuppressWarnings("unchecked")
    private R(String msg) {
        this.data = (T) msg;
    }

    public static <T> R<T> success(T data) {
        R<T> r = new R<>(data);
        r.setCode(200);
        r.setMsg("OK");
        return r;
    }

    public static <T> R<T> success(String msg) {
        R<T> r = new R<T>(msg);
        r.setCode(200);
        r.setMsg("OK");
        return r;
    }

    public static <T> R<T> success(ResultCode resultCode) {
        R<T> r = new R<T>(resultCode);
        r.setCode(200);
        r.setMsg("OK");
        return r;
    }


    public static <T> R<T> fail(String msg) {
        R<T> r = new R<T>(msg);
        r.setCode(500);
        r.setMsg("FAIL");
        return r;
    }

    public static <T> R<T> fail(ResultCode resultCode) {
        R<T> r = new R<T>(resultCode);
        r.setCode(resultCode.code);
        r.setMsg("FAIL");
        return r;
    }

    public static <T> R<T> fail(String msg, int code) {
        R<T> r = new R<T>(msg);
        r.setCode(code);
        r.setMsg("FAIL");
        return r;
    }

    public static <T> R<T> fail(T data) {
        R<T> r = new R<T>(data);
        r.setCode(500);
        r.setMsg("FAIL");
        return r;
    }


    @Getter
    public enum ResultCode {

        SUCCESS("操作成功", 200),
        FAIL("操作失败", 500),
        DATA_NOT_EXIST("数据不存在", 10000),
        PARAMS_NOT_NULL("请求参数不能为空", 10001),
        DO_NOT_REPEAT("请勿重复操作", 10003);

        private String msg;
        private int code;

        ResultCode(String msg, int code) {
            this.msg = msg;
            this.code = code;
        }
    }

}
