package com.study.week8.config.shardingjbdc.myslicealg;

import org.apache.shardingsphere.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.hint.HintShardingValue;

import java.util.Collection;

/**
 * 分片策略为 Hint 时，必须自定义分片算法，实现 HintShardingAlgorithm 接口 并提供空参构造
 */
public final class MyHintShardingAlg implements HintShardingAlgorithm<Byte> {

    public MyHintShardingAlg() {
    }

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, HintShardingValue<Byte> shardingValue) {
        //TODO
        return null;
    }

}
