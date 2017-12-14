package ca.projectpc.projectpc.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.ServiceResultCode;
import ca.projectpc.projectpc.api.services.AuthService;

public class RegisterActivity extends AppCompatActivity implements DialogInterface.OnClickListener {
    public static final int MIN_USER_NAME_LENGTH = 6;
    public static final int MIN_FIRST_NAME_LENGTH = 2;
    public static final int MIN_LAST_NAME_LENGTH = 2;
    public static final int MIN_PASSWORD_LENGTH = 8;

    private EditText mUserNameEditText;
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private Button mRegisterButton;
    private TextView mLoginTextView;

    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Get controls
        mUserNameEditText = (EditText) findViewById(R.id.register_user_name_edit_text);
        mFirstNameEditText = (EditText) findViewById(R.id.register_first_name_edit_text);
        mLastNameEditText = (EditText) findViewById(R.id.register_last_name_edit_text);
        mEmailEditText = (EditText) findViewById(R.id.register_email_edit_text);
        mPasswordEditText = (EditText) findViewById(R.id.register_password_edit_text);
        mConfirmPasswordEditText = (EditText) findViewById(R.id.register_confirm_password_edit_text);
        mRegisterButton = (Button) findViewById(R.id.register_register_button);
        mLoginTextView = (TextView) findViewById(R.id.register_login_text_view);

        mConfirmPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    onRegister(v);
                    return true;
                }

                return false;
            }
        });

        // Get preferences
        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
    }

    private void setUiEnabled(boolean enabled) {
        mUserNameEditText.setEnabled(enabled);
        mFirstNameEditText.setEnabled(enabled);
        mLastNameEditText.setEnabled(enabled);
        mEmailEditText.setEnabled(enabled);
        mPasswordEditText.setEnabled(enabled);
        mConfirmPasswordEditText.setEnabled(enabled);
        mRegisterButton.setEnabled(enabled);
        mLoginTextView.setEnabled(enabled);
    }

    public void onRegister(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_create_account);
        builder.setMessage(R.string.prompt_confirm_register);
        builder.setPositiveButton(R.string.action_yes, this);
        builder.setNegativeButton(R.string.action_no, this);
        builder.show();
    }

    public void onLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(DialogInterface d, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            try {
                AuthService authService = Service.get(AuthService.class);

                String userName = mUserNameEditText.getText().toString();
                String firstName = mFirstNameEditText.getText().toString();
                String lastName = mLastNameEditText.getText().toString();
                final String email = mEmailEditText.getText().toString();
                final String password = mPasswordEditText.getText().toString();
                String confirmPassword = mConfirmPasswordEditText.getText().toString();

                // Check if the specified information is valid
                if (userName.length() < MIN_USER_NAME_LENGTH) {
                    Toast.makeText(this, R.string.error_invalid_user_name,
                            Toast.LENGTH_LONG).show();
                    mUserNameEditText.requestFocus();
                    return;
                }
                if (firstName.length() < MIN_FIRST_NAME_LENGTH) {
                    Toast.makeText(this, R.string.error_invalid_first_name,
                            Toast.LENGTH_LONG).show();
                    mFirstNameEditText.requestFocus();
                    return;
                }
                if (lastName.length() < MIN_LAST_NAME_LENGTH) {
                    Toast.makeText(this, R.string.error_invalid_last_name,
                            Toast.LENGTH_LONG).show();
                    mLastNameEditText.requestFocus();
                    return;
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(this, R.string.error_invalid_email, Toast.LENGTH_LONG).show();
                    mEmailEditText.requestFocus();
                    return;
                }
                if (password.length() < MIN_PASSWORD_LENGTH) {
                    Toast.makeText(this, R.string.error_invalid_password, Toast.LENGTH_LONG).show();
                    mPasswordEditText.requestFocus();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(this, R.string.error_passwords_no_match,
                            Toast.LENGTH_LONG).show();
                    mPasswordEditText.requestFocus();
                    return;
                }

                final Context context = this;

                // Show progress dialog
                final ProgressDialog dialog = ProgressDialog.show(this,
                        getString(R.string.title_activity_register),
                        getString(R.string.prompt_wait), true, false);

                // Try to register with specified info
                authService.createAccount(email, firstName, lastName, userName, password,
                        new IServiceCallback<AuthService.AuthResult>() {
                            @Override
                            public void onEnd(ServiceResult<AuthService.AuthResult> result) {
                                // Hide progress dialog
                                dialog.dismiss();

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
                                    } else if (result.getCode()
                                            == ServiceResultCode.UserAlreadyExists) {
                                        Toast.makeText(context, R.string.error_user_already_exists,
                                                Toast.LENGTH_LONG).show();
                                    } else if (result.getCode()
                                            == ServiceResultCode.AlreadyAuthenticated) {
                                        // Take user to login activity
                                        Intent intent = new Intent(context, LoginActivity.class);
                                        intent.putExtra("email", email);
                                        intent.putExtra("password", password);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    // The API failed to complete the request and returned an
                                    // exception
                                    result.getException().printStackTrace();
                                    Toast.makeText(context,
                                            R.string.service_unable_to_process_request,
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
    }
}
