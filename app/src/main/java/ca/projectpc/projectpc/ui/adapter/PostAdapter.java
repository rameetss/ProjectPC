package ca.projectpc.projectpc.ui.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.ServiceTask;
import ca.projectpc.projectpc.api.service.PostService;
import ca.projectpc.projectpc.ui.ShowAdActivity;
import ca.projectpc.projectpc.ui.glide.GlideApp;
//import ca.projectpc.projectpc.ui.glide.GlideApp;
//import ca.projectpc.projectpc.ui.glide.GlideAppModule;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private Context mContext;
    private List<PostService.Post> mPosts;
    private boolean mShowSendMessageIcon;

    public PostAdapter(List<PostService.Post> posts) {
        mPosts = posts;
        mShowSendMessageIcon = true;
    }

    public void setShowSendMessageIcon(boolean show) {
        mShowSendMessageIcon = show;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_ad, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {
        final PostService.Post post = mPosts.get(position);

        SimpleDateFormat df = new SimpleDateFormat("MMM dd");

        // Open post details on click
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ShowAdActivity.class);
                mContext.startActivity(intent);
            }
        });

        // Measure distance between locations
        String distanceString = "";
        Location location = getLocation();
        if (location != null && post.latitude != null && post.longitude != null) {
            double distance = getDistanceBetweenLocations(post.latitude, post.longitude,
                    location.getLatitude(), location.getLongitude());
            if (distance < 1000) {
                distanceString = new DecimalFormat("0.0").format(distance) + "m";
            } else {
                distanceString = new DecimalFormat("0.0").format(distance / 1000) + "km";
            }
        }

        // Set text
        holder.mTitleTextView.setText(post.title);
        holder.mDateTextView.setText(df.format(post.createdAt));
        holder.mPriceTextView.setText(String.format("%.2f", post.price));
        holder.mCurrencyTextView.setText(post.currency);
        holder.mDistanceTextView.setText(distanceString);

        // Show message icon if enabled
        holder.mMessageImageView.setVisibility(mShowSendMessageIcon
                ? View.VISIBLE : View.INVISIBLE);

        // Check if image exists
        if (post.thumbnailId == null) {
            return;
        }

        // Download image
        try {
            PostService service = Service.get(PostService.class);
            holder.mDownloadImageTask = service.downloadImage(post.thumbnailId,
                    new IServiceCallback<PostService.DownloadImageResult>() {
                        @Override
                        public void onEnd(ServiceResult<PostService.DownloadImageResult> result) {
                            if (result.isCancelled()) {
                                return;
                            }

                            if (!result.hasError()) {
                                // TODO/NOTE: This isn't working all the time, but we will assume
                                // it does
                                Log.d("PostAdapter",
                                        String.format("Downloaded image %s", post.thumbnailId));

                                byte[] buffer = Base64.decode(result.getData().imageData,
                                        Base64.DEFAULT);

                                // Set image view
                                GlideApp.with(mContext)
                                        .load(buffer)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .into(holder.mThumbnailImageView);
                            } else {
                                // The API failed to complete the request and returned an exception
                                result.getException().printStackTrace();
                                Toast.makeText(mContext, R.string.service_unable_to_process_request,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } catch (Exception ex) {
            // Unable to get service (internal error)
            ex.printStackTrace();
            Toast.makeText(mContext, R.string.service_internal_error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onViewRecycled(PostViewHolder holder) {
        // Cancel download event for anything downloading
        if (holder.mDownloadImageTask != null && !holder.mDownloadImageTask.isCancelled()) {
            holder.mDownloadImageTask.cancel();
        }

        super.onViewRecycled(holder);
    }

    private Location getLocation() {
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        LocationManager locationManager =
                (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        return null;
    }

    private static double getDistanceBetweenLocations(double latitude1, double longitude1,
                                                      double latitude2, double longitude2) {
        double latitude = Math.toRadians(latitude2 - latitude1);
        double longitude = Math.toRadians(longitude2 - longitude1);

        double a = Math.pow(Math.sin(latitude / 2), 2)
                + Math.cos(Math.toRadians(latitude1))
                * Math.cos(Math.toRadians(latitude2))
                * Math.pow(Math.sin(longitude / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Radius of Earth * c * 1000
        return 6378.1 * c * 1000; // Meters
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        CardView mCardView;
        ImageView mThumbnailImageView;
        ImageView mMessageImageView;
        TextView mTitleTextView;
        TextView mDateTextView;
        TextView mDistanceTextView;
        TextView mPriceTextView;
        TextView mCurrencyTextView;

        ServiceTask mDownloadImageTask;

        public PostViewHolder(View itemView) {
            super(itemView);

            mCardView = (CardView) itemView.findViewById(R.id.item_ad_card);
            mThumbnailImageView = (ImageView) itemView.findViewById(R.id.item_ad_thumbnail);
            mMessageImageView = (ImageView) itemView.findViewById(R.id.item_ad_message);
            mTitleTextView = (TextView) itemView.findViewById(R.id.item_ad_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.item_ad_date);
            mDistanceTextView = (TextView) itemView.findViewById(R.id.item_ad_distance);
            mPriceTextView = (TextView) itemView.findViewById(R.id.item_ad_price);
            mCurrencyTextView = (TextView) itemView.findViewById(R.id.item_ad_currency);
        }
    }
}
