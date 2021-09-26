package com.study.week8.controller;

import com.study.week8.controller.request.OrderQueryRequest;
import com.study.week8.controller.response.OrderResponse;
import com.study.week8.controller.response.PageResult;
import com.study.week8.controller.response.R;
import com.study.week8.service.OrderService;
import org.apache.shardingsphere.transaction.annotation.ShardingTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.apache.shardingsphere.transaction.core.TransactionTypeHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;


    //新增订单
    @PostMapping("addOrder")
    public R<OrderResponse> addOrder() {
        return orderService.createOrder();
    }

    //分页查询订单
    @GetMapping("queryList")
    public R<PageResult<OrderResponse>> queryList(OrderQueryRequest request) {
        return orderService.queryOrderPage(request);
    }

    //查询订单详情
    @GetMapping("queryOrderInfo")
    public R<OrderResponse> queryOrderInfo(String orderId,Long userId) {
        if (!StringUtils.hasLength(orderId)) {
            return R.fail(R.ResultCode.PARAMS_NOT_NULL);
        }
        return orderService.queryOrderInfo(orderId, userId);
    }

    //修改订单
    @PostMapping("updateOrder")
    public R<String> updateOrder(String orderId, String receiver) {
        if (!StringUtils.hasLength(orderId) || !StringUtils.hasLength(receiver)) {
            return R.fail(R.ResultCode.PARAMS_NOT_NULL);
        }
        return orderService.updateOrder(orderId, receiver);
    }

    //删除订单
    @PostMapping("deleteOrder")
    public R<String> deleteOrder(String orderId) {
        if (!StringUtils.hasLength(orderId)) {
            return R.fail(R.ResultCode.PARAMS_NOT_NULL);
        }
        return orderService.deleteOrder(orderId);
    }


    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     *分布式事务XA测试
     */
    @Transactional(rollbackFor = NullPointerException.class)
    @ShardingTransactionType(TransactionType.XA)
    @GetMapping("txTest")
    public void t() {
        //修改test库的t_order表
        TransactionType txType0 = jdbcTemplate.execute("UPDATE t_order SET order_type = 1 WHERE user_id = ?", (PreparedStatementCallback<TransactionType>) preparedStatement -> {
            //t_name表只有主键id和name两个字段
            preparedStatement.setLong(1, 2464);
            preparedStatement.executeUpdate();
            return TransactionTypeHolder.get();
        });

        System.out.println("txType0====>>>" + txType0);

        //修改test01库的t_order表
        TransactionType txType1 = jdbcTemplate.execute("UPDATE t_order SET order_type = 1 WHERE user_id = ?", (PreparedStatementCallback<TransactionType>) preparedStatement -> {
            preparedStatement.setLong(1, 2643);
            preparedStatement.executeUpdate();
            return TransactionTypeHolder.get();
        });

        System.out.println("txType1====>>>" + txType1);

        //抛出异常 分布式事务回滚 以上两个跨库操作均失败
        int a = 10 / 0;
    }

}
