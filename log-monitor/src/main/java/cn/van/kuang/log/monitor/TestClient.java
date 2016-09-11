package cn.van.kuang.log.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TestClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestClient.class);

    public static void main(String[] args) throws Exception {
        LogFileMonitor.INSTANCE.start("/Users/VanKuang/Development/workspace/distribution-log-collector/", "log");

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

        final AtomicInteger counter = new AtomicInteger(1);
        executorService.scheduleAtFixedRate(() -> LOGGER.info("Message, {}", counter.getAndIncrement()),
                100, 1000, TimeUnit.MILLISECONDS);

//        executorService.scheduleAtFixedRate(() -> LOGGER.info("Message, {}", counter.getAndIncrement()),
//                200, 100, TimeUnit.MILLISECONDS);
//
//        executorService.scheduleAtFixedRate(() -> LOGGER.info("Message, {}", counter.getAndIncrement()),
//                200, 100, TimeUnit.MILLISECONDS);
//
//        executorService.scheduleAtFixedRate(() -> LOGGER.info("Message, {}", counter.getAndIncrement()),
//                200, 100, TimeUnit.MILLISECONDS);
//
//        executorService.scheduleAtFixedRate(() -> LOGGER.info("Message, {}", counter.getAndIncrement()),
//                200, 100, TimeUnit.MILLISECONDS);
    }
}
