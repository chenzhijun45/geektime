package com.example.dynamicdatasource.aop;

import com.example.dynamicdatasource.config.datasource.ChooseDataSource;
import com.example.dynamicdatasource.config.datasource.DatasourceEnum;
import com.example.dynamicdatasource.config.datasource.DynamicDataSourceHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class DatasourceAop {

    /**
     * 定义切点
     */
    @Pointcut("@annotation(com.example.dynamicdatasource.config.datasource.ChooseDataSource)")
    public void dataSourceChoosePoint() {
    }

    /**
     * 切面处理，拦截@ChooseDataSource注解修饰的方法
     */
    @Around("dataSourceChoosePoint()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        ChooseDataSource chooseDataSource = method.getAnnotation(ChooseDataSource.class);
        if (chooseDataSource == null) {
            //如果没有标注该注解 默认返回读数据源
            DynamicDataSourceHolder.putDataSource(DatasourceEnum.WRITE);
        } else {
            DynamicDataSourceHolder.putDataSource(chooseDataSource.name());
        }

        try {
            return joinPoint.proceed();
        } finally {
            DynamicDataSourceHolder.clearDataSource();
        }
    }

}
