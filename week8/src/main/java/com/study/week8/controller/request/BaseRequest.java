package com.study.week8.controller.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseRequest implements Serializable {
    private static final long serialVersionUID = 7799946474414126996L;

    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
