package com.study.cthirdweek.gateway.router;

import java.util.List;

/**
 * 抽象的路由选择接口
 */
public interface Router {

    String route(List<String> list);
}
