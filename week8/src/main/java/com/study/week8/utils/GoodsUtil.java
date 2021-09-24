package com.study.week8.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 模拟生产成随机商品
 */
public class GoodsUtil {

    public static Goods getGoods() {
        return goodsList.get(new Random().nextInt(10));
    }

    private static final List<Goods> goodsList = new ArrayList<>();

    static {
        /**
         * 模拟10件商品
         */
        goodsList.add(new Goods("云南白药牙膏", "yunnanbaiyaoyagao", "yunnanbaiyaoyagao-skuCode", new BigDecimal("26.7"), "盒"));
        goodsList.add(new Goods("华为Mate40Pro", "huaweimate40pro", "huaweimate40pro-skuCode", new BigDecimal("6999"), "台"));
        goodsList.add(new Goods("夏天凉快T恤/白色", "xaitianliangkuaitxu", "xaitianliangkuaitxu-skuCode", new BigDecimal("35.5"), "件"));
        goodsList.add(new Goods("小天鹅洗衣机", "xiaotianexiyiji", "xiaotianexiyiji-skuCode", new BigDecimal("999"), "个"));
        goodsList.add(new Goods("良品铺子零食大礼包", "laingpinpuziliangslb", "laingpinpuziliangslb-skuCode", new BigDecimal("135.35"), "袋"));
        goodsList.add(new Goods("任天堂Switch/国行版", "rentiantangswitch", "rentiantangswitch-skuCode", new BigDecimal("2000"), "个"));
        goodsList.add(new Goods("超软毛绒娃娃", "chaoruanmaorongwawa", "chaoruanmaorongwawa-skuCode", new BigDecimal("99"), "个"));
        goodsList.add(new Goods("龙泉宝剑", "lqbj", "lqbj-skuCode", new BigDecimal("3000"), "把"));
        goodsList.add(new Goods("唐横刀", "tanghengdao", "tanghengdao-skuCode", new BigDecimal("2135"), "把"));
        goodsList.add(new Goods("步枪/AK47", "ak47", "ak47-skuCode", new BigDecimal("654"), "把"));
    }

    @Data
    @AllArgsConstructor
    public static class Goods {
        private String goodsName;
        private String goodsCode;
        private String goodsSkuCode;
        private BigDecimal skuPrice;
        private String unit;
    }

}
