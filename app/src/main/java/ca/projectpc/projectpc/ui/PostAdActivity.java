package ca.projectpc.projectpc.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.bumptech.glide.Glide;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.ServiceResultCode;
import ca.projectpc.projectpc.api.ServiceTask;
import ca.projectpc.projectpc.api.service.PostService;
import ca.projectpc.projectpc.api.service.result.BasicIdResult;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class PostAdActivity extends AppCompatActivity {
    private static final int MAX_IMAGES = 8;

    private LinearLayout mImageContainer;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ad);

        // Enable back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Get controls
        mTitleEditText = (EditText) findViewById(R.id.edit_post_title);
        mDescriptionEditText = (EditText) findViewById(R.id.edit_post_description);
        mCurrenciesSpinner = (Spinner) findViewById(R.id.edit_post_currencies);
        mPriceEditText = (EditText) findViewById(R.id.edit_post_price);
        mLocationEditText = (EditText) findViewById(R.id.edit_post_location);
        mTagsChipsInput = (ChipsInput) findViewById(R.id.edit_post_tags);

        // Do show detailed chip info
        mTagsChipsInput.setShowChipDetailed(false);

        // Create tags list
        mTags = new ArrayList<>();

        // Check for valid tags
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
                        String label = charSequence.subSequence(0, length - 1).toString();
                        mTagsChipsInput.addChip(label, "");
                    }
                }
            }
        });

        // Get image container
        mImageContainer = (LinearLayout) findViewById(R.id.edit_post_image_container);

        // Get image view size
        int imageViewWidth = (int) getResources().getDimension(R.dimen.post_image_width);
        int imageViewHeight = (int) getResources().getDimension(R.dimen.post_image_height);

        // Create image views
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

        // Get category
        Intent intent = getIntent();
        mCategory = intent.getStringExtra("category");

        // Set title
        setTitle(String.format(getString(R.string.title_activity_post_in), mCategory));
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

                    // Use glide to open into the image view
                    Glide.with(getBaseContext()).load(file).into(imageView);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.makeText(getBaseContext(),
                            R.string.error_unable_open_image,
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        });
        super.onActivityResult(requestCode, resultCode, data);
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

    private void pressBack() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_alert_discard);
        builder.setMessage(R.string.prompt_discard_post);
        builder.setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == AlertDialog.BUTTON_POSITIVE) {
                    pressBack();
                }
            }
        });
        builder.setNegativeButton(R.string.action_no, null);
        builder.show();
    }

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

        // TODO: Do geocoding (if time permits)

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_alert_are_you_sure);
        builder.setMessage(R.string.prompt_are_you_sure_post);
        builder.setPositiveButton(R.string.action_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == AlertDialog.BUTTON_POSITIVE) {
                    // Show progress dialog
                    mProgressDialog = ProgressDialog.show(PostAdActivity.this,
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

                    // Upload ad
                    uploadAd(title, mCategory, mTags, Double.parseDouble(price), currency,
                            description, location, null, null);
                }
            }
        });
        builder.setNegativeButton(R.string.action_no, null);
        builder.show();
    }

    private void uploadAd(String title, String category, List<String> tags, Double price,
                          String currency, String description, String location,
                          @Nullable Double latitude, @Nullable Double longitude) {
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

                            if (!result.hasError() && result.hasData()
                                    && result.getCode() == ServiceResultCode.Ok) {
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
                mTask = service.uploadImage(postId, thumbnail, b64Image,
                        new IServiceCallback<BasicIdResult>() {
                            @Override
                            public void onEnd(ServiceResult<BasicIdResult> result) {
                                mTask = null;
                                if (result.isCancelled()) {
                                    return;
                                }

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

    private void setListed(String postId) {
        try {
            final Context context = this;
            final PostService service = Service.get(PostService.class);
            mTask = service.setListed(postId, new IServiceCallback<BasicIdResult>() {
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

                    if (!result.hasError() && result.hasData()
                            && result.getCode() == ServiceResultCode.Ok) {
                        Toast.makeText(context, R.string.prompt_ad_posted,
                                Toast.LENGTH_LONG).show();
                        finish();
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
}