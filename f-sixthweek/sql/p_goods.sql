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

 Date: 12/09/2021 14:34:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for p_goods
-- ----------------------------
DROP TABLE IF EXISTS `p_goods`;
CREATE TABLE `p_goods`  (
  `id` bigint(11) NOT NULL COMMENT '主键id',
  `goods_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品名称',
  `goods_code` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品编码',
  `goods_type` tinyint(4) NOT NULL COMMENT '商品类型（0普通商品 1虚拟商品 2待定...）',
  `goods_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品描述(简介)',
  `goods_main_pic` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品主图',
  `goods_detail_pic` varchar(700) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品详情图(多张英文逗号分隔开)',
  `sku_min_price` decimal(10, 2) UNSIGNED NOT NULL COMMENT '最小sku价格',
  `merchant_code` char(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品所属商户编码',
  `monthly_sales` int(11) NULL DEFAULT NULL COMMENT '商品月销量',
  `total_sales` int(11) NULL DEFAULT NULL COMMENT '商品总销量',
  `approval_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '商品审核状态(0审核中 1审核成功 2审核失败(驳回) 默认0)',
  `sale_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '商品上下架状态（0未上架 1上架 默认0）',
  `is_del` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是 默认0）',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '最近修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `goods_code`(`goods_code`) USING BTREE COMMENT '商品编码唯一索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of p_goods
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
