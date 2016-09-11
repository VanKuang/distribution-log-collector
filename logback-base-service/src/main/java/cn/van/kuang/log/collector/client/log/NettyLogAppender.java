package cn.van.kuang.log.collector.client.log;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import cn.van.kuang.log.collector.common.LogObserver;

public class NettyLogAppender<E> extends UnsynchronizedAppenderBase<E> {

    private final LogObserver logObserver;

    public NettyLogAppender() {
        logObserver = LogObserver.INSTANCE;
        logObserver.init();
    }

    @Override
    protected void append(E eventObject) {
        logObserver.onMessage(((LoggingEvent) eventObject).getFormattedMessage());
    }
}
