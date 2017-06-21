package com.integration.common.lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * InstanceLock
 *
 * 实例锁
 * 通过文件锁来确保相同组件在具体的服务器上仅能启动一个进程实例
 */
public class InstanceLock {

    private static final Logger LOG = LoggerFactory.getLogger(InstanceLock.class);

    // 要加载的文件
    private RandomAccessFile rf;
    // 文件锁
    private FileLock lock;

    /**
     * 构造函数
     * @param file 要获取文件锁对应的file对象
     */
    public InstanceLock(File file) {
        try {
            if (file != null) {
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        LOG.error("create {} failed", file.getAbsolutePath());
                        return;
                    }
                }

                rf = new RandomAccessFile(file, "rws");
                FileChannel channel = rf.getChannel();
                lock = channel.tryLock(0, Long.MAX_VALUE, false);
            } else {
                LOG.error("file is null, construct instance lock failed.");
            }
        } catch (IOException e) {
            LOG.error("{}", e);
        }
    }

    /**
     * 释放文件锁
     */
    public void release() {
        if (null != rf) {
            try {
                rf.close();
            } catch (IOException e) {
               LOG.error("{}", e);
            }
        }

        if (null != lock) {
            try {
                lock.close();
            } catch (IOException e) {
                LOG.error("{}", e);
            }
        }
    }

    /**
     * 获取文件锁
     * @return 如果文件锁已被其他进程获取，则返回未null
     */
    public FileLock getLock() {
        return lock;
    }
}
