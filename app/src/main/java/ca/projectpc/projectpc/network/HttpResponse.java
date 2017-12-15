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

package ca.projectpc.projectpc.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * Used for HTTP communication, contains response data for a request, containing the HTTP code,
 * stream, and whether the connection is open
 */
public class HttpResponse {
    private HttpRequest mRequest;
    private HttpURLConnection mConnection;
    private int mCode;
    private Map<String, List<String>> mHeaders;
    private boolean mStreamOpen;
    private boolean mOpen;

    /**
     * Initializes response with request, HTTP connection, the response code and input stream
     * @param request HTTP request
     * @param connection HTTP connection
     * @param code HTTP response code
     */
    public HttpResponse(HttpRequest request, HttpURLConnection connection, int code,
                        Map<String, List<String>> headers) {
        mRequest = request;
        mConnection = connection;
        mCode = code;
        mHeaders = headers;
        mStreamOpen = false;
        mOpen = true;
    }

    /**
     * Closes connection
     * @throws IOException Thrown if the connection failed to close
     */
    public void close(boolean disposeConnection) throws IOException {
        if (disposeConnection) {
            mConnection.disconnect();
        }

        if (mStreamOpen) {
            mConnection.getInputStream().close();
        }
        mOpen = false;
    }

    /**
     * Dispose of resources
     * @throws Throwable An error occurred while closing the underlying connection
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        if (mOpen) {
            if (mStreamOpen) {
                mConnection.getInputStream().close();
            }
            mConnection.disconnect();
        }
    }

    /**
     * Gets request
     * @return HTTP request
     */
    public HttpRequest getRequest() {
        return mRequest;
    }

    /**
     * Gets response data stream
     * @return HTTP data stream
     */
    public InputStream getDataStream() throws IOException {
        return mConnection.getInputStream();
    }

    /**
     * Gets response error stream
     * @return HTTP error stream
     */
    public InputStream getErrorStream() throws IOException {
        return mConnection.getErrorStream();
    }

    /**
     * Gets HTTP response code
     * @return HTTP response code
     */
    public int getCode() {
        return mCode;
    }

    /**
     * Gets HTTP response headers
     * @return HTTP response headers
     */
    public Map<String, List<String>> getHeaders() {
        return mHeaders;
    }

    /**
     * Gets HTTP response header using key
     * @param key Header key
     * @return Header value
     */
    public List<String> getHeader(String key) {
        return mHeaders.get(key);
    }

    /**
     * Connection is open or not
     * @return Whether the response connection is open or not
     */
    public boolean isOpen() {
        return mOpen;
    }
}
