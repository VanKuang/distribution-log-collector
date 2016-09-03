package cn.van.kuang.log.collector.client.log;

import ch.qos.logback.classic.spi.LoggingEvent;
import cn.van.kuang.log.collector.client.netty.NettyClient;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public enum LogObserver {

    INSTANCE;

    private static final int SEND_MSG_INTERVAL_IN_MIL_SEC = 1000;
    private static final int BUCKET_SIZE = 30;

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>();

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

        String formattedMessage = ((LoggingEvent) event).getFormattedMessage();
        messages.offer(formattedMessage);
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
            System.out.println("Initialised Netty Client");

            dispatchSender();
            System.out.println("Dispatched sender thread");
        } catch (Throwable throwable) {
            System.out.println("Fail to connect Netty server");
            throwable.printStackTrace();
        }
    }

    private void dispatchSender() {
        executorService.scheduleAtFixedRate(
                () -> doCallSend(false),
                0,
                SEND_MSG_INTERVAL_IN_MIL_SEC,
                TimeUnit.MILLISECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Trigger shutdown hook");

                executorService.shutdown();

                doCallSend(true);

                System.out.println("Shutdown hook end");
            }
        });
    }

    private void doCallSend(boolean sync) {
        List<String> msgToSend = new ArrayList<>(BUCKET_SIZE);

        String message;
        while ((message = messages.poll()) != null) {
            msgToSend.add(message);

            if (msgToSend.size() == BUCKET_SIZE) {
                doSend(msgToSend, sync);
                msgToSend.clear();
            }
        }

        if (msgToSend.isEmpty()) {
            return;
        }

        doSend(msgToSend, sync);
        msgToSend.clear();
    }

    private void doSend(List<String> messages, boolean sync) {
        if (sync) {
            syncSend(messages);
        } else {
            asyncSend(messages);
        }
    }

    private void syncSend(List<String> messages) {
        List<String> tmpMessages = new ArrayList<>(messages);
        try {
            channelContext.writeAndFlush(tmpMessages).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void asyncSend(List<String> messages) {
        List<String> tmpMessages = new ArrayList<>(messages);
        channelContext.writeAndFlush(tmpMessages);
    }

}
