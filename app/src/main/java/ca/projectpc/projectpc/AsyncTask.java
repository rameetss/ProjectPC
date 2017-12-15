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
 * Wrapper for Android asynchronous tasks to simplify implementation of child tasks and added state
 * information
 * @param <TResult> Task result type
 */
public abstract class AsyncTask<TResult> extends android.os.AsyncTask<Void, Void,
        AsyncTaskResult<TResult>> {
    private boolean mRunning;

    /**
     * Task to execute asynchronously (Internal)
     * @param params Unused parameters
     * @return Task result
     */
    @Override
    protected AsyncTaskResult<TResult> doInBackground(Void... params) {
        return process();
    }

    /**
     * Called when the task is started (Internal)
     */
    @Override
    protected void onPreExecute() {
        mRunning = true;
        onBegin();
    }

    /**
     * Called after the task is completed (Internal)
     * @param result Task result
     */
    @Override
    protected void onPostExecute(AsyncTaskResult<TResult> result) {
        onEnd(result);
        mRunning = false;
    }

    /**
     * Called if the task is cancelled (Internal)
     * @param result Task result
     */
    @Override
    protected void onCancelled(AsyncTaskResult<TResult> result) {
        onCancelled();
        mRunning = false;
    }

    /**
     * Called when the task is started
     */
    protected void onBegin() {
    }

    /**
     * Called when the task is completed
     * @param result Task result
     */
    protected void onEnd(AsyncTaskResult<TResult> result) {
    }

    /**
     * Called if the task is cancelled
     */
    @Override
    protected void onCancelled() {
    }

    /**
     * Task is running or not
     * @return Whether the task is running or not
     */
    public boolean isRunning() {
        return mRunning;
    }

    /**
     * Task to execute asynchronously
     * @return Task result
     */
    protected abstract AsyncTaskResult<TResult> process();

    /**
     * Cancels the task
     */
    public void cancel() {
        super.cancel(true);
    }
}
