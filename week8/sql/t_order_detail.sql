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

 Date: 12/09/2021 14:35:24
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_order_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_order_detail`;
CREATE TABLE `t_order_detail`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id主键',
  `order_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单id',
  `sub_order_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '子订单id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `goods_code` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品编码',
  `goods_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品名称',
  `goods_sku_code` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品sku编码',
  `sku_num` int(11) UNSIGNED NOT NULL COMMENT 'sku数量',
  `unit` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '计量单位',
  `market_price` decimal(10, 2) UNSIGNED NOT NULL COMMENT '市场价格',
  `sku_price` decimal(10, 2) UNSIGNED NOT NULL COMMENT 'sku价格',
  `sku_price_total` decimal(10, 2) NOT NULL COMMENT 'sku总价',
  `is_del` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `goods_sku_code`(`goods_sku_code`) USING BTREE COMMENT '商品sku编码',
  INDEX `order_id`(`order_id`) USING BTREE COMMENT '订单id普通索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单详情表(按商品拆单)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_order_detail
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
