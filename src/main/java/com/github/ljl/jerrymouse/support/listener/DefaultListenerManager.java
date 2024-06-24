package com.github.ljl.jerrymouse.support.listener;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @since 0.6.0
 * @create: 2024-06-24 12:03
 **/

public class DefaultListenerManager implements IListenerManager{

    private static DefaultListenerManager instance;

    private Map<String, List<EventListener>> listenerMap = new HashMap<>();

    private DefaultListenerManager() {}

    public static DefaultListenerManager get() {
        if (instance == null) {
            synchronized (DefaultListenerManager.class) {
                if (instance == null) {
                    instance = new DefaultListenerManager();
                }
            }
        }
        return instance;
    }
    @Override
    public void init(String baseDir) {

    }

    @Override
    public void register(String urlPrefix, EventListener listener) {
        if(!listenerMap.containsKey(urlPrefix)) {
            listenerMap.put(urlPrefix, new ArrayList<>());
        }
        listenerMap.get(urlPrefix).add(listener);
    }

    /**
     * TODO: 用urlPrefix区别，返回当前web-app的listener
     * @return
     */
    @Override
    public List<EventListener> getListeners() {
        return listenerMap.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
