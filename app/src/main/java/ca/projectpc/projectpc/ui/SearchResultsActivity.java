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
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.ServiceTask;
import ca.projectpc.projectpc.api.service.PostService;
import ca.projectpc.projectpc.ui.adapter.PostAdapter;

public class SearchResultsActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    private List<PostService.Post> mPosts;
    private PostAdapter mAdapter;

    private String mCategory;
    private List<String> mTags;
    private ServiceTask mTask;

    /**
     * Save any dynamic instance state in activity into the given Bundle,
     * to be later received in onCreate(Bundle) if the activity needs to be re-created.
     *
     * @param savedInstanceState Last saved state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Enable back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Setup swipe refresh layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.search_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(
                R.color.colorAccent,
                R.color.colorPrimary
        );

        // Create linear layout manager for recycler view
        mLinearLayoutManager = new LinearLayoutManager(this);

        // Find recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.search_ads_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        // Set up recycler view
        mPosts = new ArrayList<>();
        mAdapter = new PostAdapter(mPosts);
        mRecyclerView.setAdapter(mAdapter);

        // Fetch the categories from the SearchActivity intent
        Intent intent = getIntent();
        mCategory = intent.getStringExtra("category");
        String query = intent.getStringExtra("query");
        mTags = Arrays.asList(query.split(" "));

        // Set title
        setTitle(String.format(
                getString(R.string.title_activity_search_for),
                mCategory
        ));

        refresh();
    }

    /**
     * Send the user back to the home page when they've clicked the options item
     *
     * @param item The option item selected
     * @return Success or failure as a Boolean
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
     * Cancel the fetching of results when the user presses the back button
     */
    @Override
    public void onBackPressed() {
        if (mTask != null && !mTask.isCancelled()) {
            mTask.cancel();
            return;
        }

        super.onBackPressed();
    }

    /**
     * Call the refresh method after user has swiped
     */
    @Override
    public void onRefresh() {
        refresh();
    }


    /**
     * Refresh method to update search results. Initilizes the PostService, updates the adapter,
     * and adds all posts.
     */
    private void refresh() {
        // Set as complete, for now
        mSwipeRefreshLayout.setRefreshing(true);

        try {
            final Context context = this;
            PostService service = Service.get(PostService.class);
            mTask = service.getAllPostsForCategory(mCategory, mTags,
                    new IServiceCallback<PostService.GetPostsResult>() {
                        @Override
                        public void onEnd(ServiceResult<PostService.GetPostsResult> result) {
                            mSwipeRefreshLayout.setRefreshing(false);

                            // Clear adapter
                            mPosts.clear();

                            if (result.isCancelled()) {
                                return;
                            }

                            if (!result.hasError()) {
                                mPosts.addAll(Arrays.asList(result.getData().result));

                                // Notify data changed
                                mAdapter.notifyDataSetChanged();
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
            Toast.makeText(this, R.string.service_internal_error, Toast.LENGTH_LONG).show();
        }
    }
}
