package com.study.week8;

import com.study.week8.dao.model.Order;
import com.study.week8.dao.model.OrderDetail;
import com.study.week8.utils.IdUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

@MapperScan({"com.study.**.mapper"})
@SpringBootApplication
public class Week8Application {

    public static void main(String[] args) {
        SpringApplication.run(Week8Application.class, args);
    }


    /**
     * 累计插入数据条数：100w
     */
//    private static final int total = 1000000;
//
//
//    public static void main(String[] args) throws Exception {
//        /**
//         * 数据库连接
//         * 注意：需设置 rewriteBatchedStatements=true 才会开启批量插入操作 否则和单次插入没区别
//         */
//        Class.forName("com.mysql.cj.jdbc.Driver");
//        String url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true&serverTimezone=GMT%2B8";
//        String username = "root";
//        String pwd = "root";
//        Connection connection = DriverManager.getConnection(url, username, pwd);
//
//        //准备100w条数据
//        connection.setAutoCommit(false);
//        Order[] orders = new Order[total];
//        OrderDetail[] orderDetails = new OrderDetail[total];
//        for (int i = 0; i < total; i++) {
//
//            Order order = Order.builder()
//                    .orderId(IdUtil.generatorOrderId())
//                    .actualAmount()
//                    .isMain()
//                    .
//
//
//                    .isDel(0)
//                    .createTime()
//                    .payTime()
//                    .completeTime()
//                    .modifyTime()
//                    .build();
//
//
//        }
//
//        //执行批量插入
//        method05(connection, orderDetails);
//
//        //关闭连接
//        connection.close();
//    }
//
//
//    /**
//     * PreparedStatement批量插入 即 preparedStatement.addBatch() 然后 preparedStatement.executeBatch(); 事务自动提交
//     * 6260毫秒 6秒
//     */
//    public static void method02(Connection connection, TestData[] data) throws Exception {
//        String sql = "INSERT INTO t_test (name, age) values(?, ?)";
//        PreparedStatement preparedStatement = connection.prepareStatement(sql);
//
//        System.out.println("执行批量插入操作...");
//        long start = System.currentTimeMillis();
//        //方式一执行批量插入
//        for (int i = 0; i < total; i++) {
//            TestData obj = data[i];
//            preparedStatement.setString(1, obj.getName());
//            preparedStatement.setInt(2, obj.getAge());
//            preparedStatement.addBatch();
//        }
//        preparedStatement.executeBatch();
//
//        long end = System.currentTimeMillis();
//        System.out.println("耗时=" + (end - start) + "毫秒");
//    }
}
