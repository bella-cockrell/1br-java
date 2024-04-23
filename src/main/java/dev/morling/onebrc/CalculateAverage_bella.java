package dev.morling.onebrc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class CalculateAverage_bella {
    private static final String FILE = "./measurements.txt";


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        long start = System.currentTimeMillis(); //timer start

        Map<String, Double> parsedLines = null;

        try(ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            try (Stream<String> streamedFile = Files.lines(Paths.get(FILE))) {
                Future<Map<String, Double>> future = executorService.submit(new ParseLines(streamedFile));
                while(!future.isDone()) {
                    System.out.println("Waiting for future...");
                    Thread.sleep(1000);
                }
                parsedLines = future.get();
                executorService.shutdown();
                try {
                    if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                        executorService.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executorService.shutdownNow();
                }
            }
        }

        Map<String, Double> measurements = new TreeMap<>(parsedLines.entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey, e -> Math.round(e.getValue() * 10.0) / 10.0)));

        System.out.println(measurements);
        System.out.printf("Task finished in %s ms%n", System.currentTimeMillis() - start);
    }


    private record Measurement(String station, double value) {
        private Measurement(String[] parts) {
            this(parts[0], Double.parseDouble(parts[1]));
        }
    }

    private static class ParseLines implements Callable<Map<String, Double>> {
        Stream<String> stream;

        public ParseLines(Stream<String> streamedFile) {
            stream = streamedFile;
        }

        @Override
        public Map<String, Double> call() throws Exception {
            return stream.map(line -> line.split(";"))
                    .collect(groupingBy(metric -> metric[0], averagingDouble(metric -> Double.parseDouble(metric[1]))));
        }
    }

}
