package cn.van.kuang.log.collector.client.log;

import ch.qos.logback.core.UnsynchronizedAppenderBase;

public class NettyLogAppender<E> extends UnsynchronizedAppenderBase<E> {

    private final LogObserver logObserver;

    public NettyLogAppender() {
        logObserver = LogObserver.INSTANCE;
        logObserver.init();
    }

    @Override
    protected void append(E eventObject) {
        logObserver.onMessage(eventObject);
    }
}
