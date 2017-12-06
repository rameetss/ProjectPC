package ca.projectpc.projectpc.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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

    RecyclerView mRecyclerView;

    // TODO: This is temporary, we're going to fetch this data every time refresh is called
    List<Item> mItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
            TextView titleTextView = (TextView) navigationRootView.findViewById(R.id.nav_header_title);
            TextView emailTextView = (TextView) navigationRootView.findViewById(R.id.nav_header_email);
            titleTextView.setText(String.format("%s (%s %s)", sessionData.userName,
                    sessionData.firstName, sessionData.lastName));
            emailTextView.setText(sessionData.email);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Set content view
        setContentView(R.layout.activity_search);

        // TODO: Fetch data from server depending on the category (mCategory)
        // TODO: The following code is temporary
        // Find recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.search_ads_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //initializing the itemlist
        mItemList = new ArrayList<>();

        //adding some items to our list
        mItemList.add(new Item(
                1,
                "Asus Gaming Tower",
                "Feb 3",
                2.7,
                250,
                R.drawable.computer_case));

        mItemList.add(new Item(
                1,
                "500W Power Supply",
                "May 8",
                5,
                65,
                R.drawable.power_supply));

        mItemList.add(new Item(
                1,
                "8GB Kingston RAM",
                "Nov 9",
                4,
                200,
                R.drawable.ram));

        mItemList.add(new Item(
                1,
                "Asus Gaming Tower",
                "Feb 3",
                2.7,
                250,
                R.drawable.computer_case));

        mItemList.add(new Item(
                1,
                "500W Power Supply",
                "May 8",
                5,
                65,
                R.drawable.power_supply));

        mItemList.add(new Item(
                1,
                "8GB Kingston RAM",
                "Nov 9",
                4,
                200,
                R.drawable.ram));

        // Create recycler view adapter
        ItemAdapter adapter = new ItemAdapter(this, mItemList);

        //setting adapter to recycler view
        mRecyclerView.setAdapter(adapter);
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

    public class Item {
        private int mId;
        private String mTitle;
        private String mDate;
        private double mDistance;
        private double mPrice;
        private int mImage; // TODO: Download (not gonna be a int when downloaded)

        public Item(int id, String title, String date, double distance, double price, int image) {
            mId = id;
            mTitle = title;
            mDate = date;
            mDistance = distance;
            mPrice = price;
            mImage = image;
        }

        public int getId() {
            return mId;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getDate() {
            return mDate;
        }

        public double getDistance() {
            return mDistance;
        }

        public double getPrice() {
            return mPrice;
        }

        public int getImage() {
            return mImage;
        }
    }

    // TODO: Clean this up
    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ProductViewHolder> {
        private Context mContext;
        private List<Item> mItemList;

        //getting the context and product list with constructor
        public ItemAdapter(Context context, List<Item> itemList) {
            mContext = context;
            mItemList = itemList;
        }

        @Override
        public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //inflating and returning our view holder
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_ad, null);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ProductViewHolder holder, int position) {
            //getting the product of the specified position
            Item product = mItemList.get(position);

            //binding the data with the viewholder views
            holder.mTitleTextView.setText(product.getTitle());
            holder.mDateTextView.setText(product.getDate());
            holder.mDistanceTextView.setText(Double.toString(product.getDistance()));
            holder.mPriceTextView.setText(Double.toString(product.getPrice()));

            holder.mThumbnailImageView.setImageDrawable(mContext.getResources().getDrawable(product.getImage()));
        }

        @Override
        public int getItemCount() {
            return mItemList.size();
        }

        // TODO: Add currency view
        class ProductViewHolder extends RecyclerView.ViewHolder {

            TextView mTitleTextView, mDateTextView, mDistanceTextView, mPriceTextView;
            ImageView mThumbnailImageView;

            public ProductViewHolder(View itemView) {
                super(itemView);

                mTitleTextView = itemView.findViewById(R.id.item_ad_title);
                mDateTextView = itemView.findViewById(R.id.item_ad_date);
                mDistanceTextView = itemView.findViewById(R.id.item_ad_distance);
                mPriceTextView = itemView.findViewById(R.id.item_ad_price);
                mThumbnailImageView = itemView.findViewById(R.id.item_ad_thumbnail);
            }
        }
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
