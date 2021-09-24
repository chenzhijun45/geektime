package com.study.week8.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderResponse implements Serializable {
    private static final long serialVersionUID = -4495310714743261016L;

    //订单id
    private String orderId;

    //子订单id
    private String subOrderId;

    //是否主订单
    private Integer isMain;

    //下单用户id
    private Long userId;

    //用户手机号
    private String phone;

    //实际订单金额
    private BigDecimal actualAmount;

    //下单时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date orderTime;

    List<OrderDetailResponse> orderDetails;

    @Data
    public static class OrderDetailResponse implements Serializable{
        private static final long serialVersionUID = -1763746318726413201L;

        //商品名称
        private String goodsName;
        //sku数量
        private Integer skuNum;
        //计量单位
        private String unit;
        //sku价格
        private BigDecimal skuPrice;
        //sku总价
        private BigDecimal skuPriceTotal;
    }

}
