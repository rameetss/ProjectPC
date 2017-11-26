package ca.projectpc.projectpc.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.Service;

public class StartupActivity extends AppCompatActivity {
    private ImagePicker mImagePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        // Initialize API
        Service.setServerUrl("http://192.168.0.102:4040/api/"); // TODO: TEST
        Service.setTimeout(1000);

        // TODO: Do startup procedure

        // Navigate to login/home activity
        Intent intent = new Intent(this, HomeActivity.class);
        // TODO/NOTE: Debug, disabled.
        //startActivity(intent);


        File file = new File(getExternalFilesDir(null), "post-cap.jpg");
        mImagePicker = new ImagePicker(this, ImagePicker.Options.All, Uri.fromFile(file));
    }

    public void uploadImage(View view) {
        mImagePicker.pick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri image = mImagePicker.getImageUriFromResult(requestCode, resultCode, data);
        if (image != null) {
            ImageView imageView = findViewById(R.id.startup_image_test);
            Glide.with(this).load(new File(image.getPath())).into(imageView);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}