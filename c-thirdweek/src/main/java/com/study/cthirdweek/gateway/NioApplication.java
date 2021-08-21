package com.study.cthirdweek.gateway;

import com.study.cthirdweek.gateway.netty4.NettyServer;
import com.study.cthirdweek.gateway.utils.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务启动类
 */
public class NioApplication {

    private static final Logger log = LoggerFactory.getLogger(NioApplication.class);

    /**
     * TODO 有个疑问：com.study.cthirdweek.gateway.httprequesthandler.HttpClientRequestHandler类的executor方法，
     *      能不能使用线程池异步执行，如果有的接口耗时两三秒的话，会导致整个阻塞住
     *
     */
    public static void main(String[] args) {
        String port = PropertiesUtil.get("server.port", "9000");
        NettyServer nettyServer = new NettyServer(Integer.parseInt(port));
        try {
            nettyServer.run();
        } catch (Exception e) {
            log.error("程序启动异常 cause={} e={}", e.getCause(), e.getMessage());
        }
    }

}
