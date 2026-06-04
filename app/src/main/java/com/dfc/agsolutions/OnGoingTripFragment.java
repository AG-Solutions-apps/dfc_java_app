package com.dfc.agsolutions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Toast;

import com.dfc.agsolutions.activity.Api;
import com.dfc.agsolutions.activity.UpdateTripActivity;
import com.dfc.agsolutions.model.OngoingTruckTypeModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

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

public class OnGoingTripFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;

    public OnGoingTripFragment() {
        // Required empty public constructor
    }

    RecyclerView rv_shop;
    LinearLayout lav_no_data;
    ProgressDialog dialog;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    Activity activity;

    public static OnGoingTripFragment newInstance(String param1,
                                                  String param2) {
        OnGoingTripFragment fragment = new OnGoingTripFragment();
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

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_on_going_trip,
                container,
                false);

        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_on_going_trip, container, false);

        swipeRefreshLayout = inflatedView.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(() -> get_trip(mParam1, activity));

        activity = requireActivity();
//        return inflater.inflate(R.layout.fragment_ideal, container, false);

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

    public void get_trip(String selectedBranch,
                         Activity activity) {
/*//        ProgressDialog  dialog = new ProgressDialog(activity);
//        dialog.setMessage("Loading...");
//        dialog.setCancelable(false);*/
        dialog.show();
/*//        sp = PreferenceManager.getDefaultSharedPreferences(activity);
//        ed = sp.edit();*/
        try {

            OkHttpClient.Builder httpClient = createHttpClient();

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
                    .baseUrl(activity.getString(R.string.common_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

            Api loginService = retrofit.create(Api.class);

            Call<OngoingTruckTypeModel> call = loginService.getVehicleListOngoing(selectedBranch, "1");
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<OngoingTruckTypeModel> call,
                                       @NonNull Response<OngoingTruckTypeModel> response) {
                    Log.e("response..", "" + response);

                    assert response.body() != null;
                    if (response.body().getCode().equalsIgnoreCase("200")) {

                        if (response.body().getData().isEmpty()) {
                            lav_no_data.setVisibility(View.VISIBLE);
                            rv_shop.setVisibility(View.GONE);
                        } else {
                            lav_no_data.setVisibility(View.GONE);
                            rv_shop.setVisibility(View.VISIBLE);
                            HomeTodayListAdapter home_today_list_adapter = new HomeTodayListAdapter(requireActivity(),
                                    response.body().getData());
                            rv_shop.setAdapter(home_today_list_adapter);
                            rv_shop.setItemAnimator(new DefaultItemAnimator());
                            rv_shop.setHasFixedSize(true);
                        }

                    } else {
                        Toast.makeText(requireActivity(),
                                "Network Error!!",
                                Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(@NonNull Call<OngoingTruckTypeModel> call,
                                      @NonNull Throwable t) {
                    Log.e("OngoingTruckTypeModel", "" + t);
                    dialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

        } catch (Exception e) {
            Log.e("TAG", "trip: " + e);
        }
    }

    public class HomeTodayListAdapter extends
            RecyclerView.Adapter<HomeTodayListAdapter.Holder> {

        private final Activity context;

        ArrayList<OngoingTruckTypeModel> arrayListTopic;

        public HomeTodayListAdapter(Activity context,
                                    ArrayList<OngoingTruckTypeModel> arrayListTopic) {
            this.context = context;
            this.arrayListTopic = arrayListTopic;
        }

        @Override
        public int getItemCount() {
            return arrayListTopic.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @NonNull
        @Override
        public HomeTodayListAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent,
                                                              int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_on_going_trip,
                    parent,
                    false);
            return new HomeTodayListAdapter.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final HomeTodayListAdapter.Holder holder,
                                     @SuppressLint("RecyclerView") final int position) {

            OngoingTruckTypeModel ongoingTruckTypeModel = arrayListTopic.get(position);

            holder.llEdit.setOnClickListener(v -> {
                Intent intent=new Intent(context, UpdateTripActivity.class);
                intent.putExtra("pass_data", ongoingTruckTypeModel);
                context.startActivity(intent);
            });
            String status = "Status: " + ongoingTruckTypeModel.getTrip_status();
            holder.tv_status.setText(status);
            String carName =  ": " + ongoingTruckTypeModel.getTrip_vehicle();
            holder.carName.setText(carName);
            String location = ": " + ongoingTruckTypeModel.getTrip_agency();
            holder.location.setText(location);

            String driver = ": " + ongoingTruckTypeModel.getTrip_driver();
            holder.driver.setText(driver);
            String distance = ": " + ongoingTruckTypeModel.getTrip_km() + " Km";
            holder.distance.setText(distance);
            String bhsd = " : " + ongoingTruckTypeModel.getTrip_bhsd() + " Ltr";
            holder.tvBHSDValue.setText(bhsd);
            String fhsd = " : " + ongoingTruckTypeModel.getTrip_hsd() + " Ltr";
            holder.tvFHSDValue.setText(fhsd);
            String shsd = " : " + ongoingTruckTypeModel.getTrip_hsd_supplied() + " Ltr";
            holder.tvSHSDValue.setText(shsd);
            String adv = " : " + ongoingTruckTypeModel.getTrip_advance();
            holder.tvADVValue.setText(adv);

            try {
                String date = ongoingTruckTypeModel.getTrip_date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                // Parse the given date string
                Date givenDate = sdf.parse(date);

                SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                if (givenDate != null) {
                    String dateFormat = outputDateFormat.format(givenDate);
                    dateFormat = "Date :" + dateFormat;
                    holder.date.setText(dateFormat);
                }
            } catch (ParseException e) {
                String dateFormat = "Date : " + ongoingTruckTypeModel.getTrip_date();
                holder.date.setText(dateFormat);
//                throw new RuntimeException(e);
            }

/*//            System.out.println("Last Trip Date: " + lastTripDateStr);
//            System.out.println("New Date (" + daysBeforeLastTrip + " days before last trip): " + formattedNewDate);*/

            holder.tv_status.setOnClickListener(v -> {

/*//                    Dialog dialog = new Dialog(requireActivity());
//                    dialog.setContentView(R.layout.logout_bottom_sheet_dialog_going_trip);
//                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    dialog.setCancelable(true);*/

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity(), R.style.SheetDialog);
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_going_trip);
                bottomSheetDialog.show();

                TextView pending = bottomSheetDialog.findViewById(R.id.pending);
                TextView cancel = bottomSheetDialog.findViewById(R.id.cancel);
                TextView finish = bottomSheetDialog.findViewById(R.id.finish);

                if (pending != null) {
                    pending.setOnClickListener(v2 -> {
    /*//                            ongoingTruckTypeModel.setTrip_status(pending.getText().toString());
    //                            Log.d("arrayListTopic", "pending: "+ongoingTruckTypeModel.getTrip_status());*/
                        onGoingCount("Pending", ongoingTruckTypeModel.getId());
                        bottomSheetDialog.dismiss();
                    });
                }

                if (cancel != null) {
                    cancel.setOnClickListener(v3 -> {
    /*//                            ongoingTruckTypeModel.setTrip_status(cancel.getText().toString());
    //                            Log.d("arrayListTopic", "cancel: "+ongoingTruckTypeModel.getTrip_status());*/
                        onGoingCount(cancel.getText().toString(), ongoingTruckTypeModel.getId());
                        bottomSheetDialog.dismiss();
                    });
                }

                if (finish != null) {
                    finish.setOnClickListener(v1 -> {
    /*//                            ongoingTruckTypeModel.setTrip_status(finish.getText().toString());
    //                            Log.d("arrayListTopic", "finish: "+ongoingTruckTypeModel.getTrip_status());*/
                        onGoingCount(finish.getText().toString(), ongoingTruckTypeModel.getId());
                        bottomSheetDialog.dismiss();
                    });
                }

                bottomSheetDialog.show();

            });

        }

        class Holder extends RecyclerView.ViewHolder {
            LinearLayout llEdit;
            TextView tv_status, location, date, driver, distance, carName;
            TextView tvBHSDValue, tvFHSDValue, tvSHSDValue, tvADVValue;
//            LinearLayout click;

            public Holder(@NonNull View itemView) {
                super(itemView);

                llEdit = itemView.findViewById(R.id.llEdit);
                tvBHSDValue = itemView.findViewById(R.id.tvBHSDValue);
                tvFHSDValue = itemView.findViewById(R.id.tvFHSDValue);
                tvSHSDValue = itemView.findViewById(R.id.tvSHSDValue);
                tvADVValue = itemView.findViewById(R.id.tvADVValue);

                tv_status = itemView.findViewById(R.id.tv_status);
                location = itemView.findViewById(R.id.tv_location);
                date = itemView.findViewById(R.id.tv_date);
                driver = itemView.findViewById(R.id.ll_drivers);
                distance = itemView.findViewById(R.id.tv_distance);
                carName = itemView.findViewById(R.id.tv_car_name);

            }
        }

    }

    private OkHttpClient.Builder createHttpClient() {
        return new OkHttpClient.Builder();
    }

    public void onGoingCount(String tripStatus, String tripId) {
/*//        ProgressDialog  dialog = new ProgressDialog(activity);
//        dialog.setMessage("Loading...");
//        dialog.setCancelable(false);*/
        dialog.show();

/*//        sp = PreferenceManager.getDefaultSharedPreferences(activity);
//        ed = sp.edit();*/
        try {

            OkHttpClient.Builder httpClient = createHttpClient();

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
                    .baseUrl(requireActivity().getString(R.string.common_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

            Api apiService = retrofit.create(Api.class);

            Call<OngoingTruckTypeModel> call =
                    apiService.updateManagerOnGoingVehicleStatus(
                            tripId,
                            sp.getString("userBranch", ""),
                            tripStatus);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<OngoingTruckTypeModel> call,
                                       @NonNull Response<OngoingTruckTypeModel> response) {
                    Log.e("response..", "" + response);

                    assert response.body() != null;
                    if (response.body().getCode().equalsIgnoreCase("200")) {
                        get_trip(mParam1, activity);
                    } else {
                        Toast.makeText(requireActivity(), "Network Error!!", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
//                swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(@NonNull Call<OngoingTruckTypeModel> call,
                                      @NonNull Throwable t) {
                    Log.e("Failure", "" + t);
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            Log.e("TAG", "trip: " + e);
        }
    }

}
////example of bottom sheet dialog
/*//                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity(), R.style.SheetDialog);
//                bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_going_trip);
//                bottomSheetDialog.show();
//              TextView pending = bottomSheetDialog.findViewById(R.id.pending);
//                TextView cancel           = bottomSheetDialog.findViewById(R.id.cancel);
//                TextView finish           = bottomSheetDialog.findViewById(R.id.finish);                  

//                if (pending != null) {
//                    pending.setOnClickListener(v2 -> {
//                            ongoingTruckTypeModel.setTrip_status(pending.getText().toString());





//                            Log.d("arrayListTopic", "pending: "+ongoingTruckTypeModel.getTrip_status());
//                        onGoingCount("Pending", ongoingTruckTypeModel.getId());
//                        bottomSheetDialog.dismiss();
//                    });
//                }

//                if (cancel != null) {
//                    cancel.setOnClickListener(v3 -> {
//                            ongoingTruckTypeModel.setTrip_status(cancel.getText().toString());
//                            Log.d("arrayListTopic", "cancel: "+ongoingTruckTypeModel.get

Trip_status());
//                        onGoingCount(cancel.getText().toString(), ongoingTruckTypeModel.getId());
//

    //* */