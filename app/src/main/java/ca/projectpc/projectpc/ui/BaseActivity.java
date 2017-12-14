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
import android.widget.EditText;

import org.jetbrains.annotations.NotNull;

import ca.projectpc.projectpc.R;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    protected ConstraintLayout mContentContainer;
    protected Toolbar mToolbar;
    protected FloatingActionButton mFloatingActionButton;
    protected NavigationView mNavigationView;

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int id = getMenuId();
        if (id != 0) {
            getMenuInflater().inflate(getMenuId(), menu);
        }
        return true;
    }

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
