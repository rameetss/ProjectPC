package ca.projectpc.projectpc.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.services.AuthService;
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

        // Show floating action button (to create new post)
        showFloatingActionButton();

        try {
            // Get auth service
            AuthService authService = Service.get(AuthService.class);
            AuthService.SessionData sessionData = authService.getSessionData();

            // Update username and email on sidebar
            View navigationRootView = mNavigationView.getHeaderView(0);
            TextView titleTextView = (TextView)navigationRootView.findViewById(R.id.nav_header_title);
            TextView emailTextView = (TextView)navigationRootView.findViewById(R.id.nav_header_email);
            titleTextView.setText(String.format("%s (%s %s)", sessionData.userName,
                    sessionData.firstName, sessionData.lastName));
            emailTextView.setText(sessionData.email);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Set content view
        setContentView(R.layout.activity_search);

        // TODO: Fetch data from server depending on the category (mCategory)
    }

    @Override
    public void onClickFloatingActionButton(View view) {
        // TODO: Go to create post activity (if in category, pre-fill edit text in create post activity)
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
        if (id == R.id.nav_inbox) {
            // Show inbox activity
            Intent intent = new Intent(this, InboxActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            // TODO: Show settings activity
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
