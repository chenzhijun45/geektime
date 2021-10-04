package com.example.dubbocommon.order.mapper;

import com.example.dubbocommon.order.dto.OrderDTO;
import com.example.dubbocommon.order.entity.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

public interface OrderMapper {

    @Insert("insert into t_order (order_id, user_id, amount, ratio, order_type, status, create_time, modify_time)" +
            " values (#{orderId}, #{userId}, #{amount}, #{ratio}, #{orderType}, #{status}, #{createTime}, #{modifyTime})")
    int save(Order order);

    @Update("update t_order set status = 1 where user_id = #{userId} and order_id = #{orderId}")
    int confirmOrderStatus(OrderDTO orderDTO);

    @Update("update t_order set status = 2 where user_id = #{userId} and order_id = #{orderId}")
    int cancelOrderStatus(OrderDTO orderDTO);

}
