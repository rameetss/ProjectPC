package ca.projectpc.projectpc.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Arrays;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.service.SystemService;
import pl.aprilapps.easyphotopicker.EasyImage;


public class StartupActivity extends AppCompatActivity {
    public static final int REQUEST_PERMISSIONS = 10000;
    public static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
    };

    public static final String API_ENDPOINT = "https://ppc.indigogames.ca/api/";
    public static final int API_TIMEOUT = 25000;

    /**
     *
     * @param savedInstanceState
     * Initialize API, EasyImage and grab all necessary permissions on startup
     */
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
                .saveInRootPicturesDirectory()
                .setCopyExistingPicturesToPublicLocation(true);

        // Get required permissions (File reading permissions, GPS permissions)
        boolean granted = true;
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                granted = false;
            }
        }

        if (granted) {
            checkSystemAndStart();
        } else {
            ActivityCompat.requestPermissions(this,
                    PERMISSIONS, REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS && grantResults.length > 0) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    // Fail, exit app
                    Toast.makeText(getBaseContext(), R.string.permissions_request_denied,
                            Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            }

            checkSystemAndStart();
        }
    }

    private void checkSystemAndStart() {
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

                        Toast.makeText(getBaseContext(), R.string.service_unable_to_connect,
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();

            Toast.makeText(this, R.string.service_internal_error, Toast.LENGTH_LONG).show();
            finish();
        }
    }
}