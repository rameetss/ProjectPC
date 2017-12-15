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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.ServiceTask;
import ca.projectpc.projectpc.api.service.PostService;
import ca.projectpc.projectpc.api.service.result.BasicIdResult;
import ca.projectpc.projectpc.ui.glide.GlideApp;
import ca.projectpc.projectpc.utility.LatLong;

public class ShowAdActivity extends AppCompatActivity {
    private LinearLayout mImageContainer;
    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    private TextView mCurrencyTextView;
    private TextView mPriceTextView;
    private TextView mLocationTextView;
    private TextView mDistanceTextView;
    private FloatingActionButton mSendMessageButton;

    private String mPostId;
    private List<ServiceTask> mTasks;

    /**
     * Save any dynamic instance state in activity into the given Bundle,
     * to be later received in onCreate(Bundle) if the activity needs to be re-created.
     *
     * @param savedInstanceState Last saved state
     */
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
        mSendMessageButton = (FloatingActionButton) findViewById(R.id.show_ad_send);

        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send message

            }
        });

        // Create task queue
        mTasks = new ArrayList<>();

        // Get image container
        mImageContainer = (LinearLayout) findViewById(R.id.show_ad_image_container);

        // Get post id
        Intent intent = getIntent();
        mPostId = intent.getStringExtra("postId");

        // Get ad info
        downloadAd(mPostId);
    }

    /**
     * When user clicks on options menu item, inflate the show ad menu and return true.
     *
     * @param menu Menu item that was clicked
     * @return Success or failure as a Boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_ad, menu);
        return true;
    }

    /**
     * Handle what happens when the user selects a menu option. Either send them back to
     * the home page or send them to the edit post page.
     *
     * @param item Menu item selected
     * @return Success or failure as a Boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_action_edit:
                Intent intent = new Intent(this, EditAdActivity.class);
                intent.putExtra("postId", mPostId);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Cancel the show ad, send user back to ad listings
     */
    @Override
    public void onBackPressed() {
        // Cancel all the tasks
        for (ServiceTask task : mTasks) {
            if (task != null && !task.isCancelled() && task.isRunning()) {
                task.cancel();
            }
        }

        mTasks.clear();

        super.onBackPressed();
    }

    private void deleteAd() {
        try {
            final Context context = this;
            PostService service = Service.get(PostService.class);
            service.removePost(mPostId, new IServiceCallback<BasicIdResult>() {
                @Override
                public void onEnd(ServiceResult<BasicIdResult> result) {
                    if (!result.hasError()) {
                        Toast.makeText(context, R.string.prompt_ad_deleted,
                                Toast.LENGTH_LONG).show();
                        finish();
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

    /**
     * Called when the activity begins to download ad data from
     * the server, setting the appropriate text fields afterwards.
     *
     * @param postId Database identification for the ad to fetch data for
     */
    private void downloadAd(String postId) {
        // TODO: Fix design for image views, they're not right
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
                                distanceString = new DecimalFormat("0.0").format(distance) + "m";
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
                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
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

    /**
     * Called to download the ad images uploaded upon posting. Done separately from
     * downloadAd to reduce cluttering. Called recursively until all ad specific images
     * are downloaded.
     *
     * @param imageId   Current ID of the image to download
     * @param imageView Current position in the images array
     */
    private void downloadImage(String imageId, final ImageView imageView) {
        try {
            final Context context = this;
            final PostService service = Service.get(PostService.class);

            // TODO: Make cancellable (put them in a task list)
            ServiceTask task = service.downloadImage(imageId,
                    new IServiceCallback<PostService.DownloadImageResult>() {
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
            mTasks.add(task);
        } catch (Exception ex) {
            // Unable to get service (internal error)
            ex.printStackTrace();
            Toast.makeText(this, R.string.service_internal_error,
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Open message activity in order to send message to user who
     * posted the ad when the user has clicked the message button.
     *
     * @param view The send message button
     */
    public void onSendMessage(View view) {

    }
}
