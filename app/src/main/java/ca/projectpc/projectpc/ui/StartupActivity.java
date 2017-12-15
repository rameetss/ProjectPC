package ca.projectpc.projectpc.ui;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.service.SystemService;
import pl.aprilapps.easyphotopicker.EasyImage;

public class StartupActivity extends AppCompatActivity {
    public static final String API_ENDPOINT = "https://ppc.indigogames.ca/api/";
    public static final int API_TIMEOUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        // Initialize API
        Service.setServerUrl(API_ENDPOINT);
        Service.setTimeout(API_TIMEOUT);

        // Initialize EasyImage
        EasyImage.configuration(this)
                .setImagesFolderName("local")
                .saveInAppExternalFilesDir()
                .saveInRootPicturesDirectory();

        // TODO: Get required permissions (File reading permissions, GPS permissions)
        // Permissions
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA};

        ActivityCompat.requestPermissions(this, permissions, 5);

        // Check system service
        try {
            SystemService systemService = Service.get(SystemService.class);
            systemService.getVersion(new IServiceCallback<SystemService.VersionResult>() {
                @Override
                public void onEnd(ServiceResult<SystemService.VersionResult> result) {
                    try {
                        if (!result.hasError()) {
                            // Navigate to login activity
                            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            throw result.getException();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();

                        Toast.makeText(getBaseContext(), "Unable to connect to server",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();

            Toast.makeText(this, "Unable to get service", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}