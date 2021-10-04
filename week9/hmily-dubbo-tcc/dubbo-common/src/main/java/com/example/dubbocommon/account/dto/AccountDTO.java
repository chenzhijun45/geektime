package com.example.dubbocommon.account.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AccountDTO implements Serializable {
    private static final long serialVersionUID = -4535165647332565669L;

    private Long userId;

    /**
     * 账户类型 1：人民币账户 2：美元账户
     */
    private Integer accountType;

    private BigDecimal amount;
}
