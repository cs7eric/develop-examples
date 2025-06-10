package cn.nooops.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * 任务批处理通用工具类
 *
 * @author cccs7 - csq020611@gmail.com
 * @date 2025/06/10
 */
public class TaskDisposeUtils {

    /**
     * 处理
     *
     * @param taskList 任务列表
     * @param consumer 处理任务的方法
     * @param executor 线程池
     * @throws InterruptedException 中断异常
     */
    public static <T> void dispose(List<T> taskList, Consumer<? super T> consumer, Executor executor) throws InterruptedException {
        if (taskList == null || taskList.isEmpty()) {
            return;
        }

        Objects.nonNull(consumer);

        CountDownLatch countDownLatch = new CountDownLatch(taskList.size());
        for (T item : taskList) {
            executor.execute(() -> {
                try {
                    consumer.accept(item);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
    }

    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        ArrayList<String> taskList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            taskList.add("task-" + i);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        TaskDisposeUtils.dispose(taskList, TaskDisposeUtils::disposeTask, executorService);
        System.out.println("taskList completed, cost(ms):" + (System.currentTimeMillis() - startTime));
        executorService.shutdown();
    }

    public static void disposeTask(String task) {
        System.out.println(String.format("【%s】start - %s", task, System.currentTimeMillis()));
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}