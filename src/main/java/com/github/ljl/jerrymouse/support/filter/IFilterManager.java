package com.github.ljl.jerrymouse.support.filter;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import java.util.List;

public interface IFilterManager {
    /**
     * 初始化
     * @param baseDir 基础文件夹
     * @since 0.6.1
     */
    void init(String baseDir);

    /**
     * 注册 servlet
     *
     * @param url     url
     * @param filter servlet
     * @since 0.6.1
     */
    void register(String url, Filter filter);

    /**
     * 获取 servlet
     *
     * @param url url
     * @return servlet
     * @since 0.6.1
     */
    Filter getFilter(String url);

    /**
     * 获取匹配的
     * @param url 正则
     * @return 结果
     * @since 0.6.1
     */
    List<Filter> getMatchFilters(String url);
}
