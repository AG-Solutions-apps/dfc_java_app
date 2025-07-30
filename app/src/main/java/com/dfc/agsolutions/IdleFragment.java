package com.dfc.agsolutions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dfc.agsolutions.activity.Api;
import com.dfc.agsolutions.model.TruckTypeModel;

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

public class IdleFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;

    public IdleFragment() {
        // Required empty public constructor
    }

    public static IdleFragment newInstance(String param1, String param2) {
        IdleFragment fragment = new IdleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    RecyclerView rv_shop;
    LinearLayout lav_no_data;
    ProgressDialog dialog;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    Activity activity;

    //    View inflater;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View inflatedView = inflater.inflate(R.layout.fragment_idle, container, false);
        activity = requireActivity();
//        return inflater.inflate(R.layout.fragment_ideal, container, false);

        swipeRefreshLayout = inflatedView.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(() -> get_trip(mParam1, activity));

        sp = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        ed = sp.edit();

//        spinnerBranches = findViewById(R.id.spinnerBranches);
        dialog = new ProgressDialog(requireActivity());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
//        branchNames.clear();

        rv_shop = inflatedView.findViewById(R.id.rv_shop);
        lav_no_data = inflatedView.findViewById(R.id.lav_no_data);

        Log.e("branch name", "mParam1:-   " + mParam1);
        get_trip(mParam1, activity);
        return inflatedView;
    }

    public void get_trip(String selectedBranch, Activity activity) {
/*//        ProgressDialog  dialog = new ProgressDialog(activity);
//        dialog.setMessage("Loading...");
//        dialog.setCancelable(false);*/
        dialog.show();

        /*sp = PreferenceManager.getDefaultSharedPreferences(activity);
        ed = sp.edit();*/

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

//        if (token != null) {
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer " +
                            sp.getString("token", ""))
                    .method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
//        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(activity.getString(R.string.commn_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        Api loginService = retrofit.create(Api.class);

        Call<TruckTypeModel> call = loginService.getVehicleList(selectedBranch, "2");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TruckTypeModel> call,
                                   @NonNull Response<TruckTypeModel> response) {
                Log.e("response..", "" + response);

                if (response.body() != null && response.body().getCode().equalsIgnoreCase("200")) {

                    if (response.body().getData().isEmpty()) {
                        lav_no_data.setVisibility(View.VISIBLE);
                        rv_shop.setVisibility(View.GONE);
                    } else {
                        lav_no_data.setVisibility(View.GONE);
                        rv_shop.setVisibility(View.VISIBLE);
                        HomeTodayListAdapter home_today_list_adapter = new HomeTodayListAdapter(response.body().getData());
                        rv_shop.setAdapter(home_today_list_adapter);
                        rv_shop.setItemAnimator(new DefaultItemAnimator());
                        rv_shop.setHasFixedSize(true);
                    }

                }
                dialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<TruckTypeModel> call,
                                  @NonNull Throwable t) {
                Log.e("TruckTypeModel", "" + t);
                dialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
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
        public HomeTodayListAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent,
                                                              int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itam_ideal_vhical,
                    parent,
                    false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final HomeTodayListAdapter.Holder holder,
                                     @SuppressLint("RecyclerView") final int position) {

            String status = "Status:- " + arrayListTopic.get(position).getVehicle_status();
            holder.status.setText(status);
            String v_number = arrayListTopic.get(position).getReg_no();
            holder.v_number.setText(v_number);
/*//            String lastTripDateStr = arrayListTopic.get(position).getTrip_date();
//Log.e("branch name","lastTripDateStr:- " +lastTripDateStr );*/

            try {
                if (arrayListTopic.get(position).getTrip_date().isEmpty()) {
                    String daysText = "-" + " / " + "0" + "days";
                    holder.tripDate.setText(daysText);
                } else {

                    long daysDifference;

                    try {
                        String givenDateString = arrayListTopic.get(position).getTrip_date();

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
                                Locale.getDefault());

                        // Parse the given date string
                        Date givenDate = sdf.parse(givenDateString);

                        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy",
                                Locale.getDefault());
                        String dateFormate = null;
                        if (givenDate != null) {
                            dateFormate = outputDateFormat.format(givenDate);
                        }

                        // Get the current date
                        Date currentDate = new Date();

                        // Calculate the difference in milliseconds
                        long timeDifference = 0;
                        if (givenDate != null) {
                            timeDifference = currentDate.getTime() - givenDate.getTime();
                        }

                        // Convert milliseconds to days
                        daysDifference = timeDifference / (24 * 60 * 60 * 1000);

                        String dateFormat = dateFormate + " / " + daysDifference + " days";
                        holder.tripDate.setText(dateFormat);

                        System.out.println("Days difference between " + givenDateString +
                                " and today: " + daysDifference + " days");
                    } catch (ParseException e) {
                        Log.e("IdleFragment", "$e.getMessage()");
                        //                e.printStackTrace();
                        String dateFormat = arrayListTopic.get(position).getTrip_date() + " / " + "0" + "days";
                        holder.tripDate.setText(dateFormat);
                    }
                    /*//            System.out.println("Last Trip Date: " + lastTripDateStr);
                    //            System.out.println("New Date (" + daysBeforeLastTrip + " days before last trip): " + formattedNewDate);*/
                }

            } catch (Exception e) {
                Log.e("IdleFragment", "$e.getMessage()");
//                throw new RuntimeException(e);
            }

        }

        static class Holder extends RecyclerView.ViewHolder {

            TextView status, v_number, tripDate;
//            LinearLayout click;

            public Holder(@NonNull View itemView) {
                super(itemView);

                status = itemView.findViewById(R.id.status);
                v_number = itemView.findViewById(R.id.v_number);
                tripDate = itemView.findViewById(R.id.tv_trip_date);

            }

        }

    }

}