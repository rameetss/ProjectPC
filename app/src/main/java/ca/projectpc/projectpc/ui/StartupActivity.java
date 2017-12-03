package ca.projectpc.projectpc.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.Service;

public class StartupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        // Initialize API
        Service.setServerUrl("https://ppc.indigogames.ca/api/");
        Service.setTimeout(5000);

        // Navigate to login/home activity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}