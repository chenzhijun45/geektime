/*
 Navicat Premium Data Transfer

 Source Server         : mysql3306
 Source Server Type    : MySQL
 Source Server Version : 50729
 Source Host           : localhost:3306
 Source Schema         : account

 Target Server Type    : MySQL
 Target Server Version : 50729
 File Encoding         : 65001

 Date: 04/10/2021 16:18:58
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_account
-- ----------------------------
DROP TABLE IF EXISTS `t_account`;
CREATE TABLE `t_account`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `total_amount` decimal(10, 2) UNSIGNED NOT NULL,
  `available_amount` decimal(10, 2) UNSIGNED NOT NULL,
  `frozen_amount` decimal(10, 2) UNSIGNED NOT NULL,
  `pre_incr_amount` decimal(10, 2) UNSIGNED NULL DEFAULT NULL COMMENT '预增加数量',
  `account_type` tinyint(4) NOT NULL COMMENT '账户类型 1：人民币账户 2：美元账户',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `modify_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_account
-- ----------------------------
INSERT INTO `t_account` VALUES (1, 1, 10000.00, 10000.00, 0.00, 0.00, 1, '2021-10-04 01:04:01', '2021-10-04 15:54:45');
INSERT INTO `t_account` VALUES (2, 1, 10000.00, 10000.00, 0.00, 0.00, 2, '2021-10-04 01:04:01', '2021-10-04 15:54:32');

SET FOREIGN_KEY_CHECKS = 1;
