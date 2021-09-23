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

 Date: 12/09/2021 14:35:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for p_goods_stock
-- ----------------------------
DROP TABLE IF EXISTS `p_goods_stock`;
CREATE TABLE `p_goods_stock`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `goods_code` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品编码',
  `goods_sku_code` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品sku编码',
  `available_num` int(11) UNSIGNED NOT NULL COMMENT '可用数量',
  `frozen_num` int(11) UNSIGNED NOT NULL COMMENT '冻结数量',
  `total_num` int(11) UNSIGNED NOT NULL COMMENT '总数量=可用+冻结',
  `warn_num` int(11) UNSIGNED NOT NULL COMMENT '警戒线数量,低于则通知商户商品即将售罄',
  `is_del` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是 默认0)',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `modity_time` datetime(0) NULL DEFAULT NULL COMMENT '最近修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `goods_sku_code`(`goods_sku_code`) USING BTREE COMMENT '商品sku唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品库存表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of p_goods_stock
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
