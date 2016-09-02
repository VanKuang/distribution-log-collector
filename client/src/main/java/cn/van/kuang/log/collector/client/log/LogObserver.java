package cn.van.kuang.log.collector.client.log;

import ch.qos.logback.classic.spi.LoggingEvent;
import cn.van.kuang.log.collector.client.netty.NettyClient;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.atomic.AtomicBoolean;

public enum LogObserver {

    INSTANCE;

    private AtomicBoolean isInitialised = new AtomicBoolean(false);

    private NettyClient nettyClient;
    private ChannelHandlerContext channelContext;

    public void onMessage(Object event) {
        if (!isInitialised.get()) {
            return;
        }

        if (!nettyClient.isConnected()) {
            return;
        }

        channelContext.writeAndFlush(((LoggingEvent) event).getFormattedMessage());
    }

    public void init() {
        if (isInitialised.get()) {
            return;
        }

        try {
            nettyClient = new NettyClient();
            new Thread() {
                @Override
                public void run() {
                    try {
                        nettyClient.connect();
                    } catch (Exception e) {
                        System.out.println("Fail to connect Netty server");
                        e.printStackTrace();
                    }
                }
            }.start();
            channelContext = nettyClient.getChannelContext();
            isInitialised.set(true);
            System.out.println("Initialised");
        } catch (Throwable throwable) {
            System.out.println("Fail to connect Netty server");
            throwable.printStackTrace();
        }
    }

}
