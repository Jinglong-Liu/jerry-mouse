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

    private static DefaultFilterManager instance;

    private DefaultFilterManager() {}

    public static DefaultFilterManager get() {
        if (instance == null) {
            synchronized (DefaultFilterManager.class) {
                if (instance == null) {
                    instance = new DefaultFilterManager();
                }
            }
        }
        return instance;
    }
    @Override
    public void init(String baseDir) {

    }

    @Override
    public void register(String url, Filter filter) {
        filterMap.put(url, filter);
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
