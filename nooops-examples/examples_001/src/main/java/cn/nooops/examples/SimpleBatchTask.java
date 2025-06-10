package cn.nooops.examples;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 简单任务批处理
 *
 * @author cccs7 - csq020611@gmail.com
 * @date 2025/06/10
 */
public class SimpleBatchTask {

    public static void main(String[] args) {
        batchTaskTest();
    }

    public static void batchTaskTest(){

        long startTime = System.currentTimeMillis();

        ArrayList<String> taskList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            taskList.add("task-" + i);
        }

        // 使用线程池批量处理任务
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // 创建CountDownLatch, 构造器参数为任务数量
        CountDownLatch countDownLatch = new CountDownLatch(taskList.size());
        for (String task : taskList) {
            executorService.execute(() -> {
                try {
                    disposeTask(task);
                } finally {
                    //处理完成后调用 countDownLatch.countDown()
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("taskList completed, cost(ms):" + (System.currentTimeMillis() - startTime));
        executorService.shutdown();
    }

    public static void disposeTask(String task){
        System.out.println(String.format("【%s】start - %s", task, System.currentTimeMillis()));
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
