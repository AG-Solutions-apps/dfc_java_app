package com.dfc.agsolutions.activity;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dfc.agsolutions.R;

public class ComingSoonFragment extends
        Fragment {

    TextView tv_top_text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comming_soon, container, false);

        tv_top_text = view.findViewById(R.id.tv_top_text);

        view.findViewById(R.id.iv_back).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return view;

    }

}