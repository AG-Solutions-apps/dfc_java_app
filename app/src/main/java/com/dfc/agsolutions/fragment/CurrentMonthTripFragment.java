package com.dfc.agsolutions.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.dfc.agsolutions.activity.Api;
import com.dfc.agsolutions.model.PreviousHistoryDataModel;
import com.dfc.agsolutions.R;

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

public class CurrentMonthTripFragment extends Fragment {

    public static CurrentMonthTripFragment newInstance() {
        return new CurrentMonthTripFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    RecyclerView rv;
    SharedPreferences sp;
    SharedPreferences.Editor ed;

    Activity activity;
    ProgressDialog dialog;

    SwipeRefreshLayout swipeRefreshLayout;
    LottieAnimationView lav_no_data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_current_month_trip, container, false);

        activity = requireActivity();

        sp = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        ed = sp.edit();

        dialog = new ProgressDialog(requireActivity());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        swipeRefreshLayout = inflatedView.findViewById(R.id.swipeRefreshLayout);

        lav_no_data = inflatedView.findViewById(R.id.lav_no_data);
        rv = inflatedView.findViewById(R.id.rv);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            lav_no_data.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
            expensesList();
        });
        expensesList();

        return inflatedView;
    }

    public void expensesList() {

        dialog.show();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer " + sp.getString("token", ""))
                    .method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.common_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        Api loginService = retrofit.create(Api.class);

        Call<PreviousHistoryDataModel> call = loginService.getVehicleHistory("1",
                activity.getIntent().getStringExtra("v_name"));

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PreviousHistoryDataModel> call,
                                   @NonNull Response<PreviousHistoryDataModel> response) {

                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {
                    ArrayList<PreviousHistoryDataModel> arr = response.body().getData();

                    if (arr != null && !arr.isEmpty()) {
                        lav_no_data.setVisibility(View.GONE);
                        rv.setVisibility(View.VISIBLE);
                        HomeTodayListAdapter adapter = new HomeTodayListAdapter(arr);
                        rv.setAdapter(adapter);
                    } else {
                        lav_no_data.setVisibility(View.VISIBLE);
                        rv.setVisibility(View.GONE);
                    }

                } else {
                    lav_no_data.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
                    Toast.makeText(activity, "Network Error!!", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onFailure(@NonNull Call<PreviousHistoryDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("PreviousHistoryDataModel: ", "" + t);
                dialog.dismiss();
                lav_no_data.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    public static class HomeTodayListAdapter extends
            RecyclerView.Adapter<HomeTodayListAdapter.Holder> {

        ArrayList<PreviousHistoryDataModel> data;

        public HomeTodayListAdapter(ArrayList<PreviousHistoryDataModel> data) {
            this.data = data;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @NonNull
        @Override
        public HomeTodayListAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent,
                                                              int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_v_trip_list,
                    parent,
                    false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final HomeTodayListAdapter.Holder holder,
                                     @SuppressLint("RecyclerView") final int position) {

            String status = "Status: " + data.get(position).getTrip_status();
            holder.tv_status.setText(status);
            String carName = " " + data.get(position).getTrip_vehicle();
            holder.tv_car_name.setText(carName);
            String location = " " + data.get(position).getTrip_agency();
            holder.tv_location.setText(location);
            String driver = " " + data.get(position).getTrip_driver();
            holder.tv_driver.setText(driver);
            String distance = " " + data.get(position).getTrip_km() + " Km";
            holder.tv_distance.setText(distance);

            String date1 = data.get(position).getTripDate();
            try {
                SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd",
                        Locale.getDefault());
                Date date = inputDateFormat.parse(date1);

                SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy",
                        Locale.getDefault());

                String dateFormated;
                if (date != null) {
                    dateFormated = outputDateFormat.format(date);

                    String dateStr = "Date : " + dateFormated;
                    holder.tv_date.setText(dateStr);
                }

            } catch (ParseException e) {
                String dateStr = "Date : " + date1;
                holder.tv_date.setText(dateStr);
            }

        }

        static class Holder extends
                RecyclerView.ViewHolder {

            TextView tv_status, tv_location, tv_date, tv_driver, tv_distance, tv_car_name;

            public Holder(@NonNull View itemView) {
                super(itemView);

                tv_status = itemView.findViewById(R.id.tv_status);
                tv_location = itemView.findViewById(R.id.tv_location);
                tv_date = itemView.findViewById(R.id.tv_date);
                tv_driver = itemView.findViewById(R.id.ll_drivers);
                tv_distance = itemView.findViewById(R.id.tv_distance);
                tv_car_name = itemView.findViewById(R.id.tv_car_name);

            }
        }

    }

}