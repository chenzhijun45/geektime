/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50729
 Source Host           : localhost:3306
 Source Schema         : czj

 Target Server Type    : MySQL
 Target Server Version : 50729
 File Encoding         : 65001

 Date: 12/09/2021 14:35:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_order_info
-- ----------------------------
DROP TABLE IF EXISTS `t_order_info`;
CREATE TABLE `t_order_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `order_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单id',
  `sub_order_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '子订单id',
  `is_main` tinyint(4) NOT NULL COMMENT '是否主订单',
  `user_id` bigint(20) NOT NULL COMMENT '下单用户id',
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户手机号',
  `order_type` tinyint(4) NOT NULL COMMENT '订单类型(0商城订单 1待定...)',
  `goods_num` int(11) NOT NULL COMMENT '购买数量',
  `order_amount` decimal(10, 2) UNSIGNED NOT NULL COMMENT '订单金额',
  `actual_amount` decimal(10, 2) NOT NULL COMMENT '实际订单金额',
  `receiver` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收货人',
  `receiver_phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货人手机号',
  `receiver_province` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货人省份',
  `receiver_city` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货人城市',
  `receiver_district` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货人区域',
  `receiver_address` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货人详细地址',
  `is_del` tinyint(4) NOT NULL COMMENT '是否删除(0否 1是)',
  `pay_status` tinyint(4) NOT NULL COMMENT '支付状态(0待支付 1待发货 2待收货 3已完成 4已取消 5已退款 6待定...)',
  `pay_type` tinyint(4) NULL DEFAULT NULL COMMENT '支付类型(0支付宝 1微信 2银联)',
  `order_time` datetime(0) NOT NULL COMMENT '下单时间',
  `pay_time` datetime(0) NULL DEFAULT NULL COMMENT '支付时间',
  `delivery_time` datetime(0) NULL DEFAULT NULL COMMENT '发货时间',
  `complete_time` datetime(0) NULL DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '最近修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `sub_order_id`(`sub_order_id`) USING BTREE COMMENT '子订单唯一索引',
  INDEX `order_id`(`order_id`) USING BTREE COMMENT '订单id普通索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单表(按商户拆单)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_order_info
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
