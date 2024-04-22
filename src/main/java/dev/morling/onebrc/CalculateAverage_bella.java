package dev.morling.onebrc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;

import static java.util.stream.Collectors.groupingBy;

public class CalculateAverage_bella implements Runnable {
    private static final String FILE = "./measurements.txt";
    private static String line = null;

    Collector<Measurement, MeasurementAggregator, ResultRow> collector = Collector.of(
            MeasurementAggregator::new,
            (a, m) -> {
                a.min = Math.min(a.min, m.value);
                a.max = Math.max(a.max, m.value);
                a.sum += m.value;
                a.count++;
            },
            (agg1, agg2) -> {
                var res = new MeasurementAggregator();
                res.min = Math.min(agg1.min, agg2.min);
                res.max = Math.max(agg1.max, agg2.max);
                res.sum = agg1.sum + agg2.sum;
                res.count = agg1.count + agg2.count;

                return res;
            },
            agg -> {
                return new ResultRow(agg.min, (Math.round(agg.sum * 10.0) / 10.0) / agg.count, agg.max);
            });

    @Override
    public void run() {
        String line = getLine();
        System.out.println(line);
//        Map<String, ResultRow> measurements = new TreeMap<>(Files.lines(Paths.get(FILE))
//        .map(l -> new Measurement(l.split(";")))
//        .collect(groupingBy(m -> m.station(), collector)));
    }

    private static class MeasurementAggregator {
        private double min = Double.POSITIVE_INFINITY;
        private double max = Double.NEGATIVE_INFINITY;
        private double sum;
        private long count;
    }

    private record Measurement(String station, double value) {
        private Measurement(String[] parts) {
            this(parts[0], Double.parseDouble(parts[1]));
        }
    }

    private record ResultRow(double min, double mean, double max) {
        public String toString() {
            /*`StringBuilder` outperforms `String` in Java for performance optimization.
            `String` is immutable, creating new objects for modifications, consuming memory and slowing execution.
            In contrast, `StringBuilder` is mutable, making it efficient for frequent concatenation,
            string modification, and large string construction.*/

            StringBuilder builder = new StringBuilder();
            builder.append(round(min)).append("/").append(round(mean)).append("/").append(round(max));
            return builder.toString();
        }

        private double round(double value) {
            return Math.round(value * 10.0) / 10.0;
        }
    };

    private static void setLine(String line) {
        CalculateAverage_bella.line = line;
    }

    private static String getLine() {
        return CalculateAverage_bella.line;
    }

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        // Map<String, Double> measurements1 = Files.lines(Paths.get(FILE))
        // .map(l -> l.split(";"))
        // .collect(groupingBy(m -> m[0], averagingDouble(m -> Double.parseDouble(m[1]))));
        //
        // measurements1 = new TreeMap<>(measurements1.entrySet()
        // .stream()
        // .collect(toMap(e -> e.getKey(), e -> Math.round(e.getValue() * 10.0) / 10.0)));
        // System.out.println(measurements1);

        try(ExecutorService executorService = Executors.newSingleThreadExecutor()) {

            try(var file = new FileInputStream(FILE)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(file));
                reader.lines().forEach(line -> {
                    setLine(line);
                    executorService.submit(new CalculateAverage_bella());//these are carried out in order they're added
                });
            }

            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }

//        System.out.println(measurements);
        System.out.printf("Task finished in %s ms%n", System.currentTimeMillis() - start);
    }
}
