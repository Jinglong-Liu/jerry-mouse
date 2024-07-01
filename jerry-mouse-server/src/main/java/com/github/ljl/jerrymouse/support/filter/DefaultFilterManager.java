package com.github.ljl.jerrymouse.support.filter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @since 0.6.1
 * @create: 2024-06-23 17:25
 **/

public class DefaultFilterManager implements IFilterManager {

    protected final Map<String, Filter> filterMap = new HashMap<>();

    public DefaultFilterManager() {}

    @Override
    public void init(String baseDir) {

    }

    @Override
    public void register(String urlPattern, Filter filter) {
        filterMap.put(urlPattern, filter);
    }

    @Override
    public Filter getFilter(String url) {
        return filterMap.get(url);
    }

    @Override
    public List<Filter> getMatchFilters(String url) {
        List<Filter> resultList = new ArrayList<>();

        for(Map.Entry<String, Filter> entry : filterMap.entrySet()) {
            String urlPattern = entry.getKey();
            if(url.matches(urlPattern)) {
                resultList.add(entry.getValue());
            }
        }

        return resultList;
    }
}
