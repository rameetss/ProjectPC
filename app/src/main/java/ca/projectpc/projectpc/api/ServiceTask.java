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

package ca.projectpc.projectpc.api;

import ca.projectpc.projectpc.network.HttpTask;

/**
 * Wraps around HttpTask to encapsulate internal task information and provide a simple interface
 * to the API
 */
public class ServiceTask {
    private HttpTask mTask;

    /**
     * Initializes instance with HttpTask
     * @param task Internal task
     */
    public ServiceTask(HttpTask task) {
        mTask = task;
    }

    /**
     * Cancels request
     */
    public void cancel() {
        mTask.cancel();
    }

    /**
     * Request was cancelled or not
     * @return Whether the request was cancelled or not
     */
    public boolean isCancelled() {
        return mTask.isCancelled();
    }

    /**
     * Task is running or not
     * @return Whether the task is running or not
     */
    public boolean isRunning() {
        return mTask.isRunning();
    }
}
