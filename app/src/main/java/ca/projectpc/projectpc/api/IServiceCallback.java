package ca.projectpc.projectpc.api;

/**
 * An interface for implementing different callback outcomes for a Service call
 * @param <TResult> API result data type
 */
public interface IServiceCallback<TResult> {
    /**
     * Called when the request ends, along with the success/error code and result from the API
     * @param result Call result containing code, data, exception or cancel values
     */
    void onEnd(ServiceResult<TResult> result);
}
