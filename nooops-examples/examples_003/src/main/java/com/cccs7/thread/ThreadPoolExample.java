package com.cccs7.thread;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池示例
 *
 * @author cccs7 - csq020611@gmail.com
 * @date 2025/06/23
 */
public class ThreadPoolExample {

    static class LogAndRunHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.out.println("Task rejected: " + r.toString() +
                    ", Pool status: [Active threads=" + executor.getActiveCount() +
                    ", Queued tasks=" + executor.getQueue().size() + "]");
            // 在调用者线程中执行任务
            r.run();
        }
    }

    public static void main(String[] args) {


        ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(5);
        ThreadPoolExecutor.AbortPolicy handler = new ThreadPoolExecutor.AbortPolicy();


        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {

                Thread thread = new Thread(r, "no_oops-worker-" + threadNumber.getAndIncrement());
                thread.setDaemon(false);
                thread.setPriority(Thread.NORM_PRIORITY);
                thread.setUncaughtExceptionHandler((t, e) -> {
                    System.out.println("Uncaught exception in + " + t.getName() + ":" + e.getMessage());
                });
                return thread;
            }
        };

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                3,
                3,
                60L,
                TimeUnit.SECONDS,
                workQueue,
                threadFactory,
                new LogAndRunHandler());

        //提交任务
        for (int i = 1; i <= 200; i++) {
            final int taskNumber = i;
            executor.execute(() -> {
                System.out.println("Task " + taskNumber + " is running on thread: " + Thread.currentThread().getName());
                try {
                    // 模拟任务执行时间
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Task " + taskNumber + " is completed.");
            });
        }
    }

}
