package com.study.week8.config.shardingjbdc.myslicealg;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;


/**
 * 数据库或表分片策略为 standard 时，必须自定义分片算法，需要实现下面两个接口 并提供空参构造
 * <p>
 * 实现 PreciseShardingAlgorithm 接口 精确分片算法类名称 必选
 * 实现 RangeShardingAlgorithm 接口   范围分片算法类名称 可选 如果不实现该接口 SQL中的BETWEEN AND将按照全库路由处理
 */
@Slf4j
public final class MyStandardStrategyShardingAlg implements PreciseShardingAlgorithm<Long>, RangeShardingAlgorithm<Long> {

    public MyStandardStrategyShardingAlg() {
    }

    /**
     * PreciseShardingAlgorithm接口方法实现
     *
     * @param availableTargetNames 所有数据源：[ds0, ds1, ds2...]
     * @param shardingValue        查询参数
     * @return 执行操作的数据源
     */
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {
        log.info("精确 availableTargetNames={}  shardingValue={}", availableTargetNames, shardingValue.toString());
        List<String> dataSources = new ArrayList<>(availableTargetNames);
        int size = 2 * 16;  //2个库 每个库16张表
        Long value = shardingValue.getValue();
        if (value % size >= 0 && value % size <= 15) {
            return dataSources.get(0);
        } else {
            return dataSources.get(1);
        }
    }


    /**
     * RangeShardingAlgorithmd接口方法实现
     *
     * @param availableTargetNames 所有数据源：[ds0, ds1, ds2]
     * @param shardingValue        查询参数：RangeShardingValue(logicTableName=t_user, columnName=age, valueRange=[5‥+∞))
     * @return 执行操作的数据源
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Long> shardingValue) {
        log.info("范围： availableTargetNames={}  shardingValue={}", availableTargetNames, shardingValue.toString());
        //TODO 范围查询查询所有
        Collection<String> result = new LinkedHashSet<>(availableTargetNames.size());
        result.addAll(availableTargetNames);
        return result;
    }

}
