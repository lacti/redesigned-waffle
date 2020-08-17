public class BlockContext implements AutoCloseable {

    private final Task<?> task;

    BlockContext() {
        task = Task.getCurrentTask();
        task.blockBegin();
    }

    @Override
    public void close() throws InterruptedException {
        task.blockEnd();
    }
}
