package com.example.dynamicdatasource.config.datasource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import static com.example.dynamicdatasource.config.datasource.DatasourceConfigWithWeight.WRITE;
import static com.example.dynamicdatasource.config.datasource.DatasourceConfigWithWeight.weightMap;

/**
 * 动态获取数据源实现类 支持权重
 * DynamicDatasourceWithWeight + DatasourceConfigWithWeight = 支持权重
 */
public class DynamicDatasourceWithWeight extends AbstractRoutingDataSource {

    //读数据源个数
    private final int readSize;

    /**
     * 获取读数据源的策略
     */
    @Value("${read.dataSource.poll.pattern}")
    private int pollPattern;

    public DynamicDatasourceWithWeight(DataSource masterDatasource, Map<Object, Object> map) {
        //读数据源数量 = 总数据源数量 - 1 注意：如果有多个写数据源 那么这里就不是减1了
        this.readSize = map.size() - 1;
        //设置默认数据源
        super.setDefaultTargetDataSource(masterDatasource);
        //设置数据源
        super.setTargetDataSources(map);
        //afterPropertiesSet()方法调用的作用是将targetDataSources的属性写入resolvedDataSources中
        super.afterPropertiesSet();
    }


    @Override
    protected Object determineCurrentLookupKey() {
        DatasourceEnum datasourceEnum = DynamicDataSourceHolder.getDataSource();

        if (datasourceEnum == null || datasourceEnum == DatasourceEnum.WRITE || readSize <= 0) {
            //未设置数据源或者获取的是写数据源或者读数据源个数为0 直接返回写数据源
            return WRITE;
        }

        //根据负载均衡策略决定使用哪一个读数据源
        int index = getReadDataSource();
        return getReadDataSource(index);
    }


    private AtomicLong pollingCount = new AtomicLong(0);

    /**
     * 决定使用哪个读数据源
     */
    private int getReadDataSource() {
        int index;
        if (pollPattern == 0) {
            //随机
            index = ThreadLocalRandom.current().nextInt(0, readSize);
        } else if (pollPattern == 1 || pollPattern == 2) {
            //轮询 或者 权重  获取下标是一样的
            long size = pollingCount.incrementAndGet();
            if (size >= Long.MAX_VALUE) {
                //加锁避免高并发情况下 重复将count设置为0
                ReentrantLock lock = new ReentrantLock();
                try {
                    lock.lock();
                    if (size >= Long.MAX_VALUE) {
                        pollingCount.set(0);
                    }
                } finally {
                    lock.unlock();
                }
            }
            if (pollPattern == 1) {
                //轮询
                index = (int) (size % readSize);
            } else {
                //权重
                index = (int) (size % weightList.size());
            }
        } else {
            //TODO 其他 待定
            index = 0;
        }
        return index;
    }

    private String getReadDataSource(int index) {
        if (pollPattern == 0 || pollPattern == 1) {
            return pollingCountAndRandomList.get(index);
        } else if (pollPattern == 2) {
            return weightList.get(index);
        } else {
            return pollingCountAndRandomList.get(0);
        }
    }

    //权重 集合元素为各个读数据源名称 权重占比越高 数据源名称越多
    private static final List<String> weightList = new ArrayList<>();
    //轮询或者随机 集合元素为各个读数据源名称
    private static final List<String> pollingCountAndRandomList = new ArrayList<>();

    static {
        //只需要配置一次
        Set<Map.Entry<String, Integer>> entries = weightMap.entrySet();
        for (Map.Entry<String, Integer> entry : entries) {
            pollingCountAndRandomList.add(entry.getKey());
            for (int i = 0; i < entry.getValue(); i++) {
                weightList.add(entry.getKey());
            }
        }
    }

}
