package com.dfc.agsolutions.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.dfc.agsolutions.Fragment.CurrentMonthTripFragment;
import com.dfc.agsolutions.Fragment.PreviousMonthTripFragment;
import com.dfc.agsolutions.IdealFragment;
import com.dfc.agsolutions.Model.FatchAllVhicalDataModel;
import com.dfc.agsolutions.Model.PreviousHistoryDataModel;
import com.dfc.agsolutions.OnGoingTripFragment;
import com.dfc.agsolutions.R;
import com.google.android.material.tabs.TabLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VhicletripHistoryActivity extends AppCompatActivity {

    TextView title;
    private ViewPager mViewPager;

    ImageView icback;
    public static TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vhicletrip_history);

//        get_vhicleHistory
        title = findViewById(R.id.title);
        title.setText("" + getIntent().getStringExtra("v_nmae"));


        icback = findViewById(R.id.icback);
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