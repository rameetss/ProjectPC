package ca.projectpc.projectpc.ui;

import android.os.Bundle;
import android.view.View;
import android.view.MenuItem;
import android.widget.Toast;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.services.SystemService;

public class HomeActivity extends NavigationActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Show floating action button
        showFloatingActionButton();
    }

    @Override
    public void onClickFloatingActionButton(View view) {
        try {
            SystemService service = Service.get(SystemService.class);
            service.getVersion(new IServiceCallback<SystemService.VersionResult>() {
                @Override
                public void onEnd(ServiceResult<SystemService.VersionResult> result) {
                    if (!result.hasError()) {
                        Toast.makeText(getBaseContext(),
                                String.format("Version: %s", result.getData().version),
                                Toast.LENGTH_LONG).show();
                    } else {
                        result.getException().printStackTrace();
                        Toast.makeText(getBaseContext(),
                                "Unable to get system version!",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Unable to get system version!", Toast.LENGTH_LONG).show();
        }
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
