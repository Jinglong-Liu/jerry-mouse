package com.github.ljl.jerrymouse.threadpool;

import com.github.ljl.jerrymouse.exception.JerryMouseException;

import java.util.concurrent.*;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-06-20 15:00
 **/

public class JerryMouseThreadPoolUtil {
    private static volatile JerryMouseThreadPoolUtil instance = null;

    private static ExecutorService executor;

    private static final int CAPACITY = 50;

    private static final int CORE_POOL_SIZE = 7;

    private static final int MAX_POOL_SIZE = 20;

    private static final long KEEP_ALIVE_TIME = 60L;

    private JerryMouseThreadPoolUtil() {
        this.executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(CAPACITY),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
    public static JerryMouseThreadPoolUtil get() {
        if (instance == null) {
            synchronized (JerryMouseThreadPoolUtil.class) {
                if (instance == null) {
                    instance = new JerryMouseThreadPoolUtil();
                }
            }
        }
        return instance;
    }
    public void execute(Runnable command) {
        executor.execute(command);
    }
    public void submit(Runnable task) {
        executor.submit(task);
    }
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("ThreadPool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            throw new JerryMouseException(ie);
        }
    }
}
