package com.study.week8.controller.request;

import lombok.Data;

import java.util.Date;

@Data
public class OrderQueryRequest extends BaseRequest {
    private static final long serialVersionUID = 8308024152893887755L;

    private Long userId;
    private String orderId;
    private Date startOrderTime;
    private Date endOrderTime;
}
