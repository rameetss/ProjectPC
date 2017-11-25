package ca.projectpc.projectpc;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Task {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 4;
    private static final int KEEP_ALIVE_SECONDS = 60;

    private static final ThreadPoolExecutor sExecutor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_SECONDS,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>()
    );

    /**
     * Starts a task on a new thread as long as the pool is not empty. However this should be used
     * carefully as deadlocks can be produced if a task is depended on and the pool is empty,
     * therefore the thread will not be created and the depending thread will wait indefinitely.
     * @param callable Callable to run on new thread
     * @param <TResult> Callable result type
     * @return Task created by executor
     */
    public static <TResult> Future<TResult> run(Callable<TResult> callable) {
        return sExecutor.submit(callable);
    }
}
