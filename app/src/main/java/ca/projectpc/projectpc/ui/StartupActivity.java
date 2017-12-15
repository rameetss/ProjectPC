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

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
    };

    public static final String API_ENDPOINT = "http://192.168.43.40:4040/api/"; //"http://s1.indigogames.ca:44008/api/";
    public static final String API_ENDPOINT = "http://192.168.43.40:4040/api/"; // "http://192.168.0.101:4040/api/";
    public static final int API_TIMEOUT = 10000;

    /**
     * Save away any dynamic instance state in activity into the given Bundle,
     * to be later received in onCreate(Bundle) if the activity needs to be re-created.
     *
     * @param savedInstanceState Last saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        // Initialize API
        Service.setServerUrl(API_ENDPOINT);
        Service.setTimeout(API_TIMEOUT);

        // Initialize EasyImage for quickly accessing users' gallery later
        EasyImage.configuration(this)
                .setImagesFolderName("local")
                .saveInAppExternalFilesDir()
                .saveInRootPicturesDirectory()
                .setCopyExistingPicturesToPublicLocation(true);

        // Get required permissions (File and GPS permissions)
        List<String> toRequest = new ArrayList<>();
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                toRequest.add(permission);
            }
        }
        if (toRequest.size() == 0) {
            checkSystemAndStart();
        } else {
            ActivityCompat.requestPermissions(this, toRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS);
        }
    }

    /**
     * Called after the requestPermission pop-up window has received input
     * from the user ('Allow' or 'Deny').
     * @param requestCode Identifying code for the permission request
     * @param permissions All requested permissions stored as a String[] array
     * @param grantResults All respective result codes for each permission
     */
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

    /**
     * Called to check if the server service is working and can be connected to.
     * If the check is successful navigate the user to the login page, otherwise
     * produce a Toast describing the connectivity issue.
     */
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

                        // Print error stack trace and produce Toast msg to notify user
                        // of connectivity issues
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