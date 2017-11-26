package ca.projectpc.projectpc.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.services.PostService;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class StartupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        // Initialize API
        Service.setServerUrl("http://192.168.0.102:4040/api/");
        Service.setTimeout(1000);

        // Navigate to login/home activity
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void uploadImage(View view) {
        // setup easy image
        EasyImage.configuration(this)
                .setImagesFolderName("project-pc")
                .saveInAppExternalFilesDir()
                .saveInRootPicturesDirectory();

        EasyImage.openChooserWithGallery(this, "Select an image", 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // ...
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePicked(File file, EasyImage.ImageSource source, int type) {
                //if (source == EasyImage.ImageSource.CAMERA) {
                    try {
                        PostService service = Service.get(PostService.class);
                        /*
                        service.uploadImage(file, new IServiceCallback<Void>() {
                            @Override
                            public void onEnd(ServiceResult<Void> result) {
                                Log.d("ProjectPC", result.getException().toString());
                                //if (!result.hasError()) {
                                    Toast.makeText(getBaseContext(), "Uploaded!", Toast.LENGTH_LONG).show();
                                //}
                            }
                        });
                        */
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                //}
            }
        });

        super.onActivityResult(requestCode, resultCode, data);
    }
}