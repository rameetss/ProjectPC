package ca.projectpc.projectpc.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;

import java.util.ArrayList;
import java.util.List;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.ServiceTask;
import ca.projectpc.projectpc.api.service.PostService;

// TODO/NOTE: THIS IS ALL EXPERIMENTAL, DO NOT TOUCH.

// TODO: Show ads
// TODO: View Ad, if my ad, then show edit/delete/bookmark buttons on top right (action bar button)
// TODO: PostAd: Add FAB for upload, when pressed back, show dialog "Are you sure"
// TODO: Settings menu (clear bookmarks - stored locally in file, clear cache/permissions?) - Or remove settings?
// TODO: Messenger (get messages, show messages, show notifications, get messages since)

// TODO: Add location getting to this, so we can add "current location" (get last known gps coordinate)
public class EditAdActivity extends AppCompatActivity {
    private static final int MAX_IMAGES = 8;

    private LinearLayout mImageContainer;
    private EditText mTitleEditText;
    private EditText mDescriptionEditText;
    private Spinner mCurrenciesSpinner;
    private EditText mPriceEditText;
    private EditText mLocationEditText;
    private ChipsInput mTagsChipsInput;

    private ProgressDialog mProgressDialog;
    private ServiceTask mTask;

    private String mAdId;
    private List<ImageView> mImageViews;
    private Bitmap[] mImages;

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

        // Check for valid tags
        mTagsChipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(final ChipInterface chipInterface, int i) {
                // ...
            }

            @Override
            public void onChipRemoved(ChipInterface chipInterface, int i) {
                // ...
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
        mImages = new Bitmap[MAX_IMAGES];
        for (int i = 0; i < MAX_IMAGES; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setImageResource(R.drawable.ic_add_box_gray);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                    imageViewWidth,
                    imageViewHeight
            ));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: If image exists, show delete/set as main, otherwise show add image
                }
            });

            mImageContainer.addView(imageView);
            mImageViews.add(imageView);
        }

        // Check if ad is being edited or created
        Intent intent = getIntent();
        mAdId = intent.getStringExtra("id");

        if (mAdId != null) {
            // Download ad information
            try {
                final Context context = this;
                PostService service = Service.get(PostService.class);
                mTask = service.getPost(mAdId,
                        new IServiceCallback<PostService.GetPostResult>() {
                            @Override
                            public void onEnd(ServiceResult<PostService.GetPostResult> result) {
                                if (mProgressDialog != null) {
                                    mProgressDialog.dismiss();
                                }

                                if (result.isCancelled()) {
                                    return;
                                }

                                if (!result.hasError()) {
                                    // Get data
                                    PostService.GetPostResult data = result.getData();

                                    // Get currency
                                    SpinnerAdapter currencyAdapter = mCurrenciesSpinner.getAdapter();
                                    int currencyIndex = 0;
                                    for (; currencyIndex < currencyAdapter.getCount();
                                         currencyIndex++) {
                                        String currency = (String) currencyAdapter.getItem(currencyIndex);
                                        if (currency.equals(data.currency)) {
                                            break;
                                        }
                                    }

                                    // Update UI
                                    mTitleEditText.setText(data.title);
                                    mDescriptionEditText.setText(data.body);
                                    mCurrenciesSpinner.setSelection(currencyIndex);
                                    mPriceEditText.setText(data.price.toString());
                                    if (data.location != null) {
                                        mLocationEditText.setText(data.location);
                                    }

                                    // Add tags
                                    for (String tag : data.tags) {
                                        mTagsChipsInput.addChip(tag, "");
                                    }

                                    // Download images
                                    for (String imageId : data.imageIds) {

                                    }
                                } else {
                                    // The API failed to complete the request and returned an
                                    // exception
                                    result.getException().printStackTrace();
                                    Toast.makeText(
                                            context,
                                            R.string.service_unable_to_process_request,
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }
                        });

                // Show progress dialog
                mProgressDialog = ProgressDialog.show(this,
                        getString(R.string.title_activity_edit_ad),
                        getString(R.string.prompt_wait), true, true,
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                if (mTask != null && !mTask.isCancelled()) {
                                    mTask.cancel();
                                }
                            }
                        }
                );
            } catch (Exception ex) {
                // Unable to get service (internal error)
                ex.printStackTrace();
                Toast.makeText(this, R.string.service_internal_error, Toast.LENGTH_LONG).show();
            }
        }
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
        builder.setMessage(mAdId == null
                ? R.string.prompt_discard_post
                : R.string.prompt_discard_post_changes
        );
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
        // TODO: Show are you sure message (are you sure you wish to post ad/make the changes?)

        // TODO: Upload...
        Toast.makeText(this, "Saving ad...", Toast.LENGTH_LONG).show();
        finish();
    }
}
