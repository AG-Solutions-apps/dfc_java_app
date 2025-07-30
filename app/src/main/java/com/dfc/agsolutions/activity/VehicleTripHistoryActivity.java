package com.dfc.agsolutions.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.dfc.agsolutions.fragment.CurrentMonthTripFragment;
import com.dfc.agsolutions.fragment.PreviousMonthTripFragment;
import com.dfc.agsolutions.R;
import com.google.android.material.tabs.TabLayout;

public class VehicleTripHistoryActivity extends
        AppCompatActivity {

    TextView title;

    ImageView icBack;
    public static TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_trip_history);

        title = findViewById(R.id.title);
        String vehicleName = " " + getIntent().getStringExtra("v_name");
        title.setText(vehicleName);

        icBack = findViewById(R.id.iv_back);
        icBack.setOnClickListener(v -> finish());

        tabLayout = findViewById(R.id.tabLayout);
        ViewPager mViewPager = findViewById(R.id.viewpager);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position,
                                       float positionOffset,
                                       int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) { }

            @Override
            public void onPageScrollStateChanged(int state) { }

        });

    }

    public static class SectionsPagerAdapter extends
            FragmentStatePagerAdapter {

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
                case 2:
                    return PreviousMonthTripFragment.newInstance();
                case 0:
                default:
                    return CurrentMonthTripFragment.newInstance();
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
                    return "Current Month Trip";
                case 1:
                    return "Previous Month Trip";

            }
            return null;
        }

        public int getItemPosition(@NonNull Object item) {
            return POSITION_NONE;
        }

    }

}