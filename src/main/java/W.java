import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class W {

    private final Scheduler scheduler;

    private final Metrics metrics;

    public W(final int maxRunning) {
        scheduler = new Scheduler(maxRunning);

        metrics = new Metrics(scheduler);
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public <T> CompletableFuture<T> spawn(final Supplier<T> supplier) {
        final Task<T> task = new Task<>(supplier);
        task.assignScheduler(scheduler);
        return task.asCompletableFuture();
    }

    public CompletableFuture<Void> spawn(final Runnable runnable) {
        return spawn(() -> {
            runnable.run();
            return null;
        });
    }

    public <T> T await(final CompletableFuture<T> completableFuture) throws ExecutionException, InterruptedException {
        return this.<T, InterruptedException, ExecutionException>doBlockEx2(completableFuture::get);
    }

    public void doBlock(final Runnable runnable) {
        runnable.run();
    }

    public <T> T doBlock(final Supplier<T> supplier) throws InterruptedException {
        try (final var bc = new BlockContext()) {
            return supplier.get();
        }
    }

    public <E1 extends Throwable> void doBlockEx1(final FunctionUtil.RunnableThrowing1<E1> runnable)
            throws E1, InterruptedException {
        try (final var bc = new BlockContext()) {
            runnable.run();
        }
    }

    public <T, E1 extends Throwable> T doBlockEx1(final FunctionUtil.SupplierThrowing1<T, E1> supplier)
            throws E1, InterruptedException {
        try (final var bc = new BlockContext()) {
            return supplier.get();
        }
    }

    public <E1 extends Throwable, E2 extends Throwable> void doBlockEx2(
            final FunctionUtil.RunnableThrowing2<E1, E2> runnable) throws E1, E2, InterruptedException {
        try (final var bc = new BlockContext()) {
            runnable.run();
        }
    }

    public <T, E1 extends Throwable, E2 extends Throwable> T doBlockEx2(
            final FunctionUtil.SupplierThrowing2<T, E1, E2> supplier) throws E1, E2, InterruptedException {
        try (final var bc = new BlockContext()) {
            return supplier.get();
        }
    }
}
