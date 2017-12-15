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

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ca.projectpc.projectpc.R;

// NOTE: Deprecated in favour of emailing. Unfortunately we ran out of time to complete this
// however the backend code/API for it is complete.
public class MessageActivity extends AppCompatActivity {

    /**
     * Save any dynamic instance state in activity into the given Bundle,
     * to be later received in onCreate(Bundle) if the activity needs to be re-created.
     *
     * @param savedInstanceState Last saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Enable back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // ...
    }
}
