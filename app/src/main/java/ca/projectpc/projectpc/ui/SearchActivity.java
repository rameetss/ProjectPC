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

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.service.AuthService;
import ca.projectpc.projectpc.api.service.PostService;
import ca.projectpc.projectpc.ui.adapter.PostAdapter;

public class SearchActivity extends BaseActivity
        implements SwipeRefreshLayout.OnRefreshListener {
    private int mNavigationId;
    private int mMenuId;
    private String mCategory;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private SearchView mSearchView;

    private List<PostService.Post> mPosts;
    private PostAdapter mAdapter;

    /**
     * Save any dynamic instance state in activity into the given Bundle,
     * to be later received in onCreate(Bundle) if the activity needs to be re-created.
     *
     * @param savedInstanceState Last saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Get passed information
        Intent callingIntent = getIntent();
        mNavigationId = callingIntent.getIntExtra("internal_navigation_id", R.id.nav_home);
        mMenuId = callingIntent.getIntExtra("internal_menu_id", 0);

        // Get category
        int categoryId = navIdToCategoryStringId(mNavigationId);
        mCategory = categoryId != 0 ? getString(categoryId) : "";

        // Set toolbar title
        if (categoryId == 0) {
            categoryId = R.string.app_name;
        } else {
            // Show floating action button (to create new post)
            showFloatingActionButton();

            // Show menu
            if (mMenuId == 0) {
                mMenuId = R.menu.menu_search;
            }
        }

        // Set title
        setTitle(getString(categoryId));

        try {
            // Get auth service
            AuthService authService = Service.get(AuthService.class);
            AuthService.SessionData sessionData = authService.getSessionData();

            // Update username and email on sidebar
            View navigationRootView = mNavigationView.getHeaderView(0);
            TextView titleTextView = (TextView) navigationRootView.findViewById(R.id.nav_header_title);
            TextView emailTextView = (TextView) navigationRootView.findViewById(R.id.nav_header_email);
            titleTextView.setText(String.format("%s (%s %s)", sessionData.userName,
                    sessionData.firstName, sessionData.lastName));
            emailTextView.setText(sessionData.email);
        } catch (Exception ex) {
            ex.printStackTrace();
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

        // Refresh
        refresh();
    }

    /**
     * Handle when the user presses the back button
     */
    @Override
    public void onBackPressed() {
        if (!mSearchView.isIconified()) {
            mSearchView.setIconified(true);
            return;
        }

        super.onBackPressed();
    }

    /**
     * Handle the user clicking the floating add button
     *
     * @param view The floating button
     */
    @Override
    public void onClickFloatingActionButton(View view) {
        Intent intent = new Intent(this, PostAdActivity.class);
        intent.putExtra("category", mCategory);
        startActivity(intent);
    }

    @Override
    public int getNavigationId() {
        // This is for the current navigation menu or 0 if none
        return mNavigationId;
    }

    @Override
    public int getMenuId() {
        // This is for the menu to use (R.menu.BLAH) or 0 if none
        return mMenuId;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // This is for menu options
        if (item.getItemId() == R.id.menu_action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return true;
    }

    /**
     * Called when creating options menu, used to initialize the SearchManager service
     * and associate the searchable configuration with the respective SearchView.
     *
     * @param menu Menu to generate options for
     * @return success or failure as Boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem = menu.findItem(R.id.menu_action_search);
        if (menuItem != null) {
            mSearchView = (SearchView) menuItem.getActionView();
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    // Submit to results activity
                    Intent intent = new Intent(getBaseContext(), SearchResultsActivity.class);
                    intent.putExtra("category", mCategory);
                    intent.putExtra("query", s);
                    startActivity(intent);

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });
        }

        return true;
    }

    /**
     * Refresh the SwipeLayout by initializing the PostService, fetching the relevant ads,
     * and displaying them.
     */
    private void refresh() {
        // Set as complete, for now
        mSwipeRefreshLayout.setRefreshing(true);

        try {
            final Context context = this;
            PostService service = Service.get(PostService.class);
            if (mCategory.equals("")) {
                mAdapter.setShowSendMessageIcon(false);
                service.getMyPosts(new IServiceCallback<PostService.GetPostsResult>() {
                    @Override
                    public void onEnd(ServiceResult<PostService.GetPostsResult> result) {
                        mSwipeRefreshLayout.setRefreshing(false);

                        // Clear adapter
                        mPosts.clear();

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
            } else {
                mAdapter.setShowSendMessageIcon(true);
                service.getAllPostsForCategory(mCategory, null,
                        new IServiceCallback<PostService.GetPostsResult>() {
                            @Override
                            public void onEnd(ServiceResult<PostService.GetPostsResult> result) {
                                mSwipeRefreshLayout.setRefreshing(false);

                                // Clear adapter
                                mPosts.clear();

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
            }
        } catch (Exception ex) {
            // Unable to get service (internal error)
            ex.printStackTrace();
            Toast.makeText(this, R.string.service_internal_error, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Call refresh()
     */
    @Override
    public void onRefresh() {
        // Refresh
        refresh();
    }

    /**
     * Get the string name for the category based on the id passed.
     *
     * @param id Category layout id
     * @return String name of the category
     */
    private int navIdToCategoryStringId(int id) {
        int strId = 0;
        switch (id) {
            case R.id.nav_cpu:
                strId = R.string.category_cpu;
                break;
            case R.id.nav_ram:
                strId = R.string.category_ram;
                break;
            case R.id.nav_motherboard:
                strId = R.string.category_motherboard;
                break;
            case R.id.nav_gpu:
                strId = R.string.category_gpu;
                break;
            case R.id.nav_power_supply:
                strId = R.string.category_power_supply;
                break;
            case R.id.nav_cooling:
                strId = R.string.category_cooling;
                break;
            case R.id.nav_hdd:
                strId = R.string.category_hdd;
                break;
            case R.id.nav_ssd:
                strId = R.string.category_ssd;
                break;
            case R.id.nav_peripherals:
                strId = R.string.category_peripherals;
                break;
            case R.id.nav_monitor:
                strId = R.string.category_monitor;
                break;
            case R.id.nav_case:
                strId = R.string.category_case;
                break;
            case R.id.nav_misc:
                strId = R.string.category_misc;
                break;
        }
        return strId;
    }

    /**********************************************************
     INTERNAL
     *********************************************************/
    @Override
    public int getNavigationMenuId() {
        return R.menu.menu_base_drawer;
    }

    /**
     * Start the relevant activity based on the provided layout id selected from
     * the navigation drawer. If the selected navigation item is logout, initialize the
     * authentication service to logout the user.
     *
     * @param id The id of the navigation item selected
     */
    @Override
    public void onNavigationItemSelected(int id) {
        if (id == R.id.nav_inbox) {
            // Show inbox activity
            Intent intent = new Intent(this, InboxActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_sign_out) {
            try {
                AuthService authService = Service.get(AuthService.class);
                authService.logout(new IServiceCallback<Void>() {
                    @Override
                    public void onEnd(ServiceResult<Void> result) {
                        if (!result.hasError()) {
                            // Remove auto-login information
                            SharedPreferences preferences = getSharedPreferences("CurrentUser",
                                    MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("email", null);
                            editor.putString("password", null);
                            editor.apply();

                            // Return to login activity
                            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // The API failed to complete the request and returned an exception
                            result.getException().printStackTrace();
                            Toast.makeText(getBaseContext(), R.string.service_unable_to_process_request,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } catch (Exception ex) {
                // Unable to get service (internal error)
                ex.printStackTrace();
                Toast.makeText(this, R.string.service_internal_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("internal_navigation_id", id);
            startActivity(intent);
            finish();
        }
    }
}
