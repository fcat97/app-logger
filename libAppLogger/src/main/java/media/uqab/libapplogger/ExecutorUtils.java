/**
 * {ExecutorUtils}
 * Copyright (C) 2022 github/fCat97
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package media.uqab.libapplogger;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorUtils {
    private static ExecutorService executor;
    private static ScheduledExecutorService scheduler;
    private static ExecutorService poolExecutor;

    public static void submitParallel(Runnable task) {
        int availableCores = Runtime.getRuntime().availableProcessors();
        if (poolExecutor == null || poolExecutor.isShutdown())
            poolExecutor = Executors.newFixedThreadPool(availableCores);

        poolExecutor.submit(task);
    }

    public static <T> T submitParallel(Callable<T> task) {
        int availableCores = Runtime.getRuntime().availableProcessors();
        if (poolExecutor == null || poolExecutor.isShutdown())
            poolExecutor = Executors.newFixedThreadPool(availableCores);

        Future<T> future = poolExecutor.submit(task);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void scheduleDelayedTask(Runnable task, long delay, TimeUnit timeUnit) {
        if (scheduler == null) scheduler = Executors.newSingleThreadScheduledExecutor();
        if (scheduler.isShutdown()) scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.schedule(task, delay, timeUnit);
    }

    public static void submitTask(Runnable task) {
        if (executor == null) executor = Executors.newSingleThreadExecutor();
        if (executor.isShutdown()) executor = Executors.newSingleThreadExecutor();

        executor.submit(task);
    }

    // blocks the thread on which `future.get()` is called
    public static <T> T submitTask(Callable<T> task) {
        if (executor == null) executor = Executors.newSingleThreadExecutor();
        if (executor.isShutdown()) executor = Executors.newSingleThreadExecutor();

        Future<T> future = executor.submit(task);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void terminateAllExecutors() {
        if (executor != null && !executor.isShutdown()) executor.shutdown();
        if (scheduler != null && !scheduler.isShutdown()) scheduler.shutdown();
        if (poolExecutor != null && !poolExecutor.isShutdown()) poolExecutor.shutdown();
    }

    public interface TaskWatcher<T> {
        void onFinished(T result);
    }
}
