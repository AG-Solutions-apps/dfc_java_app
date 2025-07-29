package com.dfc.agsolutions.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dfc.agsolutions.R;

public class Comming_SoonActivity extends Fragment {


    TextView toptext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_comming_soon, container, false);

        toptext = view.findViewById(R.id.toptext);
//        toptext.setText(getIntent().getStringExtra("header"));


        view.findViewById(R.id.icback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
        return view;

    }
}