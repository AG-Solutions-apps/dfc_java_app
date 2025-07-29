package com.dfc.agsolutions.activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dfc.agsolutions.R;
import com.google.android.material.tabs.TabLayout;

public class HistoryFragment extends Fragment {

    TabLayout tabLayout;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_history_fragment, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);

        setData();

        return view;
    }

    private void setData() {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        TextView tab_label = null;
        int[] navLabels = {R.string.currant, R.string.previous
        };
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            RelativeLayout tab2 = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab_layout1, (ViewGroup) null);
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
                    return CurrantHistory.newInstance(position + 1);
                case 1:
                    return PreviousHistory.newInstance(position + 1);
                default:
                    return null;
            }
//            return null;
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