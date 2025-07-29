package com.dfc.agsolutions.activity;

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

public class VhicletripHistoryActivity extends AppCompatActivity {

    TextView title;
    private ViewPager mViewPager;

    ImageView icback;
    public static TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_trip_history);

//        get_vhicleHistory
        title = findViewById(R.id.title);
        title.setText("" + getIntent().getStringExtra("v_nmae"));


        icback = findViewById(R.id.iv_back);
        icback.setOnClickListener(v -> {
            finish();
        });

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
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
                    return CurrentMonthTripFragment.newInstance();
                case 1:
                    return PreviousMonthTripFragment.newInstance();
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
                    return "Current Month Trip";
                case 1:
                    return "Previous Month Trip";

            }
            return null;
        }

        public int getItemPosition(Object item) {
            return POSITION_NONE;
        }
    }


}