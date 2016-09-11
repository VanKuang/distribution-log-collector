package cn.van.kuang.log.monitor;

import cn.van.kuang.log.collector.common.LogObserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FileScanner {

    private static final int DEFAULT_SCAN_INTERVAL = 1000;

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private final LogObserver logObserver;
    private final File file;

    private long lastFileSize = 0L;

    public FileScanner(LogObserver logObserver, File file) {
        this.logObserver = logObserver;
        this.file = file;
    }

    public void start() throws FileNotFoundException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");

        executorService.scheduleWithFixedDelay(() -> {
            try {
                randomAccessFile.seek(lastFileSize);

                String line = null;
                while ((line = randomAccessFile.readLine()) != null) {
                    logObserver.onMessage(line);
                }

                lastFileSize = randomAccessFile.length();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, DEFAULT_SCAN_INTERVAL, TimeUnit.MILLISECONDS);

        System.out.println("Started scanning [" + file.getAbsolutePath() + "]");
    }

    public void stop() {
        executorService.shutdown();
    }

}
