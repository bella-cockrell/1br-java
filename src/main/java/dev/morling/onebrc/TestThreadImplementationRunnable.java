package dev.morling.onebrc;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestThreadImplementationRunnable implements Runnable {
    public static void main(String[] args) throws IOException {

        ExecutorService executorService = Executors.newFixedThreadPool(5);
//        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new TestThreadImplementationRunnable());//these are carried out in order they're added
        // to executor service, but guarantee goes when using more threads
        executorService.execute(() -> System.out.println("I'M A RANDOM TASK"));

        System.out.println("This is another RANDOM TASK!");//this is out of sequence -- separate thread

        executorService.shutdown();

//        Thread thread1 = new Thread(new TestThreadImplementationRunnable());
//        Thread thread2 = new Thread(new TestThreadImplementationRunnable());

//        thread1.start();
//        thread2.start();
    }

    @Override
    public void run() {
        for (int i = 1; i <= 10; i++) {
            System.out.println("I am thread number: " + i + " " + Thread.currentThread().getName());
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
