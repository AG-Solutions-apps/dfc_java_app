package com.dfc.agsolutions.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class PreviousMonthTripFragment extends Fragment {

    public static PreviousMonthTripFragment newInstance() {
        return new PreviousMonthTripFragment();
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
    LottieAnimationView noData;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View inflatedView = inflater.inflate(R.layout.fragment_previous_month_trip,
                container,
                false);

        activity = requireActivity();

        sp = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        ed = sp.edit();

        dialog = new ProgressDialog(requireActivity());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        swipeRefreshLayout = inflatedView.findViewById(R.id.swipeRefreshLayout);

        noData = inflatedView.findViewById(R.id.lav_no_data);
        rv = inflatedView.findViewById(R.id.rv);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            noData.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
            Expenses_List();
        });
        Expenses_List();

        return inflatedView;
    }

    private OkHttpClient.Builder createHttpClient() {
        return new OkHttpClient.Builder();
    }

    public void Expenses_List() {

        dialog.show();

        OkHttpClient.Builder httpClient = createHttpClient();

//        if (token != null) {
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer " + sp.getString("token", ""))
                    .method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
//        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.commn_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        Api loginService = retrofit.create(Api.class);
        Call<PreviousHistoryDataModel> call = loginService.getVehicleHistory( "2",
                activity.getIntent().getStringExtra("v_name"));

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<PreviousHistoryDataModel> call,
                                   @NonNull Response<PreviousHistoryDataModel> response) {

                Log.e("response..", "" + response);

                if (response.body() != null &&
                        response.body().getCode().equalsIgnoreCase("200")) {

                    noData.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                    ArrayList<PreviousHistoryDataModel> arr = response.body().getData();
                    HomeTodayListAdapter adapter = new HomeTodayListAdapter(arr);
                    rv.setAdapter(adapter);

                    if (arr.isEmpty()) {
                        noData.setVisibility(View.VISIBLE);
                        rv.setVisibility(View.GONE);
                    }

//                    Log.e("response..", "branches:-  " + branches.size());

                }
                dialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onFailure(@NonNull Call<PreviousHistoryDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("PreviousHistoryDataModel", "" + t);
                dialog.dismiss();
                noData.setVisibility(View.VISIBLE);
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
        public HomeTodayListAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_v_trip_list,
                    parent,
                    false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final HomeTodayListAdapter.Holder
                                             holder,
                                     @SuppressLint("RecyclerView") final int position) {

            String status = "Status: " + data.get(position).getTrip_status();
            holder.status.setText(status);

            String carName = " " + data.get(position).getTrip_vehicle();
            holder.carName.setText(carName);

            String location = " " + data.get(position).getTrip_agency();
            holder.location.setText(location);

//            holder.date.setText("Date : " + data.get(position).getTrip_date());

            String driver = " " + data.get(position).getTrip_driver();
            holder.driver.setText(driver);

            String distance = " " + data.get(position).getTrip_km() + " Km";
            holder.distance.setText(distance);

            String date1 = data.get(position).getTripDate();
            try {
                SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = inputDateFormat.parse(date1);

                SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String dateFormate = null;
                if (date != null) {
                    dateFormate = outputDateFormat.format(date);
                }

                dateFormate = "Date : " + dateFormate;
                holder.date.setText(dateFormate);

            } catch (ParseException e) {
                String date = "Date : " + date1;
                holder.date.setText(date);
                Log.e("error", "" + e);
            }

/*//            System.out.println("Last Trip Date: " + lastTripDateStr);
//            System.out.println("New Date (" + daysBeforeLastTrip + " days before last trip): " + formattedNewDate);*/

        }

        static class Holder extends RecyclerView.ViewHolder {

            TextView status, location, date, driver, distance, carName;
//            LinearLayout click;

            public Holder(@NonNull View itemView) {
                super(itemView);
                status = itemView.findViewById(R.id.status);
                location = itemView.findViewById(R.id.tv_location);
                date = itemView.findViewById(R.id.date);
                driver = itemView.findViewById(R.id.ll_drivers);
                distance = itemView.findViewById(R.id.distance);
                carName = itemView.findViewById(R.id.tv_car_name);
            }

        }

    }

}