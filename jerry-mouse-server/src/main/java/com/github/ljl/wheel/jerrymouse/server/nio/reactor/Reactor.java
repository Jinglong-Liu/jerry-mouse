package com.github.ljl.wheel.jerrymouse.server.nio.reactor;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

/**
 * @program: jerry-mouse
 * @description:
 * @author: ljl
 * @create: 2024-09-14 09:54
 * 参考实现：https://github.com/JasirVoriya/dark-one/blob/master/byte-bye/src/main/java/cn/darkone/framework/common/bytebye/reactor/Reactor.java
 **/

public abstract class Reactor implements Runnable, Closeable {

    protected Logger logger = LoggerFactory.getLogger(Reactor.class);

    protected static final int SELECT_TIMEOUT = 300;

    protected Selector selector;

    private long noneLoopCount = 0;

    protected Reactor() throws IOException {
        this.selector = Selector.open();
    }

    protected final void dispatch(SelectionKey key) {
        if (!key.isValid()) {
            return;
        }
        Object att = key.attachment();
        if (att instanceof Runnable) {
            ((Runnable) att).run();
        }
    }

    protected void eventLoopOnce() throws IOException {
        int count = selector.select(SELECT_TIMEOUT);
        if (count == 0) {
            ++noneLoopCount;
            // logger.debug("event loop: " + noneLoopCount);
            return;
        }
        Set<SelectionKey> keys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = keys.iterator();
        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            iterator.remove();
            this.dispatch(key);
        }
    }
    @Override
    public void run() {
        while (true) {
            try {
                eventLoopOnce();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    @Override
    public void close() throws IOException {
        selector.close();
    }
}
