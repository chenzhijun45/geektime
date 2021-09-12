[toc]

### 第六周：建表语句

#### 商品表

更多的应该还可添加是否包邮，折扣信息等相关信息，就不加了。

```java
CREATE TABLE `p_goods` (
  `id` bigint(11) NOT NULL COMMENT '主键id',
  `goods_name` varchar(64) NOT NULL COMMENT '商品名称',
  `goods_code` char(32) NOT NULL COMMENT '商品编码',
  `goods_type` tinyint(4) NOT NULL COMMENT '商品类型（0普通商品 1虚拟商品 2待定...）',
  `goods_desc` varchar(500) DEFAULT NULL COMMENT '商品描述(简介)',
  `goods_main_pic` varchar(400) DEFAULT NULL COMMENT '商品主图',
  `goods_detail_pic` varchar(700) DEFAULT NULL COMMENT '商品详情图(多张英文逗号分隔开)',
  `sku_min_price` decimal(10,2) unsigned NOT NULL COMMENT '最小sku价格',
  `merchant_code` char(10) NOT NULL COMMENT '商品所属商户编码',
  `monthly_sales` int(11) DEFAULT NULL COMMENT '商品月销量',
  `total_sales` int(11) DEFAULT NULL COMMENT '商品总销量',
  `approval_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '商品审核状态(0审核中 1审核成功 2审核失败(驳回) 默认0)',
  `sale_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '商品上下架状态（0未上架 1上架 默认0）',
  `is_del` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除（0否 1是 默认0）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '最近修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `goods_code` (`goods_code`) USING BTREE COMMENT '商品编码唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';
```



#### 商品sku信息表

```java
CREATE TABLE `p_goods_sku` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `goods_code` char(32) NOT NULL COMMENT '商品编码',
  `sku_code` char(32) NOT NULL COMMENT '商品sku编码',
  `sku_pic` varchar(500) NOT NULL COMMENT '商品sku图片',
  `specifications` varchar(64) NOT NULL COMMENT '商品sku规格(例如 大包)',
  `variety` varchar(32) NOT NULL COMMENT '商品sku品种(例如 香辣味)',
  `cost_price` decimal(10,2) unsigned NOT NULL COMMENT '成本价',
  `sale_price` decimal(10,2) unsigned NOT NULL COMMENT '销售价',
  `marker_price` decimal(10,2) NOT NULL COMMENT '市场价',
  `sale_status` tinyint(4) NOT NULL COMMENT '商品sku上下架状态（0未上架 1上架 默认0）',
  `is_del` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除(0否 1是)',
  `creare_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '最近修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sku_code` (`sku_code`) USING BTREE COMMENT '商品sku唯一索引',
  KEY `goods_code` (`goods_code`) USING BTREE COMMENT '商品编码普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品sku信息表';
```



#### 商品库存表

```java
CREATE TABLE `p_goods_stock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `goods_code` char(32) NOT NULL COMMENT '商品编码',
  `goods_sku_code` char(32) NOT NULL COMMENT '商品sku编码',
  `available_num` int(11) unsigned NOT NULL COMMENT '可用数量',
  `frozen_num` int(11) unsigned NOT NULL COMMENT '冻结数量',
  `total_num` int(11) unsigned NOT NULL COMMENT '总数量=可用+冻结',
  `warn_num` int(11) unsigned NOT NULL COMMENT '警戒线数量,低于则通知商户商品即将售罄',
  `is_del` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除(0否 1是 默认0)',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modity_time` datetime DEFAULT NULL COMMENT '最近修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `goods_sku_code` (`goods_sku_code`) USING BTREE COMMENT '商品sku唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品库存表';
