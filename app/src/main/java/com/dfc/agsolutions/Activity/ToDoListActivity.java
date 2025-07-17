package com.dfc.agsolutions.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dfc.agsolutions.R;
import com.google.android.material.tabs.TabLayout;

public class ToDoListActivity extends AppCompatActivity {
    TabLayout tabLayout;
ImageView back;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        back = (ImageView) findViewById(R.id.back);

        back.setOnClickListener(v -> {

            onBackPressed();

        });
        setData();

    }
    private void setData() {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        TextView tab_label = null;
        int[] navLabels = {R.string.pending_task, R.string.complete_task
        };
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            RelativeLayout tab2 = (RelativeLayout) LayoutInflater.from(ToDoListActivity.this).inflate(R.layout.custom_tablayout, (ViewGroup) null);
            tab_label = (TextView) tab2.findViewById(R.id.text1);
            tab_label.setText(navLabels[i]);
            tabLayout.getTabAt(i).setCustomView(tab2);
        }

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
                    return PendingTask.newInstance(position + 1);
                case 1:
                    return CompleteTask.newInstance(position + 1);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {

            }
            return null;
        }

        public int getItemPosition(Object item) {
            return POSITION_NONE;
        }
    }

}