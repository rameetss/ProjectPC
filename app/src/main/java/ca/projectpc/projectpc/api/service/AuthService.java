package ca.projectpc.projectpc.api.service;

import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.ServiceTask;
import ca.projectpc.projectpc.utility.Hash;

public class AuthService extends Service {
    public class RoleType {
        public static final int Admin = 0;
        public static final int Moderator = 1;
        public static final int User = 2;
    }

    private class CreateAccountParameters {
        String email;
        String firstName;
        String lastName;
        String userName;
        int passwordHash;
    }

    private class LoginParameters {
        String email;
        int passwordHash;
    }

    public class SessionData implements Cloneable {
        public boolean authorized;
        public String userId;
        public String userName;
        public String firstName;
        public String lastName;
        public String email;
        public int role;

        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    public class AuthResult extends SessionData {
    }

    private SessionData mSessionData;

    public SessionData getSessionData() throws Exception {
        return (SessionData)mSessionData.clone();
    }

    public ServiceTask createAccount(String email, String firstName, String lastName,
                                     String userName, String password,
                                     IServiceCallback<AuthResult> callback)
            throws Exception {
        CreateAccountParameters parameters = new CreateAccountParameters();
        parameters.email = email;
        parameters.firstName = firstName;
        parameters.lastName = lastName;
        parameters.userName = userName;

        byte[] epBytes = String.format("%s%s", email, password).getBytes("UTF-8");
        parameters.passwordHash = Hash.FNV1A_32(epBytes);

        return sendRequest("POST", "/auth/create", parameters, CreateAccountParameters.class,
                AuthResult.class, new IServiceCallback<AuthResult>() {
                    @Override
                    public void onEnd(ServiceResult<AuthResult> result) {
                        if (result.hasData()) {
                            mSessionData = result.getData();
                        }
                    }
                },
                callback
        );
    }

    public ServiceTask login(String email, String password,
                             IServiceCallback<AuthResult> callback)
            throws Exception {
        LoginParameters parameters = new LoginParameters();
        parameters.email = email;

        byte[] epBytes = String.format("%s%s", email, password).getBytes("UTF-8");
        parameters.passwordHash = Hash.FNV1A_32(epBytes);

        return sendRequest("POST", "/auth/login", parameters, LoginParameters.class,
                AuthResult.class, new IServiceCallback<AuthResult>() {
                    @Override
                    public void onEnd(ServiceResult<AuthResult> result) {
                        if (result.hasData()) {
                            mSessionData = result.getData();
                        }
                    }
                },
                callback
        );
    }

    public ServiceTask logout(IServiceCallback<Void> callback) throws Exception {
        return sendRequest("DELETE", "/auth/delete", new IServiceCallback<Void>() {
            @Override
            public void onEnd(ServiceResult<Void> result) {
                // Clear cookies before finishing up
                clearCookies();
                mSessionData = null;
            }
        }, callback);
    }
}