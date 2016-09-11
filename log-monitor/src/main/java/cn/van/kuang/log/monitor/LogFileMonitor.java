package cn.van.kuang.log.monitor;

import cn.van.kuang.log.collector.common.LogObserver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum LogFileMonitor {

    INSTANCE;

    private static final long DEFAULT_INTERVAL = 1000L;

    private LogObserver logObserver;

    private Map<String, FileScanner> scanners = new HashMap<>();

    public void start(String path, String suffix) throws Exception {
        System.out.println("Starting log file monitor");

        logObserver = LogObserver.INSTANCE;
        logObserver.init();

        scanExistingFiles(path, suffix);
        dispatchFileMonitor(path, suffix);

        System.out.println("Started log file monitor");
    }

    private void scanExistingFiles(String path, String suffix) {
        // TODO need consider how to skip duplicate log when restart log monitor
        Collection<File> files = FileUtils.listFiles(new File(path), new String[]{suffix}, false);
        files.forEach(this::startFileScanner);
    }

    private void dispatchFileMonitor(String path, String suffix) throws Exception {
        IOFileFilter directories = FileFilterUtils.and(
                FileFilterUtils.directoryFileFilter(),
                HiddenFileFilter.VISIBLE);
        IOFileFilter files = FileFilterUtils.and(
                FileFilterUtils.fileFileFilter(),
                FileFilterUtils.suffixFileFilter("." + suffix));
        IOFileFilter filter = FileFilterUtils.or(directories, files);

        FileAlterationObserver observer = new FileAlterationObserver(new File(path), filter);
        observer.addListener(new ObserverListener());

        FileAlterationMonitor monitor = new FileAlterationMonitor(DEFAULT_INTERVAL);
        monitor.addObserver(observer);
        monitor.start();
    }

    private void startFileScanner(File file) {
        try {
            FileScanner scanner = new FileScanner(logObserver, file);

            scanners.put(file.getAbsolutePath(), scanner);

            scanner.start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private class ObserverListener extends FileAlterationListenerAdaptor {

        @Override
        public void onFileCreate(File file) {
            System.out.println("[" + file.getAbsoluteFile() + "] created");

            startFileScanner(file);
        }

        @Override
        public void onFileDelete(File file) {
            System.out.println("[" + file.getAbsoluteFile() + "] deleted");

            FileScanner scanner = scanners.get(file.getAbsolutePath());
            if (scanner != null) {
                scanner.stop();
            }
        }

    }
}
