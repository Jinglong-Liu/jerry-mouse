package com.github.ljl.wheel.jerrymouse.utils;

import com.github.ljl.wheel.jerrymouse.exception.ThreadPoolException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 10:01
 **/

public class ThreadPoolUtils {

    public static Executor newFixedThreadPool(int nThreads, String name) {
        return Executors.newFixedThreadPool(nThreads, new NamedThreadFactory(name));
    }

    static class NamedThreadFactory implements ThreadFactory {

        private static final int MAX_THREAD_COUNTS = 2048;
        private static final Map<String, Integer> nameMap = new HashMap<>();

        private final String name;
        private final int maxThreadCount;
        public NamedThreadFactory(String name) {
            this(name, 100);
        }
        public NamedThreadFactory(String name, int maxThreadCount) {
            if (maxThreadCount > MAX_THREAD_COUNTS) {
                throw new ThreadPoolException("maxThreadCount over " + MAX_THREAD_COUNTS);
            }
            this.maxThreadCount = maxThreadCount;
            this.name = name;
            nameMap.put(name, nextCount(nameMap.getOrDefault(name, 0)));
        }
        @Override
        public Thread newThread(Runnable r) {
            int count = nameMap.get(name);
            nameMap.put(name, nextCount(count));
            return new Thread(r, this.name + "-" + count);
        }
        private int nextCount(int count) {
            return (count + 1) % maxThreadCount;
        }
    }
}
