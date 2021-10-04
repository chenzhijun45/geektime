package com.example.dubbocommon.order.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Order implements Serializable {
    private static final long serialVersionUID = -5306702957776461819L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderId;

    /**
     * 发起交易用户id
     */
    private Long userId;

    /**
     * 交易数量 1：人民币数量  2：美元数量
     */
    private BigDecimal amount;

    /**
     * 本次兑换汇率（美元兑换人民币汇率）
     */
    private BigDecimal ratio;

    /**
     * 交易类型 1：人民币兑换美元  2：美元兑换人民币
     */
    private Integer orderType;

    /**
     * 订单状态 1：成功  2：失败  3：待确认
     */
    private Integer status;

    private Date createTime;
    private Date modifyTime;
}
