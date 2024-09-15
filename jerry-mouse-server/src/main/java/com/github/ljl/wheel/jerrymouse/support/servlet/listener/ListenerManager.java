package com.github.ljl.wheel.jerrymouse.support.servlet.listener;

import java.util.*;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-15 15:13
 **/

public class ListenerManager {
    private final Set<EventListener> listeners = new LinkedHashSet<>();

    public ListenerManager() {}

    /**
     * add listener(不重复)
     * @param listener
     */
    public synchronized void register(EventListener listener) {
        listeners.add(listener);
    }

    /**
     * @return listeners
     */
    public List<EventListener> getListeners() {
        return new ArrayList<>(listeners);
    }
}
