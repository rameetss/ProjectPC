package ca.projectpc.projectpc.ui;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.service.PostService;
import me.gujun.android.taggroup.TagGroup;

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
    private TagGroup mTagsTagGroup;

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
        mTagsTagGroup = (TagGroup) findViewById(R.id.edit_post_tags);

        // Check for valid tags
        // TODO: Replace tags lib

        // TODO: Get tag view, so on tag add, if tag contains space, show alert and remove tag
        // TODO: Replace tag view with the material design!

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

        // TODO: Check if ad is being edited or created
        Intent intent = getIntent();
        mAdId = intent.getStringExtra("id");

        if (mAdId != null) {
            // Download ad information
            try {
                PostService service = Service.get(PostService.class);
                //final ServiceTask getAdInfoTask =
                // TODO: Implement missing/important handlers on server and client

                // Show progress dialog
                final ProgressDialog dialog = ProgressDialog.show(this,
                        getString(R.string.title_activity_edit_ad),
                        getString(R.string.prompt_wait), true, true,
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                // TODO: cancel task
                            }
                        }
                );
            } catch (Exception ex) {
                ex.printStackTrace();
                // TODO: Show error message

                finish();
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

    public void onPost(View view) {
        // TODO: Show are you sure message (are you sure you wish to post ad/make the changes?)

        // TODO: Upload...
        Toast.makeText(this, "Posting ad...", Toast.LENGTH_LONG).show();
        finish();
    }
}
