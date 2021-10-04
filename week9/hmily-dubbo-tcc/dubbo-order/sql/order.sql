/*
 Navicat Premium Data Transfer

 Source Server         : mysql3306
 Source Server Type    : MySQL
 Source Server Version : 50729
 Source Host           : localhost:3306
 Source Schema         : order

 Target Server Type    : MySQL
 Target Server Version : 50729
 File Encoding         : 65001

 Date: 04/10/2021 16:18:51
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `amount` decimal(10, 2) NOT NULL COMMENT '交易数量 1：人民币数量  2：美元数量',
  `ratio` decimal(4, 2) NOT NULL COMMENT '本次兑换汇率（美元兑换人民币汇率）',
  `order_type` tinyint(4) NOT NULL COMMENT '交易类型 1：人民币兑换美元  2：美元兑换人民币',
  `status` tinyint(4) NOT NULL COMMENT '订单状态 1：成功  2：失败  3：待确认',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `modify_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