```



#### 订单表

```java
CREATE TABLE `t_order_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `order_id` varchar(64) NOT NULL COMMENT '订单id',
  `sub_order_id` varchar(64) NOT NULL COMMENT '子订单id',
  `is_main` tinyint(4) NOT NULL COMMENT '是否主订单',
  `user_id` bigint(20) NOT NULL COMMENT '下单用户id',
  `phone` varchar(32) NOT NULL COMMENT '用户手机号',
  `order_type` tinyint(4) NOT NULL COMMENT '订单类型(0商城订单 1待定...)',
  `goods_num` int(11) NOT NULL COMMENT '购买数量',
  `ordder_amount` decimal(10,2) unsigned NOT NULL COMMENT '订单金额',
  `actual_amount` decimal(10,2) NOT NULL COMMENT '实际订单金额',
  `receiver` varchar(16) NOT NULL COMMENT '收货人',
  `receiver_phone` varchar(32) DEFAULT NULL COMMENT '收货人手机号',
  `receiver_province` varchar(16) DEFAULT NULL COMMENT '收货人省份',
  `receiverCity` varchar(16) DEFAULT NULL COMMENT '收货人城市',
  `receiverDistrict` varchar(16) DEFAULT NULL COMMENT '收货人区域',
  `receiver_address` varchar(64) DEFAULT NULL COMMENT '收货人详细地址',
  `is_del` tinyint(4) NOT NULL COMMENT '是否删除(0否 1是)',
  `pay_status` tinyint(4) NOT NULL COMMENT '支付状态(0待支付 1待发货 2待收货 3已完成 4已取消 5已退款 6待定...)',
  `pay_type` tinyint(4) DEFAULT NULL COMMENT '支付类型(0支付宝 1微信 2银联)',
  `order_time` datetime NOT NULL COMMENT '下单时间',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '最近修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sub_order_id` (`sub_order_id`) USING BTREE COMMENT '子订单唯一索引',
  KEY `order_id` (`order_id`) USING BTREE COMMENT '订单id普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表(按商户拆单)';
```



#### 订单详情表

```java
CREATE TABLE `t_order_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id主键',
  `order_id` varchar(64) NOT NULL COMMENT '订单id',
  `sub_order_id` varchar(64) NOT NULL COMMENT '子订单id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `goods_code` char(32) NOT NULL COMMENT '商品编码',
  `goods_name` varchar(32) NOT NULL COMMENT '商品名称',
  `goods_sku_code` char(32) NOT NULL COMMENT '商品sku编码',
  `sku_num` int(11) unsigned NOT NULL COMMENT 'sku数量',
  `unit` varchar(8) NOT NULL COMMENT '计量单位',
  `market_price` decimal(10,2) unsigned NOT NULL COMMENT '市场价格',
  `sku_price` decimal(10,2) unsigned NOT NULL COMMENT 'sku价格',
  `sku_price_total` decimal(10,2) NOT NULL COMMENT 'sku总价',
  `is_del` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除(0否 1是)',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `goods_sku_code` (`goods_sku_code`) USING BTREE COMMENT '商品sku编码',
  KEY `order_id` (`order_id`) USING BTREE COMMENT '订单id普通索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单详情表(按商品拆单)';
```



#### 用户表

```java
CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `nickname` varchar(64) NOT NULL COMMENT '用户昵称,默认为手机号脱敏',
  `login_name` varchar(32) NOT NULL COMMENT '登录名(手机号)',
  `password` varchar(255) NOT NULL COMMENT '登录密码',
  `phone` varchar(32) NOT NULL COMMENT '用户手机号',
  `head_img` varchar(500) DEFAULT NULL COMMENT '用户头像',
  `is_real_name` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否实名(0否 1是)',
  `is_frozen` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否冻结(冻结后不可登录 默认0)',
  `create_time` datetime NOT NULL COMMENT '创建时间(即注册时间)',
  `last_login_time` datetime NOT NULL COMMENT '最近一次登录时间',
  `modify_time` datetime DEFAULT NULL COMMENT '最近修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `login_name` (`login_name`) USING BTREE COMMENT '登录名唯一索引',
  UNIQUE KEY `user_id` (`user_id`) USING BTREE COMMENT 'userId唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```



#### 商户表

```java
待定，就不加了，应该还有个商户表，具体取决于业务系统。
```

