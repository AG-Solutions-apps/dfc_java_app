package com.dfc.agsolutions.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.dfc.agsolutions.R;
import com.dfc.agsolutions.activity.Api;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PreviousMonthTripFragment extends
        Fragment {

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
    LottieAnimationView lav_no_data;

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

        lav_no_data = inflatedView.findViewById(R.id.lav_no_data);
        rv = inflatedView.findViewById(R.id.rv);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            lav_no_data.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
            retrieveLastMonthVehicleHistory();
        });
        retrieveLastMonthVehicleHistory();

        return inflatedView;
    }

    private OkHttpClient.Builder createHttpClient() {
        return new OkHttpClient.Builder();
    }

    public void retrieveLastMonthVehicleHistory() {

        dialog.show();

        OkHttpClient.Builder httpClient = createHttpClient();

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

        Api retrofitService = retrofit.create(Api.class);
        Call<JsonObject> call = retrofitService.getVehicleHistory( "2",
                activity.getIntent().getStringExtra("v_name"));

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call,
                                   @NonNull Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    JsonObject json = response.body();
                    if (json != null) {
                        int code = json.get("code").getAsInt();
                        if (code == 200) {
                            JsonArray prevMonthVehicleHistoryArr = json.getAsJsonArray("data");
                            if (prevMonthVehicleHistoryArr != null &&
                                    !prevMonthVehicleHistoryArr.isEmpty()) {
                                lav_no_data.setVisibility(View.GONE);
                                rv.setVisibility(View.VISIBLE);

                                // If your adapter expects a list, wrap it:
                                HomeTodayListAdapter adapter = new HomeTodayListAdapter(prevMonthVehicleHistoryArr);
                                rv.setAdapter(adapter);

                            } else {
                                lav_no_data.setVisibility(View.VISIBLE);
                                rv.setVisibility(View.GONE);
                            }
                        } else {
                            lav_no_data.setVisibility(View.VISIBLE);
                            rv.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(activity, "Response failure", Toast.LENGTH_SHORT).show();
                        lav_no_data.setVisibility(View.VISIBLE);
                        rv.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(activity, "Response failure", Toast.LENGTH_SHORT).show();
                    lav_no_data.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
                }

                dialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call,
                                  @NonNull Throwable t) {
                Log.e("PreviousHistoryDataModel", "" + t);
                dialog.dismiss();
                lav_no_data.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    public static class HomeTodayListAdapter extends
            RecyclerView.Adapter<HomeTodayListAdapter.Holder> {

        JsonArray prevMonthVehicleHistoryArr;

        public HomeTodayListAdapter(JsonArray prevMonthVehicleHistoryArr) {
            this.prevMonthVehicleHistoryArr = prevMonthVehicleHistoryArr;
        }

        @Override
        public int getItemCount() {
            return prevMonthVehicleHistoryArr.size();
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

            JsonObject prevMonthVehicleHistory = prevMonthVehicleHistoryArr.get(position).getAsJsonObject();
            String status = "Status: " + prevMonthVehicleHistory.get("trip_status").getAsString();
            holder.tv_status.setText(status);

            String carName = " " + prevMonthVehicleHistory.get("trip_vehicle").getAsString();
            holder.tv_carName.setText(carName);

            String location = " " + prevMonthVehicleHistory.get("trip_agency").getAsString();
            holder.tv_location.setText(location);

            String driver = " " + prevMonthVehicleHistory.get("trip_driver").getAsString();
            holder.tv_driver.setText(driver);

            String distance = " " + prevMonthVehicleHistory.get("trip_km").getAsInt() + " Km";
            holder.tv_distance.setText(distance);

            String date1 = prevMonthVehicleHistory.get("trip_date").getAsString();
            try {
                SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = inputDateFormat.parse(date1);

                SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String dateFormate = null;
                if (date != null) {
                    dateFormate = outputDateFormat.format(date);
                }

                dateFormate = "Date : " + dateFormate;
                holder.tv_date.setText(dateFormate);

            } catch (ParseException e) {
                String date = "Date : " + date1;
                holder.tv_date.setText(date);
                Log.e("error", "" + e);
            }

        }

        static class Holder extends
                RecyclerView.ViewHolder {

            TextView tv_status, tv_location, tv_date, tv_driver, tv_distance, tv_carName;

            public Holder(@NonNull View itemView) {
                super(itemView);
                tv_status = itemView.findViewById(R.id.tv_status);
                tv_location = itemView.findViewById(R.id.tv_location);
                tv_date = itemView.findViewById(R.id.tv_date);
                tv_driver = itemView.findViewById(R.id.tv_drivers);
                tv_distance = itemView.findViewById(R.id.tv_distance);
                tv_carName = itemView.findViewById(R.id.tv_car_name);
            }

        }

    }

}