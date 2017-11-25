package ca.projectpc.projectpc.api;

import android.support.annotation.NonNull;

public class ServiceResultCode implements Comparable<ServiceResultCode> {
    public static final ServiceResultCode Ok = new ServiceResultCode(1000);
    public static final ServiceResultCode AlreadyAuthenticated = new ServiceResultCode(1001);
    public static final ServiceResultCode NotImplemented = new ServiceResultCode(1002);

    private int mValue;

    public ServiceResultCode(int value) {
        mValue = value;
    }

    @Override
    public int compareTo(@NonNull ServiceResultCode o) {
        return Integer.compare(mValue, o.mValue);
    }
};