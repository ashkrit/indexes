package playground.index.time;

import java.util.function.Supplier;

public class ApplicationTimer {

    public static <T> T time(String name, Supplier<T> supplier) {
        long start = System.currentTimeMillis();
        try {
            return supplier.get();
        } finally {
            long total = System.currentTimeMillis() - start;
            System.out.println(String.format("Task %s took %s ms", name, total));
        }
    }
}
