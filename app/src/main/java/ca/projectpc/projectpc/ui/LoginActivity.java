package ca.projectpc.projectpc.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.ServiceResultCode;
import ca.projectpc.projectpc.api.services.AuthService;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onLogin(View view) {
        try {
            AuthService authService = Service.get(AuthService.class);

            EditText txtEmail = (EditText)findViewById(R.id.login_email_edit_text);
            EditText txtPassword = (EditText)findViewById(R.id.login_password_edit_text);

            String email = txtEmail.getText().toString();
            String password = txtPassword.getText().toString();

            // Try to log in with specified email and password
            authService.login(email, password, new IServiceCallback<AuthService.AuthResult>() {
                @Override
                public void onEnd(ServiceResult<AuthService.AuthResult> result) {
                    if (!result.hasError()) {
                        // Check if we logged in successfully
                        if (result.getCode() == ServiceResultCode.Ok) {
                            Log.d("LoginActivity", "Login successful!");
                        } else if (result.getCode() == ServiceResultCode.InvalidCredentials) {
                            Log.d("LoginActivity", "Invalid email or password");
                        }
                    } else {
                        // The API failed to complete the request and returned an exception
                        result.getException().printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            // Unable to get service (internal error)
            ex.printStackTrace();
        }
    }
}


