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

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ca.projectpc.projectpc.R;
import ca.projectpc.projectpc.api.IServiceCallback;
import ca.projectpc.projectpc.api.Service;
import ca.projectpc.projectpc.api.ServiceResult;
import ca.projectpc.projectpc.api.service.MessageService;

// NOTE: Deprecated in favour of emailing. Unfortunately we ran out of time to complete this
// however the backend code/API for it is complete.
public class InboxActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    /**
     * Save any dynamic instance state in activity into the given Bundle,
     * to be later received in onCreate(Bundle) if the activity needs to be re-created.
     *
     * @param savedInstanceState Last saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        // Enable back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Setup swipe refresh layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.inbox_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorScheme(
                R.color.colorAccent,
                R.color.colorPrimary
        );

        // Setup recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.inbox_threads_recycler_view);

        // Refresh messages
        refresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // TODO: Cancel task
    }

    /**
     * Upon refreshing the layout (by swiping up), initialize the MessageService to fetch
     * all messages from the database.
     */
    private void refresh() {
        try {
            mSwipeRefreshLayout.setRefreshing(true);

            MessageService service = Service.get(MessageService.class);
            service.getAllMessages(new IServiceCallback<MessageService.GetMessagesResult>() {
                @Override
                public void onEnd(ServiceResult<MessageService.GetMessagesResult> result) {
                    mSwipeRefreshLayout.setRefreshing(false);

                    if (!result.hasError()) {
                        // TODO: Add messages to recycler view depending on them existing
                        // TODO: previously or not
                    } else {
                        result.getException().printStackTrace();

                        Toast.makeText(getBaseContext(), "Unable to refresh messages",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();

            Toast.makeText(this, "Unable to refresh messages",
                    Toast.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    /**
     * Custom adapter class to handle RecyclerView message cards. Extends ViewHolder in order
     * to place the messages in the layout properly.
     */
    private class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
        class ViewHolder extends RecyclerView.ViewHolder {
            private CardView mCardView;
            private TextView mMessageText;
            private TextView mMessageSender;
            private TextView mMessageTimeReceived;

            /**
             * Initialize a class instance, setting the cardView and message information
             * initialized above appropriately.
             *
             * @param itemView The view containing the cardView and message fields.
             */
            ViewHolder(View itemView) {
                super(itemView);

                mCardView = (CardView) itemView.findViewById(R.id.item_inbox_card_view);
                mMessageText = (TextView) itemView.findViewById(R.id.item_inbox_message);
                mMessageSender = (TextView) itemView.findViewById(R.id.item_inbox_sender);
                mMessageTimeReceived =
                        (TextView) itemView.findViewById(R.id.item_inbox_time_received);
            }
        }

        private List<MessageService.Message> mMessages;

        public MessageAdapter(List<MessageService.Message> messages) {
            mMessages = messages;
        }

        /**
         * Called when a ViewHolder is created to inflate the inbox message layout contained within.
         *
         * @param parent   ViewGroup parent containing the ViewHolder.
         * @param viewType Int required by implemented method, not used.
         * @return The created and inflated ViewHolder.
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_inbox_thread, parent, false);
            return new ViewHolder(view);
        }

        /**
         * Called to bind a RecyclerView card to a ViewHolder and initialize a click listener
         * for each card (each message).
         *
         * @param holder ViewHolder to bind the card to.
         * @param position Position in the ViewHolder to bind the card
         */
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final MessageService.Message message = mMessages.get(position);

            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: Start MessageActivity for message thread
                }
            });

            // Setup text
            //holder.mMessageSender.setText(message.)
        }

        /**
         * Called to fetch the number of cards in the ViewHolder
         * @return number of cards
         */
        @Override
        public int getItemCount() {
            return mMessages.size();
        }
    }
}
