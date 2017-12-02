package ca.projectpc.projectpc.api.services;

import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceTask;
import ca.projectpc.projectpc.utility.Hash;

public class AuthService extends Service {
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

    public class AuthResult {
        public boolean authorized;
        public String userId;
        public int roleType;
    }

    public ServiceTask create(String email, String firstName, String lastName, String userName,
                              String password, final IServiceCallback<AuthResult> callback)
            throws Exception {
        CreateAccountParameters parameters = new CreateAccountParameters();
        parameters.email = email;
        parameters.firstName = firstName;
        parameters.lastName = lastName;
        parameters.userName = userName;

        byte[] epBytes = String.format("%s%s", email, password).getBytes("UTF-8");
        parameters.passwordHash = Hash.FNV1A_32(epBytes);

        return sendRequest("POST", "/auth/create", parameters, CreateAccountParameters.class,
                AuthResult.class, null, callback);
    }

    public ServiceTask login(String email, String password,
                             final IServiceCallback<AuthResult> callback)
            throws Exception {
        LoginParameters parameters = new LoginParameters();
        parameters.email = email;

        byte[] epBytes = String.format("%s%s", email, password).getBytes("UTF-8");
        parameters.passwordHash = Hash.FNV1A_32(epBytes);

        return sendRequest("POST", "/auth/login", parameters, LoginParameters.class,
                AuthResult.class, null, callback);
    }

    public ServiceTask delete(final IServiceCallback<Void> callback) throws Exception {
        return sendRequest("DELETE", "/auth/delete", null, callback);
    }
}