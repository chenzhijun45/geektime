package com.study.week8.exception;

import com.study.week8.controller.response.R;
import lombok.Getter;

@Getter
public class MyException extends RuntimeException {
    private static final long serialVersionUID = 2376781313022479005L;

    private final String msg;
    private final int code;

    public MyException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public MyException(R.ResultCode resultCode) {
        super(resultCode.getMsg());
        this.msg = resultCode.getMsg();
        this.code = resultCode.getCode();
    }

}
