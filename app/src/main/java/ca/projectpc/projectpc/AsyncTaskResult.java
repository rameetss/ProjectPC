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

/**
 * A wrapper class around task results to implement either an exception or a result depending on
 * whether a task failed to complete or not
 * @param <TResult> Result data type
 */
public class AsyncTaskResult<TResult> {
    private TResult mResult;
    private Exception mException;

    /**
     * Initializes instance with result or exception information
     * @param result Task result
     * @param exception Exception information
     */
    public AsyncTaskResult(TResult result, Exception exception) {
        mResult = result;
        mException = exception;
    }

    /**
     * Error during task execution
     * @return Whether there was an error during task execution
     */
    public boolean hasError() {
        return mException != null;
    }

    /**
     * Has task result
     * @return Whether there is a result for the task
     */
    public boolean hasResult() {
        return mResult != null;
    }

    /**
     * Gets result
     * @return Task result
     */
    public TResult getResult() {
        return mResult;
    }

    /**
     * Gets exception information
     * @return Task exception information
     */
    public Exception getException() {
        return mException;
    }
}
