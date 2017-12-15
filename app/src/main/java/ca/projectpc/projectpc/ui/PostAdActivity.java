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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.ServiceResultCode;
import ca.projectpc.projectpc.api.ServiceTask;
import ca.projectpc.projectpc.api.service.PostService;
import ca.projectpc.projectpc.api.service.result.BasicIdResult;
import ca.projectpc.projectpc.ui.glide.GlideApp;
import ca.projectpc.projectpc.utility.LatLong;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

// TODO: Fix up comments
public class PostAdActivity extends AppCompatActivity {
    private static final int MAX_IMAGES = 8;

    private EditText mTitleEditText;
    private EditText mDescriptionEditText;
    private Spinner mCurrenciesSpinner;
    private EditText mPriceEditText;
    private EditText mLocationEditText;
    private ChipsInput mTagsChipsInput;

    private ServiceTask mTask;
    private ProgressDialog mProgressDialog;

    private String mCategory;
    private List<ImageView> mImageViews;
    private File[] mImages;
    private File mThumbnailImage;
    private List<String> mTags;
    private Location mLocation;

    /**
     * Save away any dynamic instance state in activity into the given Bundle,
     * to be later received in onCreate(Bundle) if the activity needs to be re-created.
     * @param savedInstanceState Last saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ad);

        // Change ActionBar from having NavigationDrawer to having a back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Initialize all relevant user input fields
        mTitleEditText = (EditText) findViewById(R.id.edit_post_title);
        mDescriptionEditText = (EditText) findViewById(R.id.edit_post_description);
        mCurrenciesSpinner = (Spinner) findViewById(R.id.edit_post_currencies);
        mPriceEditText = (EditText) findViewById(R.id.edit_post_price);
        mLocationEditText = (EditText) findViewById(R.id.edit_post_location);
        mTagsChipsInput = (ChipsInput) findViewById(R.id.edit_post_tags);

        mTagsChipsInput.setShowChipDetailed(false);
        // Create chips tag list for storing later entered chips
        mTags = new ArrayList<>();

        // Listener for when the user is adding tags to 'chip' them out,
        // displaying categorically.
        mTagsChipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(final ChipInterface chipInterface, int i) {
                mTags.add(chipInterface.getLabel());
            }

            @Override
            public void onChipRemoved(ChipInterface chipInterface, int i) {
                String label = chipInterface.getLabel();
                if (mTags.contains(label)) {
                    mTags.remove(label);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence) {
                int length = charSequence.length();
                if (length > 0) {
                    char lastChar = charSequence.charAt(length - 1);
                    if (lastChar == ' ') {
                        String label = charSequence.toString().trim();
                        if (label.trim().length() == 0) {
                            return;
                        }

                        mTagsChipsInput.addChip(label, "");
                    }
                }
            }
        });

        LinearLayout mImageContainer = (LinearLayout) findViewById(R.id.edit_post_image_container);
        int imageViewWidth = (int) getResources().getDimension(R.dimen.post_image_width);
        int imageViewHeight = (int) getResources().getDimension(R.dimen.post_image_height);

        mImageViews = new ArrayList<>();
        mImages = new File[MAX_IMAGES];
        for (int i = 0; i < MAX_IMAGES; i++) {
            final int imageIndex = i;
            final ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setImageResource(R.drawable.ic_add_box_gray);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                    imageViewWidth,
                    imageViewHeight
            ));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final File image = mImages[imageIndex];
                    if (image != null) {
                        // Show menu
                        String[] options = {
                                getString(R.string.action_set_primary),
                                getString(R.string.action_remove)
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(PostAdActivity.this);
                        builder.setTitle(getString(R.string.prompt_image_options));
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                if (which == 0) {
                                    // Set as primary
                                    mThumbnailImage = image;
                                } else if (which == 1) {
                                    // Remove image
                                    mImages[imageIndex] = null;
                                    if (image == mThumbnailImage) {
                                        mThumbnailImage = null;
                                    }
                                    imageView.setImageResource(R.drawable.ic_add_box_gray);
                                }
                            }
                        });
                        builder.show();
                    } else {
                        // Show add image dialog
                        EasyImage.openChooserWithGallery(PostAdActivity.this,
                                getString(R.string.prompt_select_image), imageIndex);
                    }
                }
            });

            mImageContainer.addView(imageView);
            mImageViews.add(imageView);
        }

        // Get ad category sent by the intent
        Intent intent = getIntent();
        mCategory = intent.getStringExtra("category");

        // Set title
        setTitle(String.format(getString(R.string.title_activity_post_in), mCategory));

        // Set location EditText based on last known location
        mLocation = LatLong.getLocation(this);
        if (mLocation != null) {
            mLocationEditText.setText(getLocationString(mLocation));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePicked(File file, EasyImage.ImageSource source, int imageIndex) {
                try {
                    // Get image view
                    ImageView imageView = mImageViews.get(imageIndex);

                    // Store image for later uploading
                    mImages[imageIndex] = file;

                    // If this is the first image, set as primary
                    if (mThumbnailImage == null) {
                        mThumbnailImage = file;
                    }

                    // Use glide to open into the image view
                    GlideApp.with(getBaseContext())
                            .load(file)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(imageView);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.makeText(getBaseContext(),
                            R.string.error_unable_open_image,
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                // Remove temporary file if cancelled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(PostAdActivity.this);
                    if (photoFile != null) {
                        photoFile.delete();
                    }
                }
            }
        });
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Called when a menu item is selected to navigate the user back to
     * the home page.
     *
     * @param item MenuItem that was selected
     * @return If the selection was handled
     */
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

