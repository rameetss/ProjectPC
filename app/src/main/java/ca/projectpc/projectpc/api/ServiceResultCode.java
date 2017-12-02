package ca.projectpc.projectpc.api;

public class ServiceResultCode {
    public static final int Ok = 1000;
    public static final int InternalError = 1001;

    public static final int InvalidUserId = 1100;
    public static final int InvalidCredentials = 1101;
    public static final int AlreadyAuthenticated = 1102;
    public static final int UserAlreadyExists = 1103;

    public static final int InvalidPostId = 1200;
    public static final int InvalidImageId = 1201;
    public static final int ImageLimitReached = 1202;

    public static final int NotImplemented = 9999;
}