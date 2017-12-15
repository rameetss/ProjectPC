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

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.jetbrains.annotations.NotNull;

import ca.projectpc.projectpc.R;

/**
 * Abstract class for all activities to be based on
 */
public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    protected ConstraintLayout mContentContainer;
    protected Toolbar mToolbar;
    protected FloatingActionButton mFloatingActionButton;
    protected NavigationView mNavigationView;

    /**
     * Save away any dynamic instance state in activity into the given Bundle,
     * to be later received in onCreate(Bundle) if the activity needs to be re-created.
     *
     * @param savedInstanceState Last saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);

        mContentContainer = (ConstraintLayout) findViewById(R.id.base_content_container);

        mToolbar = (Toolbar) findViewById(R.id.base_toolbar);
        setSupportActionBar(mToolbar);

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.base_fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickFloatingActionButton(view);
            }
        });
        mFloatingActionButton.hide();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.inflateMenu(getNavigationMenuId());
    }

    /**
     * Called when the back button is pressed on the device
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Called only activated if user attempts to navigate to different page.
     * Used for implementing navigation to other pages
     *
     * @param item Navigation item which was clicked
     * @return Success or failure as a Boolean
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NotNull MenuItem item) {
        int id = item.getItemId();

        // Only trigger event if the ID is different than the current one
        if (id != getNavigationId()) {
            onNavigationItemSelected(id);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Generates menu options for action bar
     *
     * @param menu Menu to generate options for
     * @return Whether generation was successful or not
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int id = getMenuId();
        if (id != 0) {
            getMenuInflater().inflate(getMenuId(), menu);
        }
        return true;
    }

    /**
     * Set content view for inner container, instead of for entire activity
     * @param layoutResID Layout to inflate
     */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        View childView = View.inflate(this, layoutResID, null);
        mContentContainer.addView(childView);
    }

    public void showFloatingActionButton() {
        mFloatingActionButton.show();
    }

    public void hideFloatingActionButton() {
        mFloatingActionButton.hide();
    }

    /**********************************************************
     Must be implemented
     *********************************************************/
    public abstract void onClickFloatingActionButton(View view);

    public abstract void onNavigationItemSelected(int id);

    public abstract int getNavigationMenuId();

    public abstract int getNavigationId();

    public abstract int getMenuId();

    @Override
    public abstract boolean onOptionsItemSelected(MenuItem item);
}
