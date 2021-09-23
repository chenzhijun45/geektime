package com.study.week8.config.shardingjbdc.keygenerator;

import lombok.Getter;
import lombok.Setter;
import org.apache.shardingsphere.spi.keygen.ShardingKeyGenerator;

import java.util.Properties;


/**
 * 自定义分布式主键生成器 需实现 ShardingKeyGenerator 接口
 * 且在 resources下新建 META_INF/services 目录 并将相应信息写进去
 */
@Getter
@Setter
public class MyKeyGenerator implements ShardingKeyGenerator {

    private Properties properties = new Properties();

    @Override
    public Comparable<?> generateKey() {
        //TODO
        return null;
    }

    @Override
    public String getType() {
        return "CZJ";
    }

}
