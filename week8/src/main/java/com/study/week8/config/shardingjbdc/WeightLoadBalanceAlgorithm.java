package com.study.week8.config.shardingjbdc;

import com.study.week8.utils.PropertiesUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.shardingsphere.spi.masterslave.MasterSlaveLoadBalanceAlgorithm;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 自定义读数据库负载均衡实现，需要实现 MasterSlaveLoadBalanceAlgorithm 接口 并提供空参构造函数
 * 且在 resources下新建 META_INF/services 目录 并将相应信息写进去
 */
@Setter
@Getter
public final class WeightLoadBalanceAlgorithm implements MasterSlaveLoadBalanceAlgorithm {

    public WeightLoadBalanceAlgorithm() {
    }

    private Properties properties = new Properties();

    private List<String> slaverDatasource = new ArrayList<>();
    private AtomicLong count = new AtomicLong(-1);

    @Override
    public String getDataSource(String name, String masterDataSourceName, List<String> slaveDataSourceNames) {
        //加载从数据源
        slaverWeight(slaveDataSourceNames);
        //根据权重获取
        long value = count.incrementAndGet();
        if (value >= Long.MAX_VALUE) {
            ReentrantLock lock = new ReentrantLock();
            try {
                lock.lock();
                if (value >= Long.MAX_VALUE) {
                    count.set(0);
                }
            } finally {
                lock.unlock();
            }
        }

        int index = (int) (count.longValue() % slaverDatasource.size());
        return slaverDatasource.get(index);
    }


    @Override
    public String getType() {
        return "WEIGHT";
    }


    //是否初始化过
    private boolean slaverDatasourceInit = false;

    private void slaverWeight(List<String> slaveDataSourceNames) {
        if (!slaverDatasourceInit) {
            String weights = PropertiesUtil.get("spring.shardingsphere.datasource.salver.weights");
            if (StringUtils.hasLength(weights)) {
                //TODO 化简
                String[] weightArr = weights.split(",");
                for (int i = 0; i < weightArr.length; i++) {
                    for (int j = 0; j < Integer.parseInt(weightArr[i]); j++) {
                        slaverDatasource.add(slaveDataSourceNames.get(i));
                    }
                }
            } else {
                slaverDatasource.addAll(slaveDataSourceNames);
            }
            slaverDatasourceInit = true;
        }
    }

}
