package com.dfc.agsolutions.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.dfc.agsolutions.IdleFragment;
import com.dfc.agsolutions.model.OngoingTruckTypeModel;
import com.dfc.agsolutions.model.TruckTypeModel;
import com.dfc.agsolutions.OnGoingTripFragment;
import com.dfc.agsolutions.R;
import com.google.android.material.tabs.TabLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminTripActivity extends
        AppCompatActivity {

    RecyclerView rly_shop;
    LinearLayout ll_no_data;
    ProgressDialog dialog;
    SharedPreferences sp;
    SharedPreferences.Editor ed;

    List<String> branchNames = new ArrayList<>();
    private ViewPager mViewPager;

    public static TabLayout tabLayout;

    SectionsPagerAdapter mSectionsPagerAdapter;
    public String selectedBranchName;

    TextView header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_admin);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();

        Spinner spinnerBranches = findViewById(R.id.spinnerBranches);
        dialog = new ProgressDialog(AdminTripActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        branchNames.clear();

        rly_shop = findViewById(R.id.rv_shop);
        ll_no_data = findViewById(R.id.lav_no_data);
        header = findViewById(R.id.header);

        String vehicleListBranchName = "Vehicle list - " + sp.getString("userBranch", "");
        header.setText(vehicleListBranchName);
        tabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewpager);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        onGoingTripCount();
        idleVehicleCount();

        dialog.show();

        new Handler().postDelayed(this::setData, 1000);

        spinnerBranches.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedBranchName = parentView.getItemAtPosition(position).toString();
                Log.e("branchName", "selectedBranchName:-   " + selectedBranchName);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void setData() {

        selectedBranchName = sp.getString("userBranch", "");
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        TextView tv_tab_label;
        TextView tv_total_todo;

        int[] navLabels = {R.string.ongoing, R.string.ideal};

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            RelativeLayout tab2 = (RelativeLayout) LayoutInflater.from(AdminTripActivity.this).inflate(R.layout.custom_tablayout,
                    new LinearLayout(this));
            tv_tab_label = tab2.findViewById(R.id.text1);
            tv_total_todo = tab2.findViewById(R.id.tv_total_todo);
            if (i == 0) {
                tv_tab_label.setText(navLabels[i]);
                tv_total_todo.setText(String.valueOf(onGoingVehicleCount));
            } else {
                tv_tab_label.setText(navLabels[i]);
                tv_total_todo.setText(String.valueOf(idleVehicleCount));
            }

            Objects.requireNonNull(tabLayout.getTabAt(i)).setCustomView(tab2);
            dialog.dismiss();
        }

    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {
                case 1:
                    return IdleFragment.newInstance(selectedBranchName, "1");

                case 0:
                default:
                    return OnGoingTripFragment.newInstance(selectedBranchName, "0");
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        public int getItemPosition(@NonNull Object item) {
            return POSITION_NONE;
        }
    }

    int onGoingVehicleCount;
    int idleVehicleCount;

    public void onGoingTripCount() {

        try {

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", "Bearer " + sp.getString("token", ""))
                        .method(original.method(), original.body());
                Request request = requestBuilder.build();
                return chain.proceed(request);
            });

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AdminTripActivity.this.getString(R.string.commn_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

            Api loginService = retrofit.create(Api.class);

            Call<OngoingTruckTypeModel> call = loginService.getVehicleListOngoing(sp.getString("userBranch", ""), "1");
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<OngoingTruckTypeModel> call,
                                       @NonNull Response<OngoingTruckTypeModel> response) {
                    Log.e("response..", "" + response);

                    assert response.body() != null;
                    if (response.body().getCode().equalsIgnoreCase("200")) {


                        ArrayList<OngoingTruckTypeModel> branches = response.body().getData();

                        Log.e("Response---------", "onResponse: " + response.body().getData().size());

                        onGoingVehicleCount = branches.size();

                    } else {
                        Toast.makeText(AdminTripActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<OngoingTruckTypeModel> call,
                                      @NonNull Throwable t) {
                    Log.e("OngoingTruckTypeModel: ", "" + t);
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            Log.e("TAG", "trip: " + e);
        }

    }

    public void idleVehicleCount() {
        dialog.show();
        try {

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", "Bearer " + sp.getString("token", ""))
                        .method(original.method(), original.body());
                Request request = requestBuilder.build();
                return chain.proceed(request);
            });

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AdminTripActivity.this.getString(R.string.commn_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

            Api loginService = retrofit.create(Api.class);

            Call<OngoingTruckTypeModel> call = loginService.getVehicleListOngoing(sp.getString("userBranch", ""), "2");
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<OngoingTruckTypeModel> call,
                                       @NonNull Response<OngoingTruckTypeModel> response) {
                    Log.e("response..", "" + response);

                    assert response.body() != null;
                    if (response.body().getCode().equalsIgnoreCase("200")) {

                        ArrayList<OngoingTruckTypeModel> branches = response.body().getData();

                        Log.e("Response---------", "onResponse: " + response.body().getData().size());

                        idleVehicleCount = branches.size();

                    } else {
                        Toast.makeText(AdminTripActivity.this,
                                "Network Error!!",
                                Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<OngoingTruckTypeModel> call,
                                      @NonNull Throwable t) {
                    Log.e("OngoingTruckTypeModel", "" + t);
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            Log.e("TAG", "trip: " + e);
        }

    }

    public static class HomeTodayListAdapter extends
            RecyclerView.Adapter<HomeTodayListAdapter.Holder> {

        ArrayList<TruckTypeModel> arrayListTopic;

        public HomeTodayListAdapter(ArrayList<TruckTypeModel> arrayListTopic) {
            this.arrayListTopic = arrayListTopic;
        }

        @Override
        public int getItemCount() {
            return arrayListTopic.size();
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itam_ideal_vhical,
                    parent,
                    false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final Holder holder,
                                     @SuppressLint("RecyclerView") final int position) {


            String status = "Status:- " + arrayListTopic.get(position).getVehicle_status();
            holder.tv_status.setText(status);
            String vehicleNumber = " " + arrayListTopic.get(position).getReg_no();
            holder.tv_v_number.setText(vehicleNumber);

            if (arrayListTopic.get(position).getTrip_date().isEmpty()) {

                String tripDate = "-" + " / " + "0" + "days";
                holder.tv_trip_date.setText(tripDate);

            } else {

                long daysDifference;
                try {
                    String givenDateString = arrayListTopic.get(position).getTrip_date();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    // Parse the given date string
                    Date givenDate = sdf.parse(givenDateString);

                    // Get the current date
                    Date currentDate = new Date();

                    // Calculate the difference in milliseconds
                    assert givenDate != null;
                    long timeDifference = currentDate.getTime() - givenDate.getTime();

                    // Convert milliseconds to days
                    daysDifference = timeDifference / (24 * 60 * 60 * 1000);

                    String tripDate = arrayListTopic.get(position).getTrip_date() + " / " + daysDifference + " days";
                    holder.tv_trip_date.setText(tripDate);

                    System.out.println("Days difference between " + givenDateString + " and today: " + daysDifference + " days");

                } catch (ParseException e) {
                    Log.e("TAG", "onBindViewHolder: " + e);
                    String tripDate = arrayListTopic.get(position).getTrip_date() + " / " + "0" + "days";
                    holder.tv_trip_date.setText(tripDate);

                }

            }


        }

        static class Holder extends RecyclerView.ViewHolder {

            TextView tv_status, tv_v_number, tv_trip_date;

            public Holder(@NonNull View itemView) {
                super(itemView);

                tv_status = itemView.findViewById(R.id.status);
                tv_v_number = itemView.findViewById(R.id.v_number);
                tv_trip_date = itemView.findViewById(R.id.tv_trip_date);

            }
        }

    }

}