package com.study.week8.dao.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("t_order_detail")
public class OrderDetail implements Serializable {
    private static final long serialVersionUID = -3797518689557855784L;

    //主键id
    @TableId(type = IdType.AUTO)
    private Long id;

    //订单id
    private String orderId;

    //子订单id
    private String subOrderId;

    //用户id
    private Long userId;

    //商品编码
    private String goodsCode;

    //商品名称
    private String goodsName;

    //商品sku编码
    private String goodsSkuCode;

    //sku数量
    private Integer skuNum;

    //计量单位
    private String unit;

    //市场价格
    private BigDecimal marketPrice;

    //sku价格
    private BigDecimal skuPrice;

    //sku总价
    private BigDecimal skuPriceTotal;

    //是否删除(0否 1是)
    private Integer isDel;

    //创建时间
    private Date createTime;

    //最近修改时间
    private Date modifyTime;

}
