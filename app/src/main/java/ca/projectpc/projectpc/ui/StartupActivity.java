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
        Service.setServerUrl("http://192.168.0.102:4040/api/"); // TODO: TEST
        Service.setTimeout(5000);

        // TODO: Do startup procedure

        // Navigate to login/home activity
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}