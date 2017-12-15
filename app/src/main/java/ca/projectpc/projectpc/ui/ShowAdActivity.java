package ca.projectpc.projectpc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.Task;
import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.ServiceTask;
import ca.projectpc.projectpc.api.service.PostService;


public class ShowAdActivity extends AppCompatActivity {
    private LinearLayout mImageContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ad);

        // Enable back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Get UI

        // Get image container
        mImageContainer = (LinearLayout) findViewById(R.id.show_ad_image_container);

        // Get post id
        Intent intent = getIntent();
        String postId = intent.getStringExtra("postId");

        // Get ad info
        downloadAd(postId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        // TODO/NOTE: Task cancellation

        super.onBackPressed();
    }

    private void downloadAd(String postId) {
        try {
            final Context context = this;
            PostService service = Service.get(PostService.class);
            service.getPost(postId, new IServiceCallback<PostService.GetPostResult>() {
                @Override
                public void onEnd(ServiceResult<PostService.GetPostResult> result) {
                    // TODO: Loading dialog?

                    if (result.isCancelled()) {
                        return;
                    }

                    if (!result.hasError()) {
                        PostService.GetPostResult data = result.getData();

                        // Set title
                        setTitle(data.title);

                        // TODO: set other info

                        // Create image views
                        for (String imageId : data.imageIds) {
                            ImageView imageView = new ImageView(context);
                            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            ));
                            mImageContainer.addView(imageView);

                            // Schedule image download
                            downloadImage(imageId, imageView);
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
            Toast.makeText(this, R.string.service_internal_error,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void downloadImage(String imageId, final ImageView imageView) {
        try {
            final Context context = this;
            final PostService service = Service.get(PostService.class);

            service.downloadImage(imageId, new IServiceCallback<PostService.DownloadImageResult>() {
                @Override
                public void onEnd(ServiceResult<PostService.DownloadImageResult> result) {
                    if (result.isCancelled()) {
                        return;
                    }

                    if (!result.hasError()) {
                        // Decode image
                        byte[] buffer = Base64.decode(result.getData().imageData, Base64.DEFAULT);

                        // Load into image view
                        Glide.with(context).load(buffer).into(imageView);
                    } else {
                        result.getException().printStackTrace();
                        Toast.makeText(context,
                                getString(R.string.service_unable_to_process_request),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception ex) {
            // Unable to get service (internal error)
            ex.printStackTrace();
            Toast.makeText(this, R.string.service_internal_error,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void onSendMessage(View view) {

    }
}
