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

package ca.projectpc.projectpc.api.service;

import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.ServiceTask;
import ca.projectpc.projectpc.utility.Hash;

/**
 * Auth service API, provides authentication functionality like logging in, creating an account and
 * ending a session
 */
public class AuthService extends Service {
    /**
     * User role type
     */
    public class RoleType {
        public static final int Admin = 0;
        public static final int Moderator = 1;
        public static final int User = 2;
    }

    /**
     * Used for internal communication
     * /auth/create
     */
    private class CreateAccountParameters {
        String email;
        String firstName;
        String lastName;
        String userName;
        int passwordHash;
    }

    /**
     * Used for internal communication
     * /auth/login
     */
    private class LoginParameters {
        String email;
        int passwordHash;
    }

    /**
     * User session data, containing basic user information
     */
    public class SessionData implements Cloneable {
        public boolean authorized;
        public String userId;
        public String userName;
        public String firstName;
        public String lastName;
        public String email;
        public int role;

        /**
         * Create clone of session data, as it should be read-only
         *
         * @return Session data cloned
         * @throws CloneNotSupportedException Thrown if cloning not supported by OS
         */
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    /**
     * Alias for SessionData, contains session information returned
     * when logging in/creating account
     */
    public class AuthResult extends SessionData {
    }

    private SessionData mSessionData;

    /**
     * Clone the session data, throwing exception and return it.
     *
     * @return Cloned session data
     * @throws Exception Cloning data may throw Exception
     */
    public SessionData getSessionData() throws Exception {
        return (SessionData) mSessionData.clone();
    }

    /**
     * Accepting user account registration credentials, formatting them together in byte arrays,
     * hashing the password and sending the HTTP POST request /auth/create.
     *
     * @param email     Users desired email
     * @param firstName Users first name
     * @param lastName  Users surname
     * @param userName  Users desired username
     * @param password  Users desired password
     * @param callback  Service callback to receive success or fail result
     * @return The result of the HTTP sendRequest
     * @throws Exception sendRequest may throw Exception
     */
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

    /**
     * Accepting user account credentials, formatting them together in byte arrays,
     * hashing the password and sending the HTTP POST request /auth/login.
     *
     * @param email Users email
     * @param password Users password
     * @param callback Service callback to receive success or fail result
     * @return The result of the HTTP sendRequest method
     * @throws Exception sendRequest may throw Exception
     */
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

    /**
     * Logging out a logged in user by sending HTTP DELETE request, clearing cookies and
     * setting the current session data to null.
     *
     * @param callback Service callback to receive success or fail result
     * @return The result of the HTTP sendRequest method
     * @throws Exception sendRequest may throw Exception
     */
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