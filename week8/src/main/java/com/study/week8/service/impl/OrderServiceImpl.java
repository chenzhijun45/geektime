package com.study.week8.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.week8.controller.request.OrderQueryRequest;
import com.study.week8.controller.response.OrderResponse;
import com.study.week8.controller.response.PageResult;
import com.study.week8.controller.response.R;
import com.study.week8.dao.mapper.OrderMapper;
import com.study.week8.dao.model.Order;
import com.study.week8.dao.model.OrderDetail;
import com.study.week8.service.OrderDetailService;
import com.study.week8.service.OrderService;
import com.study.week8.utils.GoodsUtil;
import com.study.week8.utils.IdUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderDetailService orderDetailService;


    /**
     * 模拟新增订单
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public R<OrderResponse> createOrder() {
        String orderId = IdUtil.generatorOrderId();
        Long userId = IdUtil.generatorUserId();
        Date time = new Date();

        //说明：原本订单先根据商户（店铺）拆单，即t_order表的sub_order_id分别代表不同的商户购买商品订单
        //     再根据商品拆单 即t_order_detail表的sub_order_id分别表示购买的不同的商品
        //t_order表和t_order_detail表的关系：t_order表的sub_order_id是t_order_detail表的order_id
        //这里为了简便 t_order表就懒得按照商户拆单了 所以order_id和sub_order_id是一样的

        //构建订单表
        Order order = new Order();
        order.setOrderId(orderId);
        order.setSubOrderId(orderId);
        order.setUserId(userId);
        order.setPhone("18712341234");
        order.setIsMain(1);
        order.setIsDel(0);
        order.setReceiver("czj");
        order.setReceiverPhone("18712341234");

        order.setReceiverProvince("广东省");
        order.setReceiverCity("深圳市");
        order.setReceiverDistrict("西乡街道");
        order.setReceiverAddress("大益广场");

        order.setOrderType(0);
        //支付状态(0待支付 1待发货 2待收货 3已完成 4已取消 5已退款 6待定...)
        order.setPayStatus(3);
        order.setPayType(new Random().nextInt(3));
        order.setCreateTime(time);//创建时间
        order.setOrderTime(time);//下单时间
        order.setPayTime(time);//支付时间
        order.setDeliveryTime(time);//发货时间
        order.setCompleteTime(time);//完成时间
        order.setModifyTime(time);//最近修改时间

        //计算订单总金额
        BigDecimal orderAmount = BigDecimal.ZERO;
        int totalGoodsNum = 0;

        //每笔订单随机购买1-3件商品
        List<OrderDetail> orderDetailList = new ArrayList<>(4);
        int goodsNum = new Random().nextInt(3) + 1;
        AtomicInteger atomicInteger = new AtomicInteger(1);
        for (int i = 0; i < goodsNum; i++) {
            //构建订单详情表
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setUserId(userId);
            orderDetail.setOrderId(orderId);
            orderDetail.setSubOrderId(orderId + "_" + atomicInteger.getAndIncrement());
            GoodsUtil.Goods goods = GoodsUtil.getGoods();
            orderDetail.setGoodsCode(goods.getGoodsCode());
            orderDetail.setGoodsName(goods.getGoodsName());
            orderDetail.setGoodsSkuCode(goods.getGoodsSkuCode());
            orderDetail.setMarketPrice(goods.getSkuPrice());
            orderDetail.setSkuPrice(goods.getSkuPrice());
            //商品购买数量：随机1-10件
            int skuNum = new Random().nextInt(10) + 1;
            orderDetail.setSkuNum(skuNum);
            BigDecimal skuTotalPrice = goods.getSkuPrice().multiply(BigDecimal.valueOf(skuNum));
            orderDetail.setSkuPriceTotal(skuTotalPrice);
            orderDetail.setUnit(goods.getUnit());
            orderDetail.setIsDel(0);
            orderDetail.setCreateTime(time);
            orderDetail.setModifyTime(time);
            orderDetailList.add(orderDetail);

            orderAmount = orderAmount.add(skuTotalPrice);
            totalGoodsNum = totalGoodsNum + skuNum;
        }

        order.setGoodsNum(totalGoodsNum);
        order.setOrderAmount(orderAmount);
        order.setActualAmount(orderAmount);

        boolean save = this.save(order);
        if (!save) {
            return R.fail("新增订单失败");
        }

        boolean b = orderDetailService.saveBatch(orderDetailList);
        if (!b) {
            throw new RuntimeException("新增订单详情失败");
        }

        OrderResponse orderResponse = generateOrderResponse(order, orderDetailList);
        return R.success(orderResponse);
    }


    /**
     * 查询订单详情
     *
     * @param orderId 订单id
     */
    @Override
    public R<OrderResponse> queryOrderInfo(String orderId, Long userId) {
        Order order = baseMapper.selectOne(
                new QueryWrapper<Order>().lambda()
                        .eq(Order::getOrderId, orderId)
                        .eq(userId != null, Order::getUserId, userId)
        );
        if (order == null || order.getIsDel() == 1) {
            return R.fail(R.ResultCode.DATA_NOT_EXIST);
        }
        List<OrderDetail> orderDetails = orderDetailService.list(
                new QueryWrapper<OrderDetail>().lambda()
                        .eq(OrderDetail::getOrderId, order.getSubOrderId())
                        .eq(userId != null, OrderDetail::getUserId, userId)
        );
        if (orderDetails == null || orderDetails.size() == 0) {
            return R.fail(R.ResultCode.DATA_NOT_EXIST);
        }

        OrderResponse orderResponse = generateOrderResponse(order, orderDetails);
        return R.success(orderResponse);
    }


    /**
     * 订单信息分页查询
     */
    @Override
    public R<PageResult<OrderResponse>> queryOrderPage(OrderQueryRequest request) {
        //重置pageNo 作为分页sql中 limit a, b 的参数a 参数b则是pageSize
        int realPageNo = request.getPageNo();
        int pageNo = (request.getPageNo() - 1) * request.getPageSize();
        request.setPageNo(pageNo);

        List<Order> orders = baseMapper.queryPage(request);
        Integer total = baseMapper.queryCount(request);

        List<OrderResponse> orderResponses = new ArrayList<>(request.getPageSize());
        orders.forEach(o -> {
            List<OrderDetail> orderDetails = orderDetailService.list(new QueryWrapper<OrderDetail>().lambda()
                    .eq(OrderDetail::getOrderId, o.getSubOrderId())
                    .eq(OrderDetail::getUserId, o.getUserId())
            );
            orderResponses.add(generateOrderResponse(o, orderDetails));
        });

        PageResult<OrderResponse> result = new PageResult<OrderResponse>(
                realPageNo,
                request.getPageSize(),
                total,
                orderResponses
        );
        return R.success(result);
    }


    /**
     * 修改订单信息 仅能修改收货人
     */
    @Override
    public R<String> updateOrder(String orderId, String receiver) {
        Order order = super.getOne(new QueryWrapper<Order>().lambda().eq(Order::getOrderId, orderId));
        if (order == null || order.getIsDel() == 1) {
            return R.fail(R.ResultCode.DATA_NOT_EXIST);
        }

        Order updateOrder = new Order();
        updateOrder.setReceiver(receiver);
        updateOrder.setModifyTime(new Date());
        //注意：不能使用 updateById() 因为分片键不允许修改
        boolean update = super.update(
                updateOrder,
                new UpdateWrapper<Order>().lambda()
                        .eq(Order::getOrderId, orderId)
                        //这里加上userId(分片列)更新的话 可以直接定位到具体的实际表 否则会更新所有表
                        .eq(Order::getUserId, order.getUserId())
        );
        if (update) {
            return R.success(R.ResultCode.SUCCESS);
        }
        return R.fail("订单信息更新失败");
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public R<String> deleteOrder(String orderId) {
        Order order = super.getOne(new QueryWrapper<Order>().lambda().eq(Order::getOrderId, orderId));
        if (order == null) {
            return R.fail(R.ResultCode.DATA_NOT_EXIST);
        }

        if (order.getIsDel() == 1) {
            return R.fail(R.ResultCode.DO_NOT_REPEAT);
        }

        Order updateOrder = new Order();
        updateOrder.setIsDel(1);
        updateOrder.setModifyTime(new Date());
        boolean b = this.update(
                updateOrder,
                new UpdateWrapper<Order>().lambda()
                        .eq(Order::getOrderId, orderId)
                        //这里加上userId(分片列)更新的话 可以直接定位到具体的实际表 否则会更新所有表
                        .eq(Order::getUserId, order.getUserId())
        );
        if (b) {
            //删除订单详情
            OrderDetail orderDetailUpdate = new OrderDetail();
            orderDetailUpdate.setIsDel(1);
            orderDetailUpdate.setModifyTime(new Date());

            orderDetailService.update(orderDetailUpdate,
                    new QueryWrapper<OrderDetail>().lambda()
                            .eq(OrderDetail::getOrderId, order.getSubOrderId())
                            .eq(OrderDetail::getUserId, order.getUserId())
            );

            return R.success(R.ResultCode.SUCCESS);
        }
        return R.fail(R.ResultCode.FAIL);
    }


    private OrderResponse generateOrderResponse(Order order, List<OrderDetail> orderDetailList) {
        OrderResponse orderResponse = new OrderResponse();
        BeanUtils.copyProperties(order, orderResponse);
        List<OrderResponse.OrderDetailResponse> detailResponses = orderDetailList.stream().map(x -> {
            OrderResponse.OrderDetailResponse detailResponse = new OrderResponse.OrderDetailResponse();
            BeanUtils.copyProperties(x, detailResponse);
            return detailResponse;
        }).collect(Collectors.toList());
        orderResponse.setOrderDetails(detailResponses);
        return orderResponse;
    }

}
