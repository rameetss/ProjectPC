package ca.projectpc.projectpc.api;

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
