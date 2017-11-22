package ca.projectpc.projectpc.ui;

import android.os.Bundle;
import android.view.View;
import android.view.MenuItem;
import android.widget.Toast;

import ca.projectpc.projectpc.R;

public class HomeActivity extends NavigationActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    public void onClickFloatingActionButton(View view) {
        Toast.makeText(this, "Home FAB!", Toast.LENGTH_LONG).show();
    }

    @Override
    public int getNavigationId() {
        // This is for the current navigation menu or 0 if none
        return R.id.nav_home;
    }

    @Override
    public int getMenuId() {
        // This is for the menu to use (R.menu.BLAH) or 0 if none
        return R.menu.menu_home;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // This is for menu options
        return true;
    }
}
