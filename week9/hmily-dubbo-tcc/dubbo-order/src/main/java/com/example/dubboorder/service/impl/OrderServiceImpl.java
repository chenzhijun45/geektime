package com.example.dubboorder.service.impl;

import com.example.dubbocommon.account.api.AccountService;
import com.example.dubbocommon.account.dto.AccountDTO;
import com.example.dubbocommon.order.dto.OrderDTO;
import com.example.dubbocommon.order.entity.Order;
import com.example.dubbocommon.order.mapper.OrderMapper;
import com.example.dubboorder.service.OrderService;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrderServiceImpl implements OrderService {

    private static final BigDecimal RATIO = new BigDecimal("6.45");

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AccountService accountService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @HmilyTCC(confirmMethod = "orderStatusConfirm", cancelMethod = "orderStatusCancel")
    public boolean confirm(OrderDTO orderDTO) {
        /**
         * 生成订单
         */
        Order order = new Order();
        String orderId = UUID.randomUUID().toString().replace("-", "");
        order.setOrderId(orderId);
        orderDTO.setOrderId(orderId);
        order.setAmount(orderDTO.getAmount());
        order.setOrderType(orderDTO.getOrderType());
        order.setUserId(orderDTO.getUserId());
        order.setRatio(RATIO);
        order.setStatus(3);
        order.setCreateTime(new Date());
        order.setModifyTime(new Date());
        int save = orderMapper.save(order);

        /**
         * 修改账户余额
         */
        if (save == 1) {
            //人民币兑换美元：人民币账户余额减少
            AccountDTO accountDTO = new AccountDTO();
            accountDTO.setAmount(orderDTO.getAmount());
            accountDTO.setUserId(orderDTO.getUserId());
            accountDTO.setAccountType(1);
            boolean b = accountService.decrAccount(accountDTO);
            System.out.println("人民币 account decr： " + b);

            //人民币兑换美元：美元账户余额增加
            accountDTO.setAccountType(2);
            BigDecimal incrAmount = orderDTO.getAmount().divide(RATIO, 2, RoundingMode.DOWN);
            accountDTO.setAmount(incrAmount);
            boolean b1 = accountService.incrAccount(accountDTO);
            System.out.println("美元 account incr：" + b1);

            return true;
        } else {
            System.out.println("订单生成失败");
            throw new RuntimeException("订单生成失败：" + save);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @HmilyTCC(confirmMethod = "orderStatusConfirm", cancelMethod = "orderStatusCancel")
    public boolean confirmException(OrderDTO orderDTO) {
        /**
         * 生成订单
         */
        Order order = new Order();
        String orderId = UUID.randomUUID().toString().replace("-", "");
        order.setOrderId(orderId);
        orderDTO.setOrderId(orderId);
        order.setAmount(orderDTO.getAmount());
        order.setOrderType(orderDTO.getOrderType());
        order.setUserId(orderDTO.getUserId());
        order.setRatio(RATIO);
        order.setStatus(3);
        order.setCreateTime(new Date());
        order.setModifyTime(new Date());

        System.out.println("order=" + order);

        int save = orderMapper.save(order);

        /**
         * 修改账户余额
         */
        if (save == 1) {
            //人民币兑换美元：人民币账户余额减少
            AccountDTO accountDTO = new AccountDTO();
            accountDTO.setAmount(orderDTO.getAmount());
            accountDTO.setUserId(orderDTO.getUserId());
            accountDTO.setAccountType(1);
            boolean b = accountService.decrAccount(accountDTO);
            System.out.println("人民币 account decr = " + b);

            //人民币兑换美元：美元账户余额增加
            accountDTO.setAccountType(2);
            BigDecimal incrAmount = orderDTO.getAmount().divide(RATIO, 2, RoundingMode.DOWN);
            accountDTO.setAmount(incrAmount);
            boolean b1 = accountService.incrAccountException(accountDTO);
            System.out.println("美元 account incr = " + b1);

            return true;
        } else {
            System.out.println("订单生成失败");
            throw new RuntimeException("订单生成失败：" + save);
        }
    }

    /**
     * The Cancel count
     */
    private static AtomicInteger cancelCount = new AtomicInteger(0);

    /**
     * The Confirm count
     */
    private static AtomicInteger confirmCount = new AtomicInteger(0);

    @Transactional(rollbackFor = Exception.class)
    public boolean orderStatusConfirm(OrderDTO orderDTO) {
        System.out.println("============dubbo tcc 执行确认订单状态接口===============");
        System.out.println("orderDTO=" + orderDTO);
        int count = orderMapper.confirmOrderStatus(orderDTO);
        System.out.println("orderStatusConfirm count = " + count);
        final int i = confirmCount.incrementAndGet();
        System.out.println("调用了order status confirm " + i + " 次");
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean orderStatusCancel(OrderDTO orderDTO) {
        System.out.println("============dubbo tcc 执行取消订单状态接口===============");
        int count = orderMapper.cancelOrderStatus(orderDTO);
        System.out.println("orderStatusCancel count = " + count);
        final int i = cancelCount.incrementAndGet();
        System.out.println("调用了order status cancel " + i + " 次");
        return true;
    }

}
