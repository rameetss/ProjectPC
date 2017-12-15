package ca.projectpc.projectpc.ui.adapter;

import android.content.Context;
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

import java.text.SimpleDateFormat;
import java.util.List;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.ServiceTask;
import ca.projectpc.projectpc.api.service.PostService;

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

        // TODO: Onclick card/imageview/message imageview

        // TODO: Get location and measure distance

        // Set text
        holder.mTitleTextView.setText(post.title);
        holder.mDateTextView.setText(df.format(post.createdAt));
        holder.mPriceTextView.setText(String.format("%.2f", post.price));
        holder.mCurrencyTextView.setText(post.currency);

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

                            if (!result.hasError() && result.hasData()) {
                                // TODO/NOTE: This isn't working all the time, but we will assume
                                // it does
                                Log.d("PostAdapter",
                                        String.format("Downloaded image %s", post.thumbnailId));

                                byte[] buffer = Base64.decode(result.getData().imageData,
                                        Base64.DEFAULT);

                                // Set image view
                                Glide.with(mContext).load(buffer).into(holder.mThumbnailImageView);
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

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
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
