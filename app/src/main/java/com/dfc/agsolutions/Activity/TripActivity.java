package com.dfc.agsolutions.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.dfc.agsolutions.IdealFragment;
import com.dfc.agsolutions.Model.Branch;
import com.dfc.agsolutions.Model.ResponseArrayModel;
import com.dfc.agsolutions.Model.TruckTypeModel;
import com.dfc.agsolutions.OnGoingTripFragment;
import com.dfc.agsolutions.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TripActivity extends AppCompatActivity {

    RecyclerView rly_shope;
    LinearLayout nodata;
    ProgressDialog dialog;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    private Spinner spinnerBranches;
    List<String> branchNames = new ArrayList<>();
    private ViewPager mViewPager;

    public static TabLayout tabLayout;
    public static TabLayout.Tab tab;
    SectionsPagerAdapter mSectionsPagerAdapter;
    public String selectedBranchname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();

        spinnerBranches = findViewById(R.id.spinnerBranches);
        dialog = new ProgressDialog(TripActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        branchNames.clear();

        rly_shope = findViewById(R.id.rly_shope);
        nodata = findViewById(R.id.nodata);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);


        findViewById(R.id.icback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        get_branch();


        spinnerBranches.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedBranch = (String) parentView.getItemAtPosition(position);
                selectedBranchname = selectedBranch;
                Log.e("branchname", "selectedBranchname:-   " + selectedBranchname);
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                mViewPager.setAdapter(mSectionsPagerAdapter);
                tabLayout.setupWithViewPager(mViewPager);
//                Fragment currentFragment = mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem());
//                if (currentFragment instanceof OnGoingTripFragment) {
//                    ((OnGoingTripFragment) currentFragment).updateContent(selectedBranch);
//                }
//                if (currentFragment instanceof IdealFragment) {
//                    ((IdealFragment) currentFragment).updateContent(selectedBranch, TripActivity.this);
//                }

//                get_trip(selectedBranch);

                // Do something with the selected branch name
//                Toast.makeText(MainActivity.this, "Selected Branch: " + selectedBranch, Toast.LENGTH_SHORT).show();
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

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {
                case 0:
                    return OnGoingTripFragment.newInstance(selectedBranchname, "0");
                case 1:
                    return IdealFragment.newInstance(selectedBranchname, "1");
            /* case 5:

               /* case 5:
                    return Fragment_Ivisited.newInstance(position+1);*/
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.ongoing);
                case 1:
                    return getString(R.string.ideal);

            }
            return null;
        }

        public int getItemPosition(Object item) {
            return POSITION_NONE;
        }
    }

    public void get_branch() {
        dialog.show();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

//        if (token != null) {
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer " + sp.getString("token", ""))
                    .method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
//        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.commn_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        Api loginservice = retrofit.create(Api.class);
        Call<ResponseArrayModel> call = loginservice.get_branch();
        call.enqueue(new Callback<ResponseArrayModel>() {
            @Override
            public void onResponse(Call<ResponseArrayModel> call, Response<ResponseArrayModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {

                    List<Branch> branches = response.body().getData();

                    for (Branch branch : branches) {
                        branchNames.add(branch.getBranchName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(TripActivity.this, R.layout.simple_spinner_item_branch, branchNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerBranches.setAdapter(adapter);
//                    setupSpinner(branchNames);
                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(TripActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<ResponseArrayModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
    }


    public void get_trip(String selectedBranch) {
        dialog.show();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

//        if (token != null) {
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer " + sp.getString("token", ""))
                    .method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
//        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.commn_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        Api loginservice = retrofit.create(Api.class);
        Call<TruckTypeModel> call = loginservice.get_vhiclelist(selectedBranch, "2");
        call.enqueue(new Callback<TruckTypeModel>() {
            @Override
            public void onResponse(Call<TruckTypeModel> call, Response<TruckTypeModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {

                    if (response.body().getData().size() == 0) {
                        nodata.setVisibility(View.VISIBLE);
                        rly_shope.setVisibility(View.GONE);
                    } else {
                        nodata.setVisibility(View.GONE);
                        rly_shope.setVisibility(View.VISIBLE);
                        Home_Today_list_Adapter home_today_list_adapter = new Home_Today_list_Adapter(TripActivity.this, response.body().getData());
                        rly_shope.setAdapter(home_today_list_adapter);
                        rly_shope.setItemAnimator(new DefaultItemAnimator());
                        rly_shope.setHasFixedSize(true);
                    }

                } else {
                    Toast.makeText(TripActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<TruckTypeModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
    }


    public class Home_Today_list_Adapter extends RecyclerView.Adapter<Home_Today_list_Adapter.Holder> {
        private Activity context;

        ArrayList<TruckTypeModel> arrayListTopic;

        public Home_Today_list_Adapter(Activity context, ArrayList<TruckTypeModel> arrayListTopic) {
            this.context = context;
            this.arrayListTopic = arrayListTopic;
        }

        @Override
        public int getItemCount() {
            return arrayListTopic.size();
        }

        @NonNull
        @Override
        public Home_Today_list_Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itam_ideal_vhical, parent, false);
            return new Home_Today_list_Adapter.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final Home_Today_list_Adapter.Holder holder, @SuppressLint("RecyclerView") final int position) {


            holder.status.setText("Status:- " + arrayListTopic.get(position).getVehicle_status());
            holder.v_number.setText("" + arrayListTopic.get(position).getReg_no());
            String lastTripDateStr = arrayListTopic.get(position).getTrip_date();


            if (arrayListTopic.get(position).getTrip_date().equals("")) {
                holder.tripdate.setText("-" + " / " + "0" + "days");

            } else {

                long daysDifference;
                try {
                    String givenDateString = arrayListTopic.get(position).getTrip_date();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    // Parse the given date string
                    Date givenDate = sdf.parse(givenDateString);

                    // Get the current date
                    Date currentDate = new Date();

                    // Calculate the difference in milliseconds
                    long timeDifference = currentDate.getTime() - givenDate.getTime();

                    // Convert milliseconds to days
                    daysDifference = timeDifference / (24 * 60 * 60 * 1000);
                    holder.tripdate.setText(arrayListTopic.get(position).getTrip_date() + " / " + daysDifference + " days");



                    System.out.println("Days difference between " + givenDateString + " and today: " + daysDifference + " days");


                } catch (ParseException e) {
//                e.printStackTrace();
                    holder.tripdate.setText(arrayListTopic.get(position).getTrip_date() + " / " + "0" + "days");

                }
//            System.out.println("Last Trip Date: " + lastTripDateStr);
//            System.out.println("New Date (" + daysBeforeLastTrip + " days before last trip): " + formattedNewDate);

            }


        }

        class Holder extends RecyclerView.ViewHolder {

            TextView status, v_number, tripdate;
//            LinearLayout click;

            public Holder(@NonNull View itemView) {
                super(itemView);


                status = itemView.findViewById(R.id.status);
                v_number = itemView.findViewById(R.id.v_number);
                tripdate = itemView.findViewById(R.id.tripdate);

            }
        }


    }


}