package ca.projectpc.projectpc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.MenuItem;
import android.widget.Toast;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.services.SystemService;

public class SearchActivity extends BaseActivity {
    private int mNavigationId;
    private int mMenuId;
    private String mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        }
        setTitle(getString(categoryId));

        // Set content view
        setContentView(R.layout.activity_search);

        // Show floating action button (to create new post)
        showFloatingActionButton();

        // TODO: Fetch data from server depending on the category (mCategory)
    }

    @Override
    public void onClickFloatingActionButton(View view) {
        // TODO: Go to create post activity
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

        return true;
    }

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

    @Override
    public void onNavigationItemSelected(int id) {
        Class c = null;
        if (id == R.id.nav_inbox) {
            // TODO: Show inbox activity
        } else if (id == R.id.nav_settings) {
            // TODO: Show settings activity
        } else if (id == R.id.nav_sign_out) {
            // TODO: Sign out and go to login activity
        } else {
            c = SearchActivity.class;
        }

        if (c != null) {
            Intent intent = new Intent(this, c);
            intent.putExtra("internal_navigation_id", id);
            startActivity(intent);
            finish();
        }
    }
}
