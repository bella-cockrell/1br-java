package dev.morling.onebrc;

import java.io.IOException;
import java.util.concurrent.Callable;

public class TestThreadImplementationCallable implements Callable<String> {
    /*Instead of overriding run() method, for this one we override call() method,
    which unlike run() can throw an exception and can return a generic value*/

    public static void main(String[] args) throws Exception {
        TestThreadImplementationCallable callable = new TestThreadImplementationCallable();
        String result = callable.call();
        System.out.println(result);
    }

        @Override
    public String call() throws Exception {
        return "Hello, I am a Callable thread!";
    }

}
