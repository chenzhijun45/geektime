package com.example.dynamicdatasource.config.datasource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 动态获取数据源实现类
 */
public class DynamicDatasource extends AbstractRoutingDataSource {

    //读数据源个数
    private final int readSize;

    /**
     * 获取读数据源的策略
     */
    @Value("${read.dataSource.poll.pattern}")
    private int pollPattern;

    public DynamicDatasource(DataSource masterDatasource, Map<Object, Object> map) {
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
            return DatasourceEnum.WRITE.name();
        }

        //根据负载均衡策略决定使用哪一个读数据源
        int index = getReadDataSource();
        return DatasourceEnum.READ.name() + index;
    }


    private AtomicLong count = new AtomicLong(0);

    /**
     * 决定使用哪个读数据源
     */
    private int getReadDataSource() {
        int index;
        if (pollPattern == 0) {
            //随机
            index = ThreadLocalRandom.current().nextInt(0, readSize);
        } else if (pollPattern == 1) {
            //轮询
            long size = count.incrementAndGet();
            if (size >= Long.MAX_VALUE) {
                //加锁避免高并发情况下 重复将count设置为0
                ReentrantLock lock = new ReentrantLock();
                try {
                    lock.lock();
                    if (size >= Long.MAX_VALUE) {
                        count.set(0);
                    }
                } finally {
                    lock.unlock();
                }
            }
            index = (int) (size % readSize);
        } else {
            //TODO 权重 待定...
            index = 0;
        }
        return index;
    }

}
