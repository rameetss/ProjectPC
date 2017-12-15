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

package ca.projectpc.projectpc.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.ServiceResultCode;
import ca.projectpc.projectpc.api.ServiceTask;
import ca.projectpc.projectpc.api.service.AuthService;

/**
 * Provides login functionality to user
 */
public class LoginActivity extends AppCompatActivity implements Dialog.OnCancelListener {
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;

    private SharedPreferences mPreferences;
    private ServiceTask mLoginTask;

    /**
     * Called when activity is loaded, loads preferences and automatically logs user in
     * if credentials are stored in the preferences.
     *
     * @param savedInstanceState Last saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize user input fields and login button
        mEmailEditText = (EditText) findViewById(R.id.login_email);
        mPasswordEditText = (EditText) findViewById(R.id.login_password);
        mLoginButton = (Button) findViewById(R.id.login_login);

        mPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    onLogin(v);
                    return true;
                }

                return false;
            }
        });

        // Get saved user preferences
        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

        // Fetch email and password from intent
        Intent callingIntent = getIntent();
        String email = callingIntent.getStringExtra("email");
        String password = callingIntent.getStringExtra("password");

        if (email == null || email.isEmpty()) {
            // Get stored login information to login quickly
            email = mPreferences.getString("email", null);
            password = mPreferences.getString("password", null);
        }

        mEmailEditText.setText(email);
        mPasswordEditText.setText(password);
        if (email != null && !email.isEmpty()) {
            onLogin(mLoginButton);
        }
    }

    /**
     * Called when user clicks login button. Initialize the authentication service, check
     * entered information for validity, and attempt a login with the provided information.
     * Display a login dialog in the interim.
     *
     * @param view The layout login button that was clicked.
     */
    public void onLogin(View view) {
        try {
            AuthService authService = Service.get(AuthService.class);

            final String email = mEmailEditText.getText().toString();
            final String password = mPasswordEditText.getText().toString();

            // Check if the specified information is valid
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, R.string.error_invalid_email, Toast.LENGTH_LONG).show();
                mEmailEditText.requestFocus();
                return;
            }
            if (password.length() < RegisterActivity.MIN_PASSWORD_LENGTH) {
                Toast.makeText(this, R.string.error_invalid_password, Toast.LENGTH_LONG).show();
                mPasswordEditText.requestFocus();
                return;
            }

            final Context context = this;

            // Show progress dialog
            final ProgressDialog dialog = ProgressDialog.show(this,
                    getString(R.string.title_activity_login),
                    getString(R.string.prompt_wait), true, true, this);

            // Try to log in with specified email and password
            mLoginTask = authService.login(email, password,
                    new IServiceCallback<AuthService.AuthResult>() {
                        @Override
                        public void onEnd(ServiceResult<AuthService.AuthResult> result) {
                            // Hide progress dialog
                            dialog.dismiss();
                            mLoginTask = null;

                            if (result.isCancelled()) {
                                return;
                            }

                            if (!result.hasError()) {
                                // Check if we logged in successfully
                                if (result.getCode() == ServiceResultCode.Ok) {
                                    SharedPreferences.Editor editor = mPreferences.edit();
                                    editor.putString("email", email);
                                    editor.putString("password", password);
                                    editor.apply();

                                    Intent intent = new Intent(context, SearchActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else if (result.getCode() == ServiceResultCode.InvalidCredentials) {
                                    Toast.makeText(context, getString(R.string.error_invalid_credentials),
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                // The API failed to complete the request and returned an exception
                                result.getException().printStackTrace();
                                Toast.makeText(context, R.string.service_unable_to_process_request,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } catch (Exception ex) {
            // Unable to get service (internal error)
            ex.printStackTrace();
            Toast.makeText(this, R.string.service_internal_error, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Called when the user clicks the register button. Simply send the user to the
     * registration page.
     *
     * @param view The layout register button that was clicked.
     */
    public void onRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Called when the back button is pressed on the device
     */
    @Override
    public void onBackPressed() {
        if (mLoginTask != null && !mLoginTask.isCancelled()) {
            mLoginTask.cancel();
            return;
        }
        super.onBackPressed();
    }

    /**
     * Called when the progress dialog is cancelled, terminates the login task keeping
     * the user on the login page.
     *
     * @param dialog DialogInterface where cancel was clicked.
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        if (mLoginTask != null && !mLoginTask.isCancelled()) {
            mLoginTask.cancel();
        }
    }
}


