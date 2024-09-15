package com.github.ljl.wheel.jerrymouse.support.servlet.filter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-15 13:48
 **/

public class FilterManager {
    protected final Map<String, Filter> filterMap = new HashMap<>();

    public FilterManager() {}

    public void init(String baseDir) {

    }

    public void register(String urlPattern, Filter filter) {
        filterMap.put(urlPattern, filter);
    }

    public Filter getFilter(String url) {
        return filterMap.get(url);
    }

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
