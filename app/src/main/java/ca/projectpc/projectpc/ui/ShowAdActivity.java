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

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.Task;
import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.ServiceTask;
import ca.projectpc.projectpc.api.service.PostService;
import ca.projectpc.projectpc.ui.glide.GlideApp;
import ca.projectpc.projectpc.utility.LatLong;

// TODO: Add fab
public class ShowAdActivity extends AppCompatActivity {
    private LinearLayout mImageContainer;
    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    private TextView mCurrencyTextView;
    private TextView mPriceTextView;
    private TextView mLocationTextView;
    private TextView mDistanceTextView;

    private String mPostId;

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
        mTitleTextView = (TextView) findViewById(R.id.show_ad_title);
        mDescriptionTextView = (TextView) findViewById(R.id.show_ad_description);
        mCurrencyTextView = (TextView) findViewById(R.id.show_ad_currency);
        mPriceTextView = (TextView) findViewById(R.id.show_ad_price);
        mLocationTextView = (TextView) findViewById(R.id.show_ad_location);
        mDistanceTextView = (TextView) findViewById(R.id.show_ad_distance);

        // Get image container
        mImageContainer = (LinearLayout) findViewById(R.id.show_ad_image_container);

        // Get post id
        Intent intent = getIntent();
        mPostId = intent.getStringExtra("postId");

        // Get ad info
        downloadAd(mPostId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_ad, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_action_edit:
                Intent intent = new Intent(this, EditAdActivity.class);
                intent.putExtra("postId", mPostId);
                return true;
            case R.id.menu_action_bookmark:
                // TODO: Implement
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

                        // Measure distance between locations
                        String distanceString = "";
                        Location location = LatLong.getLocation(context);
                        if (location != null && data.latitude != null && data.longitude != null) {
                            double distance = LatLong.getDistanceBetweenLocations(data.latitude,
                                    data.longitude, location.getLatitude(),
                                    location.getLongitude());
                            if (distance < 1000) {
                                distanceString = new DecimalFormat("0.0").format(distance)
                                        + "m";
                            } else {
                                distanceString = new DecimalFormat("0.0").format(distance / 1000)
                                        + "km";
                            }
                        }

                        // Set other info
                        mTitleTextView.setText(data.title);
                        mDescriptionTextView.setText(data.body);
                        mCurrencyTextView.setText(data.currency);
                        mPriceTextView.setText(String.format("%.2f", data.price));
                        mLocationTextView.setText(data.location);
                        mDistanceTextView.setText(distanceString);

                        // Get image view size
                        int imageViewWidth = (int) getResources().getDimension(
                                R.dimen.show_image_width);
                        int imageViewHeight = (int) getResources().getDimension(
                                R.dimen.show_image_height);

                        // Create image views
                        for (String imageId : data.imageIds) {
                            ImageView imageView = new ImageView(context);
                            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                                    imageViewWidth,
                                    imageViewHeight
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
                        GlideApp.with(context)
                                .load(buffer)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(imageView);
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
