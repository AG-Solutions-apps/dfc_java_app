package com.dfc.agsolutions.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
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

public class CurrantHistory extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    ProgressDialog dialog;
    LottieAnimationView lav_no_data;

    SharedPreferences sp;
    SharedPreferences.Editor ed;

    public static Fragment newInstance(int i) {
        CurrantHistory fragment = new CurrantHistory();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, i);
        fragment.setArguments(args);
        return fragment;
    }

    RecyclerView rv;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_previous_history,
                container,
                false);

        sp = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        ed = sp.edit();
        lav_no_data = view.findViewById(R.id.lav_no_data);

        dialog = new ProgressDialog(requireActivity());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        rv = view.findViewById(R.id.rv);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(this::previousHistory);

        previousHistory();

        return view;
    }

    public void previousHistory() {

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

        Call<PreviousHistoryDataModel> call = loginService.getPreviousHistory(String.valueOf(1));

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PreviousHistoryDataModel> call,
                                   @NonNull Response<PreviousHistoryDataModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    ArrayList<PreviousHistoryDataModel> branches = response.body().getData();

                    Log.e("Response---------", "onResponse: "+response.body().getData().size());

                    if(response.body().getData().isEmpty()) {
                        lav_no_data.setVisibility(View.VISIBLE);
                        rv.setVisibility(View.GONE);
                    } else {
                        lav_no_data.setVisibility(View.GONE);
                        rv.setVisibility(View.VISIBLE);
                        HomeTodayListAdapter adapter = new HomeTodayListAdapter(response.body().getData());
                        rv.setAdapter(adapter);
                    }

                    Log.e("response..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(requireActivity(),  "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<PreviousHistoryDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("PreviousHistoryDataModel: ", "" + t);
                dialog.dismiss();
            }
        });

        swipeRefreshLayout.setRefreshing(false);
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
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_on_going_trip,
                    parent,
                    false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final Holder holder,
                                     @SuppressLint("RecyclerView") final int position) {

            String status = "Status:- " + data.get(position).getTrip_status();
            holder.tv_status.setText(status);

            String carName = " " + data.get(position).getTrip_vehicle();
            holder.tv_car_name.setText(carName);

            String location = "Destination : " + data.get(position).getTrip_agency();
            holder.tv_location.setText(location);

            String driver = "Driver : " + data.get(position).getTrip_driver();
            holder.tv_driver.setText(driver);

            String distance = "Distance : " + data.get(position).getTrip_km() + " Km";
            holder.tv_distance.setText(distance);
            String date1 = data.get(position).getTripDate();

            try {
                SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = inputDateFormat.parse(date1);

                SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String dateFormated;
                if (date != null) {
                    dateFormated = outputDateFormat.format(date);
                    String date_ = "Date : " + dateFormated;
                    holder.tv_date.setText(date_);
                }

            } catch (ParseException e) {
                String date_ = "Date : " + date1;
                holder.tv_date.setText(date_);
            }

        }

        static class Holder extends RecyclerView.ViewHolder {

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