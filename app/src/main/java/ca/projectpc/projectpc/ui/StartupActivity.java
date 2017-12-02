package ca.projectpc.projectpc.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.ServiceResultCode;
import ca.projectpc.projectpc.api.services.AuthService;
import ca.projectpc.projectpc.api.services.PostService;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class StartupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        // Initialize API
        Service.setServerUrl("http://ppc.indigogames.ca/api/");
        Service.setTimeout(5000);

        // Navigate to login/home activity
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    /*
    // Login button was pressed
    public void login(View view) {
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
    */

    public void uploadImage(View view) {
        // setup easy image
        EasyImage.configuration(this)
                .setImagesFolderName("local")
                .saveInAppExternalFilesDir()
                .saveInRootPicturesDirectory();

        EasyImage.openChooserWithGallery(this, "Select an image", 0);

        /*try {
            AuthService authService = Service.get(AuthService.class);
            authService.login("example@interwebs.com", "1234",
                    new IServiceCallback<AuthService.AuthResult>() {
                        @Override
                        public void onEnd(final ServiceResult<AuthService.AuthResult> authResult) {
                            try {
                                if (!authResult.hasError()) {
                                    Log.d("StartupActivity", "Authenticated");

                                    PostService postService = Service.get(PostService.class);
                                    postService.downloadImage("5a20d9dc08f4f04b5c13a935", new IServiceCallback<PostService.DownloadImageResult>() {
                                        @Override
                                        public void onEnd(ServiceResult<PostService.DownloadImageResult> downloadImageResult) {
                                            try {
                                                if (!downloadImageResult.hasError()) {
                                                    Log.d("StartupActivity", "Image downloaded");

                                                    byte[] imageData = Base64.decode(downloadImageResult.getData().imageData, Base64.DEFAULT);
                                                    Glide.with(getBaseContext()).load(imageData).into((ImageView)findViewById(R.id.startup_image_test));
                                                }
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePicked(final File file, EasyImage.ImageSource source, int type) {
                try {
                    AuthService authService = Service.get(AuthService.class);
                    authService.login("example@interwebs.com", "1234",
                    //authService.create("example@interwebs.com", "Eyaz", "Rehman", "Imposter", "1234",
                            new IServiceCallback<AuthService.AuthResult>() {
                                @Override
                                public void onEnd(final ServiceResult<AuthService.AuthResult> authResult) {
                                    try {
                                        if (!authResult.hasError()) {
                                            Log.d("StartupActivity", "Authenticated");

                                            final PostService postService = Service.get(PostService.class);
                                            postService.createPost("This is an ad!", "motherboard", null,
                                                    100.99, "CAD", "Buy it now bro",
                                                    new IServiceCallback<PostService.BasicIdResult>() {
                                                        @Override
                                                        public void onEnd(ServiceResult<PostService.BasicIdResult> createPostResult) {
                                                            try {
                                                                if (!createPostResult.hasError()) {
                                                                    Log.d("StartupActivity", "Created post");

                                                                    Toast.makeText(getBaseContext(), "Created post", Toast.LENGTH_LONG).show();

                                                                    // Load image
                                                                    byte[] buffer = new byte[(int)file.length()];
                                                                    FileInputStream stream = new FileInputStream(file);
                                                                    int bytesRead = stream.read(buffer);
                                                                    if (bytesRead > 0) {
                                                                        String base64Image = Base64.encodeToString(buffer, Base64.DEFAULT);
                                                                        postService.uploadImage(createPostResult.getData().id, true, base64Image, new IServiceCallback<PostService.BasicIdResult>() {
                                                                            @Override
                                                                            public void onEnd(ServiceResult<PostService.BasicIdResult> uploadImageResult) {
                                                                                try {
                                                                                    if (!uploadImageResult.hasError()) {
                                                                                        Log.d("StartupActivity", "Uploaded image");
                                                                                        Toast.makeText(getBaseContext(), "Uploaded image!", Toast.LENGTH_LONG).show();
                                                                                    } else {
                                                                                        throw uploadImageResult.getException();
                                                                                    }
                                                                                } catch (Exception ex) {
                                                                                    ex.printStackTrace();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                } else {
                                                                    throw createPostResult.getException();
                                                                }
                                                            } catch (Exception ex) {
                                                                ex.printStackTrace();
                                                            }
                                                        }
                                                    }
                                            );
                                        } else {
                                            throw authResult.getException();
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        super.onActivityResult(requestCode, resultCode, data);
    }
}