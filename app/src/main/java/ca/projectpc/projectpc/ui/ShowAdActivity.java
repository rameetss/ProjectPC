package ca.projectpc.projectpc.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ca.projectpc.projectpc.R;

/**
 * Created by Jesse on 2017-12-14.
 */

public class ShowAdActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ad);
    }

    public void onSendMessage(View view) {

    }
}
