package ca.projectpc.projectpc.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ca.projectpc.projectpc.R;

public class MessageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }
}
