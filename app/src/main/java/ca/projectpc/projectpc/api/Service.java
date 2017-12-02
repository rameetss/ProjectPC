package ca.projectpc.projectpc.api;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import ca.projectpc.projectpc.AsyncTaskResult;
import ca.projectpc.projectpc.Task;
import ca.projectpc.projectpc.network.HttpResponse;
import ca.projectpc.projectpc.network.HttpTask;

public abstract class Service {
    public static final int REQUEST_TIMEOUT = 1000;

    private static String sServerUrl = null;
    private static int sTimeout = REQUEST_TIMEOUT;
    private static Map<String, Service> sServices = new TreeMap<>();
    private static Map<String, String> sCookies = new TreeMap<>();

    public static String getServerUrl() {
        return sServerUrl;
    }

    public static void setServerUrl(String url) {
        sServerUrl = url;
    }

    public static int getTimeout() {
        return sTimeout;
    }

    public static void setTimeout(int timeout) {
        sTimeout = timeout;
    }

    protected static String getCookie(String name) {
        return sCookies.get(name);
    }

    protected static void setCookie(String name, String value) {
        sCookies.put(name, value);
    }

    protected static Map<String, String> getCookies() {
        return sCookies;
    }

    public static void clearCookies() {
        sCookies.clear();
    }

    public static void reset() {
        sCookies.clear();
        sTimeout = REQUEST_TIMEOUT;
        sServerUrl = null;
    }

    protected static <TResult>
    ServiceTask sendRequestRaw(String method, String path,
                            Map<String, List<String>> headers,
                            final byte[] buffer,
                            final Class<TResult> resultClass,
                            final IServiceCallback<TResult> internalCallback,
                            final IServiceCallback<TResult> callback)
        throws UnsupportedEncodingException, ParseException {
        String requestUrl = String.format("%s/%s", sServerUrl, path).replaceAll("///", "/");

        // Create headers map if it doesn't already exist
        if (headers == null) {
            headers = new TreeMap<>();
        }

        // Add cookies to headers
        String expiresTime = null;
        List<String> cookiesList = new ArrayList<>();
        for (Map.Entry<String, String> entry : sCookies.entrySet()) {
            if (entry.getKey().equals("Expires")) {
                expiresTime = entry.getValue();
            }

            cookiesList.add(String.format(!entry.getValue().isEmpty() ? "%s=%s" : "%s",
                    URLEncoder.encode(entry.getKey(), "UTF-8"),
                    URLEncoder.encode(entry.getValue(), "UTF-8")
            ));
        }

        boolean addCookies = true;
        if (expiresTime != null) {
            // Compare expire time, if cookies are expired, remove them from headers
            DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz",
                    Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            Date expiresDateTime = dateFormat.parse(expiresTime);
            Date currentDateTime = Calendar.getInstance().getTime();
            if (expiresDateTime.before(currentDateTime)) {
                sCookies.clear();
                if (headers.containsKey("Cookie"))
                    headers.remove("Cookie");
                addCookies = false;
            }
        }

        if (addCookies) {
            if (headers.containsKey("Cookie"))
                cookiesList.addAll(headers.get("Cookie"));

            headers.put("Cookie", cookiesList);
        }

        HttpTask task = new HttpTask(requestUrl, buffer, sTimeout) {
            private Future<ServiceMessage<TResult>> mFutureTask;

            @Override
            protected void onEnd(final AsyncTaskResult<HttpResponse> result) {
                if (result != null) {
                    if (!result.hasError()) {
                        // Get response
                        final HttpResponse response = result.getResult();

                        Map<String, List<String>> headers = response.getHeaders();
                        if (headers.containsKey("Set-Cookie")) {
                            List<String> cookies = headers.get("Set-Cookie");
                            for (String cookie : cookies) {
                                String[] cookieSplit = cookie.split("=");
                                try {
                                    // Read cookie key and value
                                    String key = URLDecoder.decode(cookieSplit[0], "UTF-8");
                                    String value = cookieSplit.length == 2
                                            ? URLDecoder.decode(cookieSplit[1], "UTF-8")
                                            : "";

                                    // Store cookie
                                    sCookies.put(key, value);
                                } catch (Exception ex) {
                                    ServiceResult<TResult> callbackResult
                                            = new ServiceResult<>(response.getCode(), ex);
                                    if (internalCallback != null)
                                        internalCallback.onEnd(callbackResult);
                                    if (callback != null)
                                        callback.onEnd(callbackResult);
                                }
                            }
                        }

                        // Read stream
                        mFutureTask = Task.run(new Callable<ServiceMessage<TResult>>() {
                            @Override
                            public ServiceMessage<TResult> call() throws Exception {
                                // Choose the correct stream, if the server response code isn't 200
                                // (HTTP OK) Java will throw a FileNotFoundException when accessing
                                // getInputStream on the connection object, so instead we read from
                                // the error stream, which should contain the body of the response
                                // from the server
                                InputStream readStream = response.getCode()
                                        == HttpURLConnection.HTTP_OK
                                        ? response.getDataStream() : response.getErrorStream();
                                InputStreamReader inputStreamReader =
                                        new InputStreamReader(readStream);
                                BufferedReader reader = new BufferedReader(inputStreamReader);
                                final StringBuilder builder = new StringBuilder();

                                String line;
                                while ((line = reader.readLine()) != null) {
                                    builder.append(line);
                                }

                                ServiceMessage<TResult> message = new ServiceMessage<>(resultClass);
                                message.deserialize(builder.toString());

                                return message;
                            }
                        });

                        try {
                            ServiceMessage<TResult> message = mFutureTask.get();
                            if (message.getError() != null) {
                                Exception exception = new Exception(message.getError());

                                ServiceResult<TResult> callbackResult
                                        = new ServiceResult<>(message.getCode(), exception);
                                if (internalCallback != null)
                                    internalCallback.onEnd(callbackResult);
                                if (callback != null)
                                    callback.onEnd(callbackResult);
                            } else {
                                ServiceResult<TResult> callbackResult
                                        = new ServiceResult<>(message.getCode(), message.getData());
                                if (internalCallback != null)
                                    internalCallback.onEnd(callbackResult);
                                if (callback != null)
                                    callback.onEnd(callbackResult);
                            }
                        } catch (Exception ex) {
                            ServiceResult<TResult> callbackResult
                                    = new ServiceResult<>(response.getCode(), ex);
                            if (internalCallback != null)
                                internalCallback.onEnd(callbackResult);
                            if (callback != null)
                                callback.onEnd(callbackResult);
                        }
                    } else {
                        ServiceResult<TResult> callbackResult
                                = new ServiceResult<>(0, result.getException());
                        if (internalCallback != null)
                            internalCallback.onEnd(callbackResult);
                        if (callback != null)
                            callback.onEnd(callbackResult);
                    }
                }
            }

            @Override
            protected void onCancelled() {
                if (mFutureTask != null) {
                    mFutureTask.cancel(true);
                    mFutureTask = null;
                }

                ServiceResult<TResult> callbackResult
                        = new ServiceResult<>();
                if (internalCallback != null)
                    internalCallback.onEnd(callbackResult);
                if (callback != null)
                    callback.onEnd(callbackResult);
            }
        };
        task.setMethod(method);
        task.setHeaders(headers);

        task.execute();

        return new ServiceTask(task);
    }

