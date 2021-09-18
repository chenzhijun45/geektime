package com.example.dynamicdatasource.config.datasource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据源选择注解，加在方法上可决定使用哪种数据源，默认为写数据源
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChooseDataSource {

    //默认为写数据源
    DatasourceEnum name() default DatasourceEnum.WRITE;
}
