package ca.projectpc.projectpc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import ca.projectpc.projectpc.R;

public abstract class NavigationActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClickFloatingActionButton(View view) {
        Toast.makeText(this, "Home FAB!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNavigationItemSelected(int id) {
        Class c = null;
        if (id == R.id.nav_home) {
            c = HomeActivity.class;
        } else if (id == R.id.nav_inbox) {
            // ...
        }

        if (c != null) {
            Intent intent = new Intent(this, c);
            startActivity(intent);
        }
    }
}
