package com.github.ljl.wheel.jerrymouse.server.nio.reactor;

import com.github.ljl.wheel.jerrymouse.utils.ThreadPoolUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 09:59
 **/

public class SubReactorManager {
    public static final int SUB_REACTOR_THREAD_SIZE = 10;

    private final Executor subThreadPool = ThreadPoolUtils.newFixedThreadPool(SUB_REACTOR_THREAD_SIZE, "sub-reactor");

    private final SubReactor[] subReactors = new SubReactor[SUB_REACTOR_THREAD_SIZE];

    private int index = 0;

    private static SubReactorManager instance;

    private SubReactorManager() {
        for (int i = 0; i < subReactors.length; i++) {
            try {
                subReactors[i] = new SubReactor();
                subThreadPool.execute(subReactors[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * singleton
     * @return
     */
    public static SubReactorManager get() {
        if (Objects.isNull(instance)) {
            synchronized (SubReactorManager.class) {
                if (Objects.isNull(instance)) {
                    instance = new SubReactorManager();
                }
            }
        }
        return instance;
    }

    public SubReactor nextReactor() {
        SubReactor reactor = subReactors[index];
        index = (index + 1) % SUB_REACTOR_THREAD_SIZE;
        return reactor;
    }
}
