package dev.morling.onebrc;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class TestThreadImplementationDump implements Runnable {
    public static void main(String[] args) throws IOException {
        Thread thread = new Thread(new TestThreadImplementationDump());
        thread.start();
    }

    @Override
    public void run() {
        for (int i = 1; i <= 10; i++) {
            System.out.println("I am thread number: " + i);
        }
    }
}
