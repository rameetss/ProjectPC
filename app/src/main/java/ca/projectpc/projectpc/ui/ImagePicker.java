package ca.projectpc.projectpc.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import ca.projectpc.projectpc.R;

public class ImagePicker {
    public enum Options {
        Camera,
        Gallery,
        All
    }

    private static final int REQUEST_CAPTURE_IMAGE = 10000;
    private static final int REQUEST_SELECT_IMAGE = 10001;

    private Activity mActivity;
    private Options mOptions;
    private Uri mImageLocation;

    public ImagePicker(Activity activity, Options options, Uri imageLocation) {
        mActivity = activity;
        mOptions = options;
        mImageLocation = imageLocation;
    }

    public void pick() {
        if (mOptions == Options.Camera) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageLocation);

            mActivity.startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
        } else if (mOptions == Options.Gallery) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent = Intent.createChooser(
                    intent,
                    mActivity.getString(R.string.image_picker_select_image)
            );

            mActivity.startActivityForResult(intent, REQUEST_SELECT_IMAGE);
        } else if (mOptions == Options.All) {
            // Create image selection dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(mActivity.getString(R.string.image_picker_select_image_via));

            // Add items
            String[] options = {
                    mActivity.getString(R.string.image_picker_camera),
                    mActivity.getString(R.string.image_picker_gallery)
            };
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageLocation);

                        mActivity.startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
                    } else if (which == 1) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        intent = Intent.createChooser(
                                intent,
                                mActivity.getString(R.string.image_picker_select_image_via)
                        );

                        mActivity.startActivityForResult(intent, REQUEST_SELECT_IMAGE);
                    }
                }
            });

            builder.show();
        }
    }

    public Uri getImageUriFromResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAPTURE_IMAGE) {
                return mImageLocation;
            } else if (requestCode == REQUEST_SELECT_IMAGE) {
                return data.getData();
            }
        }
        return null;
    }
}
