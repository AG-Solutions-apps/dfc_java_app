package com.dfc.agsolutions.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.dfc.agsolutions.IdleFragment;
import com.dfc.agsolutions.model.Branch;
import com.dfc.agsolutions.model.ResponseArrayModel;
import com.dfc.agsolutions.OnGoingTripFragment;
import com.dfc.agsolutions.R;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TripActivity extends
        AppCompatActivity {

    RecyclerView rl_shop;
    LinearLayout ll_no_data;
    ProgressDialog dialog;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    private Spinner spinnerBranches;
    List<String> branchNames = new ArrayList<>();
    private ViewPager mViewPager;

    public static TabLayout tabLayout;

    SectionsPagerAdapter mSectionsPagerAdapter;
    public String selectedBranchName;

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

        rl_shop = findViewById(R.id.rv_shop);
        ll_no_data = findViewById(R.id.lav_no_data);
        tabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewpager);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        getBranch();

        spinnerBranches.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView,
                                       int position,
                                       long id) {

                selectedBranchName = (String) parentView.getItemAtPosition(position);
                Log.e("branchName", "selectedBranchName:-   " + selectedBranchName);
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                mViewPager.setAdapter(mSectionsPagerAdapter);
                tabLayout.setupWithViewPager(mViewPager);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position,
                                       float positionOffset,
                                       int positionOffsetPixels) {

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

        public int getItemPosition(@NonNull Object item) {
            return POSITION_NONE;
        }
    }

    private OkHttpClient.Builder createHttpClient() {
        return new OkHttpClient.Builder();
    }

    public void getBranch() {
        dialog.show();

        OkHttpClient.Builder httpClient = createHttpClient();

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
                .baseUrl(getString(R.string.common_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        Api loginService = retrofit.create(Api.class);

        Call<ResponseArrayModel> call = loginService.getBranch();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseArrayModel> call,
                                   @NonNull Response<ResponseArrayModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    List<Branch> branches = response.body().getData();

                    for (Branch branch : branches) {
                        branchNames.add(branch.getBranchName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(TripActivity.this,
                            R.layout.simple_spinner_item_branch, branchNames);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerBranches.setAdapter(adapter);

                    Log.e("response..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(TripActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<ResponseArrayModel> call,
                                  @NonNull Throwable t) {
                Log.e("ResponseArrayModel", " " + t);
                dialog.dismiss();
            }
        });
    }

}