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
import ca.projectpc.projectpc.api.services.AuthService;

public class LoginActivity extends AppCompatActivity implements Dialog.OnCancelListener {
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;

    private SharedPreferences mPreferences;
    private ServiceTask mLoginTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get controls
        mEmailEditText = (EditText) findViewById(R.id.login_email_edit_text);
        mPasswordEditText = (EditText) findViewById(R.id.login_password_edit_text);
        mLoginButton = (Button) findViewById(R.id.login_login_button);

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

        // Get preferences
        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);

        // Check if intent information was passed
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

    public void onRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mLoginTask != null) {
            mLoginTask.cancel();
        }
    }
}