    /**
     * Back pressed functionality
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_alert_discard);
        builder.setMessage(R.string.prompt_discard_post);
        builder.setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == AlertDialog.BUTTON_POSITIVE) {
                    PostAdActivity.super.onBackPressed();
                }
            }
        });
        builder.setNegativeButton(R.string.action_no, null);
        builder.show();
    }

    /**
     * Called when the user clicks the save button. Check all fields for valid entries,
     * display an 'Are you sure' dialog, then call uploadAd passing relevant ad data.
     *
     * @param view The layout save button that was clicked.
     */
    public void onSave(View view) {
        // Check input
        final String title = mTitleEditText.getText().toString();
        if (title.isEmpty()) {
            Toast.makeText(this, R.string.error_invalid_title,
                    Toast.LENGTH_LONG).show();
            mTitleEditText.requestFocus();
            return;
        }

        final String description = mDescriptionEditText.getText().toString();
        if (description.isEmpty()) {
            Toast.makeText(this, R.string.error_invalid_description,
                    Toast.LENGTH_LONG).show();
            mDescriptionEditText.requestFocus();
            return;
        }

        final String price = mPriceEditText.getText().toString();
        if (price.isEmpty()) {
            Toast.makeText(this, R.string.error_invalid_price,
                    Toast.LENGTH_LONG).show();
            mPriceEditText.requestFocus();
            return;
        }

        final String location = mLocationEditText.getText().toString();
        if (location.isEmpty()) {
            Toast.makeText(this, R.string.error_invalid_location,
                    Toast.LENGTH_LONG).show();
            mLocationEditText.requestFocus();
            return;
        }

        final String currency = mCurrenciesSpinner.getSelectedItem().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_alert_are_you_sure);
        builder.setMessage(R.string.prompt_are_you_sure_post);
        builder.setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == AlertDialog.BUTTON_POSITIVE) {
                    // Get location coordinates (used later for measuring distance between user
                    // and ad poster
                    Double latitude = null;
                    Double longitude = null;
                    if (mLocation != null) {
                        latitude = mLocation.getLatitude();
                        longitude = mLocation.getLongitude();
                    }

                    // Upload ad
                    uploadAd(title, mCategory, mTags, Double.parseDouble(price), currency,
                            description, location);
                }
            }
        });
        builder.setNegativeButton(R.string.action_no, null);
        builder.show();
    }

    /**
     * Method to take the passed ad parameters and send the data to the server
     * to create an ad and list it.
     *
     * @param title Ad title
     * @param category Ad category
     * @param tags Any tags added for the ad
     * @param price Ad price
     * @param currency Currency the ad price is in
     * @param description Ad description
     * @param location Location of the Ad
     */
    private void uploadAd(String title, String category, List<String> tags, Double price,
                          String currency, String description, String location) {
        // Show progress dialog
        mProgressDialog = ProgressDialog.show(this,
                getString(R.string.title_progress_posting),
                getString(R.string.prompt_wait), true, true,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        if (mTask != null && !mTask.isCancelled()) {
                            mTask.cancel();
                        }
                    }
                });

        // Upload
        try {
            final Context context = this;
            final PostService service = Service.get(PostService.class);
            mTask = service.createPost(title, mCategory, mTags, price, currency, description,
                    location, null, null, new IServiceCallback<BasicIdResult>() {
                        @Override
                        public void onEnd(ServiceResult<BasicIdResult> result) {
                            mTask = null;
                            if (result.isCancelled()) {
                                return;
                            }

                            if (!result.hasError()) {
                                String postId = result.getData().id;

                                File lastImage = null;
                                for (File image : mImages) {
                                    if (image != null) {
                                        lastImage = image;
                                    }
                                }

                                for (File image : mImages) {
                                    if (image != null) {
                                        uploadImage(postId, image, image == mThumbnailImage,
                                                image == lastImage);
                                    }
                                }
                            } else {
                                // The API failed to complete the request and returned an
                                // exception
                                result.getException().printStackTrace();
                                Toast.makeText(context, R.string.service_unable_to_process_request,
                                        Toast.LENGTH_LONG).show();
                                if (mProgressDialog != null) {
                                    mProgressDialog.dismiss();
                                    mProgressDialog = null;
                                }
                            }
                        }
                    });
        } catch (Exception ex) {
            // Unable to get service (internal error)
            ex.printStackTrace();
            Toast.makeText(this, R.string.service_internal_error,
                    Toast.LENGTH_LONG).show();
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }

    /**
     * Upload the images for the ad to the server. Method was generated separately from
     * uploadAd in order to reduce cluttering.
     *
     * @param postId Database identification of the post to match images to.
     * @param image The file location of the image to upload.
     * @param thumbnail Is the image being passed a thumbnail?
     * @param lastImage Is the image being passed the last of the images?
     */
    private void uploadImage(final String postId, File image, boolean thumbnail,
                             final boolean lastImage) {
        try {
            final Context context = this;
            final PostService service = Service.get(PostService.class);
            try {
                // Load into buffer
                FileInputStream stream = new FileInputStream(image);

                // Read file
                byte[] buffer = new byte[(int) image.length()];
                stream.read(buffer);

                // Base64 encode
                String b64Image = Base64.encodeToString(buffer, Base64.DEFAULT);
                service.uploadImage(postId, thumbnail, b64Image,
                        new IServiceCallback<BasicIdResult>() {
                            @Override
                            public void onEnd(ServiceResult<BasicIdResult> result) {
                                if (!result.hasError()) {
                                    if (result.getCode() == ServiceResultCode.Ok) {
                                        if (lastImage) {
                                            setListed(postId);
                                        }
                                    } else if (result.getCode() == ServiceResultCode.Unauthorized) {
                                        Toast.makeText(context, R.string.service_unauthorized,
                                                Toast.LENGTH_LONG).show();
                                        if (mProgressDialog != null) {
                                            mProgressDialog.dismiss();
                                            mProgressDialog = null;
                                        }
                                    }
                                } else {
                                    // The API failed to complete the request and returned an
                                    // exception
                                    result.getException().printStackTrace();
                                    Toast.makeText(context,
                                            R.string.service_unable_to_process_request,
                                            Toast.LENGTH_LONG).show();
                                    if (mProgressDialog != null) {
                                        mProgressDialog.dismiss();
                                        mProgressDialog = null;
                                    }
                                }
                            }
                        });
            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(this, R.string.error_unable_open_image, Toast.LENGTH_LONG).show();
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (Exception ex) {
            // Unable to get service (internal error)
            ex.printStackTrace();
            Toast.makeText(this, R.string.service_internal_error,
                    Toast.LENGTH_LONG).show();
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }

    /**
     * Called in order to list an ad publicly. Initialize the PostService, send the postId to
     * the server and await result.
     *
     * @param postId Post identification to tell the server what to list
     */
    private void setListed(String postId) {
        try {
            final Context context = this;
            final PostService service = Service.get(PostService.class);
            mTask = service.setListed(postId, true, new IServiceCallback<BasicIdResult>() {
                @Override
                public void onEnd(ServiceResult<BasicIdResult> result) {
                    mTask = null;
                    if (result.isCancelled()) {
                        return;
                    }

                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }

                    if (!result.hasError()) {
                        Toast.makeText(context, R.string.prompt_ad_posted,
                                Toast.LENGTH_LONG).show();
                        finish();
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
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }

    /**
     * Called to fetch the proper string output for 'city, country' given a Location.
     *
     * @param location The location to Geocode into 'city, country'.
     * @return String in format "city, country".
     */
    private String getLocationString(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);

            // Get first address information
            Address address = addresses.get(0);

            return String.format("%s, %s",
                    address.getLocality(),
                    address.getCountryName()
            );
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return "";
    }
}