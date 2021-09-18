package com.example.dynamicdatasource.config.datasource;

/**
 * 数据源上下文
 */
public class DynamicDataSourceHolder {

    private static ThreadLocal<DatasourceEnum> holder = new ThreadLocal<DatasourceEnum>();


    public static void putDataSource(DatasourceEnum datasourceEnum) {
        holder.set(datasourceEnum);
    }

    public static DatasourceEnum getDataSource() {
        return holder.get();
    }

    public static void clearDataSource() {
        holder.remove();
    }

}
