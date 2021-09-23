package com.study.week8.config.shardingjbdc.myslicealg;

import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import java.util.Collection;

/**
 * 分片策略为 complex 时，必须自定义分片算法，实现 ComplexKeysShardingAlgorithm 接口 并提供空参构造
 */
public final class MyComplexShardingAlg implements ComplexKeysShardingAlgorithm<Byte> {

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, ComplexKeysShardingValue<Byte> shardingValue) {
        //TODO
        return null;
    }

}
