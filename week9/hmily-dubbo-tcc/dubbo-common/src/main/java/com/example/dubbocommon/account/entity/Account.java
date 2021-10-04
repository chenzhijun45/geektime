package com.example.dubbocommon.account.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Account implements Serializable {
    private static final long serialVersionUID = -762618384045031761L;

    private Long id;

    private Long userId;

    /**
     * 总数量
     */
    private BigDecimal totalAmount;

    /**
     * 可用数量
     */
    private BigDecimal availableAmount;

    /**
     * 冻结数量
     */
    private BigDecimal frozenAmount;

    /**
     * 预增加数量
     */
    private BigDecimal preIncrAmount;

    /**
     * 账户类型 1：人民币账户 2：美元账户
     */
    private Integer accountType;

    private Date createTime;
    private Date modifyTime;

}
