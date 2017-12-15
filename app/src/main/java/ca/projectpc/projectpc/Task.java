/*
 * ProjectPC
 *
 * Copyright (C) 2017 ProjectPC. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or any
 * later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details. You should have received
 * a copy of the GNU General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */

package ca.projectpc.projectpc;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Provides a Task running API using a thread pool, with the amount of CPU cores used as the base
 * amount of threads
 */
public class Task {
    private static final ExecutorService sExecutor = Executors.newCachedThreadPool();

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
