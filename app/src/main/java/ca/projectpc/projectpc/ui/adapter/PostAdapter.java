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

package ca.projectpc.projectpc.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.ServiceTask;
import ca.projectpc.projectpc.api.service.AuthService;
import ca.projectpc.projectpc.api.service.PostService;
import ca.projectpc.projectpc.ui.ShowAdActivity;
import ca.projectpc.projectpc.ui.glide.GlideApp;
import ca.projectpc.projectpc.utility.LatLong;

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

    /**
     * Called when creating a ViewHolder to inflate the specific ad.
     *
     * @param parent   Parent view to hold the ad row
     * @param viewType Required for the implemented method, not used
     * @return Inflated ViewHolder for the ad
     */
    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_ad, parent, false);
        return new PostViewHolder(view);
    }

    /**
     * Called to bind the post ViewHolder to the row position, and set all relevant fields
     * to the ad data.
     *
     * @param holder PostViewHolder containing the post data
     * @param position Integer position in the ViewHolder
     */
    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {
        final PostService.Post post = mPosts.get(position);

        SimpleDateFormat df = new SimpleDateFormat("MMM dd");

        // Open post details on click
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ShowAdActivity.class);
                intent.putExtra("postId", post.id);
                mContext.startActivity(intent);
            }
        });

        // Open send message on click
        holder.mMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send message
                Toast.makeText(mContext, "Send message!", Toast.LENGTH_LONG).show();
            }
        });

        // Measure distance between locations
        String distanceString = "";
        Location location = LatLong.getLocation(mContext);
        if (location != null && post.latitude != null && post.longitude != null) {
            double distance = LatLong.getDistanceBetweenLocations(post.latitude, post.longitude,
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

        // Download image
        try {
            AuthService authService = Service.get(AuthService.class);

            // Show message icon if enabled
            if (mShowSendMessageIcon
                    && !post.authorId.equals(authService.getSessionData().userId)) {
                holder.mMessageImageView.setVisibility(View.VISIBLE);
            } else {
                holder.mMessageImageView.setVisibility(View.INVISIBLE);
            }

            // Check if image exists
            if (post.thumbnailId == null) {
                return;
            }

            PostService postService = Service.get(PostService.class);
            holder.mDownloadImageTask = postService.downloadImage(post.thumbnailId,
                    new IServiceCallback<PostService.DownloadImageResult>() {
                        @Override
                        public void onEnd(ServiceResult<PostService.DownloadImageResult> result) {
                            if (result.isCancelled()) {
                                return;
                            }

                            if (!result.hasError()) {
                                // Decode image
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

    /**
     * Initialize the image downloader when the RecycledView is initialized.
     *
     * @param holder ViewHolder containing ad data.
     */
    @Override
    public void onViewRecycled(PostViewHolder holder) {
        // Cancel download event for anything downloading
        if (holder.mDownloadImageTask != null && !holder.mDownloadImageTask.isCancelled()) {
            holder.mDownloadImageTask.cancel();
        }

        super.onViewRecycled(holder);
    }

    /**
     * Fetch the number of posts
     *
     * @return number of posts
     */
    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    /**
     * Custom ViewHolder class for each ad post
     */
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
