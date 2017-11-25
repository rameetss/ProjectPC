package ca.projectpc.projectpc.api;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Service messages are serialized into JSON objects which are specific to each result type,
 * this class is able to serialize and deserialize any data type which is used for communication
 * between the client and backend
 * @param <TData> Result data type
 */
public class ServiceMessage<TData> {
    @SerializedName("code")
    private int mCode;

    @SerializedName("error")
    private String mError;

    @SerializedName("data")
    private Object mDataSerialized;

    // These fields are transient because we don't want the JSON serializer to
    // discover type info for them
    private transient Class<TData> mClassInfo;
    private transient TData mData;

    /**
     * Initialize with required class info to allow serialization
     * @param classInfo Data class type information
     */
    public ServiceMessage(Class<TData> classInfo) {
        mClassInfo = classInfo;
        mDataSerialized = "null";

        mCode = -1;
        mData = null;
    }

    /**
     * Get specific message code
     * @return Message code
     */
    public int getCode() {
        return mCode;
    }

    /**
     * Get error string
     * @return Error string
     */
    public String getError() {
        return mError;
    }

    /**
     * Get message data
     * @return Message data
     */
    public TData getData() {
        return mData;
    }

    /**
     * Deserialize JSON string into message
     * @param data JSON serialized string
     */
    public void deserialize(String data) {
        Gson gson = new Gson();
        ServiceMessage<TData> message = gson.fromJson(data, getClass());

        mCode = message.mCode;
        mError = message.mError;
        mDataSerialized = gson.toJson(message.mDataSerialized);
        mData = gson.fromJson(mDataSerialized.toString(), mClassInfo);
    }

    /**
     * Serialize class instance into JSON sting
     * @return String serialized in JSON format
     */
    public String serialize() {
        Gson gson = new Gson();
        mDataSerialized = gson.toJson(mData, mClassInfo);
        return gson.toJson(this, getClass());
    }
}