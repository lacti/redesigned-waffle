import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.fail;

public class MainTest {

    private final W w = new W(1);

    @Test
    public void test() throws Exception {
        new MetricsMonitor(w.getMetrics()).start();

        CompletableFuture.allOf(
            w.spawn(this::run),
            w.spawn(this::run)
        ).get();
    }

    private void run() {
        try {
            System.out.println("1");

            w.doBlockEx1(() -> Thread.sleep(1000L));

            System.out.println("2");

            final var i1 = w.await(getCpuBoundIntAsync());

            System.out.println("3: " + i1);

            final var i2 = getCpuBoundInt();

            System.out.println("4: " + i2);

        } catch (final InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private CompletableFuture<Integer> getCpuBoundIntAsync() {
        return w.spawn(this::getCpuBoundInt);
    }

    private int getCpuBoundInt() {
        System.out.println("cpu start");
        try {
            // Pseudo CPU-bound operation
            Thread.sleep(1000L);
        } catch (final InterruptedException e) {
            // Do nothing
        }
        System.out.println("cpu end");

        return 1;
    }

    @Test
    public void testDoBlocks() {
        w.spawn(() -> {
            try {
                w.doBlock(() -> 1);

                w.doBlockEx1(() -> Thread.sleep(2000L));

                w.<IOException, GeneralSecurityException>doBlockEx2(MainTest::twoExThrower);

                fail();

            } catch (final InterruptedException | IOException | GeneralSecurityException e) {
                // Do nothing
            }
        });
    }

    private static void twoExThrower() throws IOException, GeneralSecurityException {
        throw new IOException("");
    }
}
