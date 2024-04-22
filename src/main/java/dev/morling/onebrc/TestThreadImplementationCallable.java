package dev.morling.onebrc;

import java.io.IOException;
import java.util.concurrent.*;

public class TestThreadImplementationCallable implements Callable<String> {
    /*Instead of overriding run() method, for this one we override call() method,
    which unlike run() can throw an exception and can return a generic value*/

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = executorService.submit(new TestThreadImplementationCallable());
        //.submit and .invokeAll methods return Futures

        String result = null;
        try {
            result = future.get(200, TimeUnit.MILLISECONDS); //Calling the get() method while the task is still
            // running will cause execution to block until the task properly executes and the result is available.
            //adding a timeout can prevent degradation
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println(result);

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

        @Override
    public String call() throws Exception {
        return "Hello, I am a Callable thread!";
    }

}
