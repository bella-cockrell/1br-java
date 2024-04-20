package dev.morling.onebrc;

import java.io.IOException;

public class TestThreadImplementationRunnable implements Runnable {
    public static void main(String[] args) throws IOException {
        Thread thread1 = new Thread(new TestThreadImplementationRunnable());
        Thread thread2 = new Thread(new TestThreadImplementationRunnable());

        thread1.start();
        thread2.start();
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
