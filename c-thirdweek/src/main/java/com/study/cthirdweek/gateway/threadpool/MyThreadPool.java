package com.study.cthirdweek.gateway.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyThreadPool {

    private static final int cores = Runtime.getRuntime().availableProcessors();

    private MyThreadPool() {

    }

    private static class InnerClass {
        private static ThreadPoolExecutor executeThreadPool = executeThreadPool();
        private static ExecutorService callBackThreadPool = callBackThreadPool();
    }

    public static ThreadPoolExecutor getExecutePool() {
        return InnerClass.executeThreadPool;
    }

    public static ExecutorService getCallBackThreadPool() {
        return InnerClass.callBackThreadPool;
    }

    /**
     * 执行HTTP请求线程池
     */
    private static ThreadPoolExecutor executeThreadPool() {
        //Executors创建的四种线程池 本质上都是new ThreadPoolExecutor() 无非7个参数设置不一样 ScheduledThreadPoolExecutor稍微特殊一点
        return new ThreadPoolExecutor(
                cores * 2,//核心线程数 网关更偏向于io密集型，所以设置大一点
                cores * 2 + 4,//最大线程数
                8,//最大线程空闲存活时间
                TimeUnit.SECONDS,//最大线程空闲存活时间单位
                new LinkedBlockingQueue<>(3000),//阻塞队列大小
                new MyThreadFactory("myNetty"),//线程工厂
                new ThreadPoolExecutor.CallerRunsPolicy()//拒绝策略 提交任务线程处理，走到这里 说明网关请求达到瓶颈了，很难再接收新的请求了
        );
    }

    private static ExecutorService callBackThreadPool() {
        return new ThreadPoolExecutor(
                cores * 2,//核心线程数 网关更偏向于io密集型，所以设置大一点
                cores * 2 + 4,//最大线程数
                8,//最大线程空闲存活时间
                TimeUnit.SECONDS,//最大线程空闲存活时间单位
                new LinkedBlockingQueue<>(3000),//阻塞队列大小
                new MyThreadFactory("callBack"),//线程工厂
                new ThreadPoolExecutor.CallerRunsPolicy()//拒绝策略 提交任务线程处理，走到这里 说明网关请求达到瓶颈了，很难再接收新的请求了
        );
    }

}
