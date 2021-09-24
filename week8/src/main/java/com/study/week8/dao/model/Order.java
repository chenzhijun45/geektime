package com.study.week8.dao.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("t_order")
public class Order implements Serializable {
    private static final long serialVersionUID = -7529845870419129488L;

    //主键id
    @TableId(type = IdType.AUTO)
    private Long id;

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

    //订单类型(0商城订单 1待定...)
    private Integer orderType;

    //订单金额
    private BigDecimal orderAmount;

    //实际订单金额
    private BigDecimal actualAmount;

    //商品总数量
    private Integer goodsNum;

    //收货人
    private String receiver;

    //收货人手机号
    private String receiverPhone;

    //收货人省份
    private String receiverProvince;

    //收货人城市
    private String receiverCity;

    //收货人区域
    private String receiverDistrict;

    //收货人详细地址
    private String receiverAddress;

    //是否删除(0否 1是)
    private Integer isDel;

    //支付状态(0待支付 1待发货 2待收货 3已完成 4已取消 5已退款 6待定...)
    private Integer payStatus;

    //支付类型(0-支付宝 1-微信 2-银联)
    private Integer payType;

    //下单时间
    private Date orderTime;

    //支付时间
    private Date payTime;

    //发货时间
    private Date deliveryTime;

    //完成时间
    private Date completeTime;

    //创建时间
    private Date createTime;

    //最近修改时间
    private Date modifyTime;

}
