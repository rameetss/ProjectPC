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

/**
 * Class to handle and fetch service results of a type TResult
 *
 * @param <TResult> Service result type
 */
public class ServiceResult<TResult> {
    private int mCode;
    private TResult mData;
    private Exception mException;
    private boolean mCancelled;

    public ServiceResult() {
        mCode = 0;
        mData = null;
        mException = null;
        mCancelled = true;
    }

    public ServiceResult(int code, TResult data) {
        mCode = code;
        mData = data;
    }

    public ServiceResult(int code, Exception ex) {
        mCode = code;
        mException = ex;
    }

    public ServiceResult(Exception ex) {
        mException = ex;
    }

    public int getCode() {
        return mCode;
    }

    public boolean hasData() {
        return mData != null;
    }

    public TResult getData() {
        return mData;
    }

    public boolean hasError() {
        return mException != null;
    }

    public Exception getException() {
        return mException;
    }

    public void throwException() throws Exception {
        throw mException;
    }

    public boolean isCancelled() {
        return mCancelled;
    }
}