    protected static <TArgs, TResult>
    ServiceTask sendRequest(String method, String path,
                            Map<String, List<String>> headers,
                            TArgs args, Class<TArgs> argsClass,
                            final Class<TResult> resultClass,
                            final IServiceCallback<TResult> internalCallback,
                            final IServiceCallback<TResult> callback)
            throws UnsupportedEncodingException, ParseException {
        byte[] data = null;
        if (args != null && argsClass != null) {
            Gson gson = new Gson();
            String argsEncoded = gson.toJson(args, argsClass);
            data = argsEncoded.getBytes("UTF-8");
        }
        if (headers == null) {
            headers = new TreeMap<>();
        }

        headers.put("Content-Type", Arrays.asList("application/json"));

        return sendRequestRaw(method, path, headers, data, resultClass, internalCallback, callback);
    }

    protected static <TArgs, TResult>
    ServiceTask sendRequest(String method,
                            String path,
                            Map<String, String> cookies,
                            Map<String, List<String>> headers,
                            TArgs args, Class<TArgs> argsClass,
                            final Class<TResult> resultClass,
                            final IServiceCallback<TResult> internalCallback,
                            final IServiceCallback<TResult> callback)
            throws UnsupportedEncodingException, ParseException {
        List<String> cookiesList = new ArrayList<>();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            cookiesList.add(String.format("%s=%s",
                    URLEncoder.encode(entry.getKey(), "UTF-8"),
                    URLEncoder.encode(entry.getValue(), "UTF-8")
            ));
        }

        headers.put("Cookie", cookiesList);

        return sendRequest(method, path, headers, args, argsClass, resultClass, internalCallback,
                callback);
    }

    protected static <TArgs, TResult>
    ServiceTask sendRequest(String method,
                            String path,
                            TArgs args, Class<TArgs> argsClass,
                            final Class<TResult> resultClass,
                            final IServiceCallback<TResult> internalCallback,
                            final IServiceCallback<TResult> callback)
            throws UnsupportedEncodingException, ParseException {
        return sendRequest(method, path, null, args, argsClass, resultClass, internalCallback,
                callback);
    }

    protected static <TResult>
    ServiceTask sendRequest(String method,
                            String path,
                            final Class<TResult> resultClass,
                            final IServiceCallback<TResult> internalCallback,
                            final IServiceCallback<TResult> callback)
            throws UnsupportedEncodingException, ParseException {
        return sendRequest(method, path, null, null, null, resultClass, internalCallback, callback);
    }

    protected static
    ServiceTask sendRequest(String method,
                            String path,
                            final IServiceCallback<Void> internalCallback,
                            final IServiceCallback<Void> callback)
            throws UnsupportedEncodingException, ParseException {
        return sendRequest(method, path, null, null, null, null, internalCallback, callback);
    }

    protected static <TResult>
    ServiceTask sendRequest(String method,
                            String path,
                            Map<String, List<String>> headers,
                            byte[] buffer,
                            final Class<TResult> resultClass,
                            final IServiceCallback<TResult> internalCallback,
                            final IServiceCallback<TResult> callback)
            throws UnsupportedEncodingException, ParseException {
        return sendRequestRaw(method, path, headers, buffer, resultClass, internalCallback, callback);
    }

    protected static
    ServiceTask sendRequest(String method,
                            String path,
                            Map<String, List<String>> headers,
                            byte[] buffer,
                            final IServiceCallback<Void> internalCallback,
                            final IServiceCallback<Void> callback)
            throws UnsupportedEncodingException, ParseException {
        return sendRequestRaw(method, path, headers, buffer, null, internalCallback, callback);
    }

    public static <TService extends Service>
    TService get(Class<TService> clazz)
            throws IllegalAccessException, InstantiationException {
        if (sServices.containsKey(clazz.toString())) {
            return (TService)sServices.get(clazz.toString());
        }

        TService instance = clazz.newInstance();
        sServices.put(clazz.toString(), instance);
        return instance;
    }
}