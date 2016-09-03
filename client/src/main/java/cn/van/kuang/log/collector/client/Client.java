package cn.van.kuang.log.collector.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

        final AtomicInteger counter = new AtomicInteger(1);
        executorService.scheduleAtFixedRate(() -> LOGGER.info("Message, {}", counter.getAndIncrement()),
                100, 100, TimeUnit.MILLISECONDS);

        executorService.scheduleAtFixedRate(() -> LOGGER.info("Message, {}", counter.getAndIncrement()),
                200, 100, TimeUnit.MILLISECONDS);

        executorService.scheduleAtFixedRate(() -> LOGGER.info("Message, {}", counter.getAndIncrement()),
                200, 100, TimeUnit.MILLISECONDS);

        executorService.scheduleAtFixedRate(() -> LOGGER.info("Message, {}", counter.getAndIncrement()),
                200, 100, TimeUnit.MILLISECONDS);

        executorService.scheduleAtFixedRate(() -> LOGGER.info("Message, {}", counter.getAndIncrement()),
                200, 100, TimeUnit.MILLISECONDS);
    }

}
