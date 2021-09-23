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

 Date: 12/09/2021 14:35:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for p_goods_sku
-- ----------------------------
DROP TABLE IF EXISTS `p_goods_sku`;
CREATE TABLE `p_goods_sku`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `goods_code` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品编码',
  `sku_code` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品sku编码',
  `sku_pic` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品sku图片',
  `specifications` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品sku规格(例如 大包)',
  `variety` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品sku品种(例如 香辣味)',
  `cost_price` decimal(10, 2) UNSIGNED NOT NULL COMMENT '成本价',
  `sale_price` decimal(10, 2) UNSIGNED NOT NULL COMMENT '销售价',
  `marker_price` decimal(10, 2) NOT NULL COMMENT '市场价',
  `sale_status` tinyint(4) NOT NULL COMMENT '商品sku上下架状态（0未上架 1上架 默认0）',
  `is_del` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除(0否 1是)',
  `creare_time` datetime(0) NOT NULL COMMENT '创建时间',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '最近修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `sku_code`(`sku_code`) USING BTREE COMMENT '商品sku唯一索引',
  INDEX `goods_code`(`goods_code`) USING BTREE COMMENT '商品编码普通索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品sku信息表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
