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
 * 表分片策略为 standard 时，必须自定义分片算法，需要实现下面两个类 并提供空参构造
 * 注意：如果数据库分片和表分片的策略都是 standard 那么，如果都是使用一个 UserStandardShardingAlg 会先后执行两边
 * <p>
 * 实现 PreciseShardingAlgorithm 接口 精确分片算法类名称 必选
 * 实现 RangeShardingAlgorithm 接口   范围分片算法类名称 可选 如果不实现该接口 SQL中的BETWEEN AND将按照全库路由处理
 */
@Slf4j
public final class MyTableStandardShardingAlg implements PreciseShardingAlgorithm<Byte>, RangeShardingAlgorithm<Byte> {

    public MyTableStandardShardingAlg() {
    }

    /**
     * PreciseShardingAlgorithm接口方法实现
     *
     * @param availableTargetNames 所有数据源：[ds0, ds1, ds2...]
     * @param shardingValue        查询参数
     * @return 执行操作的数据源
     */
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Byte> shardingValue) {
        log.info("精确： availableTargetNames={}  shardingValue={}", availableTargetNames, shardingValue.toString());
        List<String> tables = new ArrayList<>(availableTargetNames);
        Byte value = shardingValue.getValue();
        /*if (value % 3 == 0) {
            return tables.get(0);
        } else if (value % 3 == 1) {
            return tables.get(1);
        } else {
            return tables.get(2);
        }*/
        if (value % 2 == 0) {
            return tables.get(0);
        } else {
            return tables.get(1);
        }
    }


    /**
     * RangeShardingAlgorithmd接口方法实现
     *
     * @param availableTargetNames 所有数据表：[t_order0, t_order1, t_order2...]
     * @param shardingValue        查询参数
     * @return 执行操作的数据源
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Byte> shardingValue) {
        log.info("范围： availableTargetNames={}  shardingValue={}", availableTargetNames, shardingValue.toString());
        Collection<String> result = new LinkedHashSet<>(availableTargetNames.size());
        result.addAll(availableTargetNames);
        return result;
    }

}
