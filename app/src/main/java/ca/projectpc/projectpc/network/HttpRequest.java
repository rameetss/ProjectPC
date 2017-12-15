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

import android.text.TextUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Used for HTTP communication. HTTP requests contain url and POST data which is submitted when
 * requesting a response from the server specified.
 */
public class HttpRequest {
    public static final int DEFAULT_TIMEOUT = 10000;

    private URL mUrl;
    private String mMethod;
    private Map<String, List<String>> mHeaders;
    private byte[] mData;
    private HttpURLConnection mConnection;
    private HttpResponse mResponse;
    private int mTimeout;

    /**
     * Constructor initializing request with specified URL
     * @param url Request URL
     * @throws MalformedURLException Thrown when URL is malformed
     */
    public HttpRequest(String url) throws MalformedURLException {
        mUrl = new URL(url);
        mMethod = "GET";
        mHeaders = new TreeMap<>();
        mData = null;
        mTimeout = DEFAULT_TIMEOUT;
    }

    /**
     * Constructor initializing request with specified URL
     *
     * @param url Request URL
     * @param data Data in byte array
     * @throws MalformedURLException Thrown when URL is malformed
     */
    public HttpRequest(String url, byte[] data) throws MalformedURLException {
        mUrl = new URL(url);
        mMethod = "POST";
        mHeaders = new TreeMap<>();
        mData = data;
        mTimeout = DEFAULT_TIMEOUT;
    }

    /**
     * Constructor initializing request with specified URL
     * @param url Request URL
     * @param data Data in string
     * @throws MalformedURLException Thrown when URL is malformed
     */
    public HttpRequest(String url, String data) throws MalformedURLException {
        mUrl = new URL(url);
        mMethod = "GET";
        mHeaders = new TreeMap<>();
        mTimeout = DEFAULT_TIMEOUT;
        if (data != null && !data.isEmpty()) {
            mMethod = "POST";
            mData = data.getBytes();
        }
    }

    /**
     * Gets request timeout
     * @return Request timeout
     */
    public int getTimeout() {
        return mTimeout;
    }

    /**
     * Sets request timeout
     * @param timeout Request timeout
     */
    public void setTimeout(int timeout) {
        mTimeout = timeout;
    }

    /**
     * Gets URL
     * @return Request URL
     */
    public String getUrl() {
        return mUrl.toString();
    }

    /**
     * Gets HTTP request method
     * @return HTTP request method
     */
    public String getMethod() {
        return mMethod;
    }

    /**
     * Sets HTTP request method
     * @param method HTTP request method
     */
    public void setMethod(String method) {
        mMethod = method;
    }


    /**
     * Gets HTTP request headers
     * @return HTTP request headers
     */
    public Map<String, List<String>> getHeaders() {
        return mHeaders;
    }

    /**
     * Sets HTTP request headers
     * @param headers HTTP request headers
     */
    public void setHeaders(Map<String, List<String>> headers) {
        mHeaders = headers;
    }

    /**
     * Sets HTTP request headers
     * @param key Header key
     */
    public List<String> getHeader(String key) {
        return mHeaders.get(key);
    }

    /**
     * Sets HTTP request headers
     * @param key Header key
     * @param value Header value
     */
    public void setHeader(String key, List<String> value) {
        mHeaders.put(key, value);
    }

    /**
     * Closes request connection
     */
    public void close() {
        if (mConnection != null) {
            mConnection.disconnect();
            mConnection = null;
        }
    }

    /**
     * Gets response for HTTP request
     *
     * @return HTTP response
     * @throws IOException Thrown when connection is not opened successfully
     */
    public HttpResponse getResponse() throws IOException {
        if (mConnection != null) {
            return null;
        }

        if (mResponse != null && mResponse.isOpen()) {
            return mResponse;
        }

        // Create connection
        mConnection = (HttpURLConnection) mUrl.openConnection();
        mConnection.setConnectTimeout(mTimeout);
        mConnection.setReadTimeout(mTimeout);
        mConnection.setRequestMethod(mMethod);

        // Set headers
        for (Map.Entry<String, List<String>> entry : mHeaders.entrySet()) {
            mConnection.setRequestProperty(entry.getKey(), TextUtils.join("; ", entry.getValue()));
        }

        // Set data
        if (mData != null && mData.length > 0) {
            mConnection.setDoOutput(true);
            mConnection.setFixedLengthStreamingMode(mData.length);

            OutputStream outputStream = mConnection.getOutputStream();
            outputStream.write(mData);
        }

        int code = mConnection.getResponseCode();

        // Parse response headers and split them
        Map<String, List<String>> responseHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Map.Entry<String, List<String>> entry : mConnection.getHeaderFields().entrySet()) {
            if (entry.getKey() == null) {
                responseHeaders.put("Status", entry.getValue());
            } else if (entry.getValue().size() > 1) {
                responseHeaders.put(entry.getKey(), entry.getValue());
            } else if (entry.getValue().size() == 1) {
                String value = entry.getValue().get(0);
                responseHeaders.put(entry.getKey(), value.contains("; ")
                        ? Arrays.asList(TextUtils.split(value, "; "))
                        : entry.getValue()
                );
            }
        }

        return (mResponse = new HttpResponse(this, mConnection, code, responseHeaders));
    }
}
