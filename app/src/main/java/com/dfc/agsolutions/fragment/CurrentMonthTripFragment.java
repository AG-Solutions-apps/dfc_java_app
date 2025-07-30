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


    // TODO: Rename and change types and number of parameters
    public static CurrentMonthTripFragment newInstance() {
        CurrentMonthTripFragment fragment = new CurrentMonthTripFragment();
        return fragment;
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
    LottieAnimationView nodata;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_current_month_trip, container, false);

        activity = getActivity();

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ed = sp.edit();

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        swipeRefreshLayout = inflatedView.findViewById(R.id.swipeRefreshLayout);

        nodata = inflatedView.findViewById(R.id.lav_no_data);
        rv = inflatedView.findViewById(R.id.rv);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                nodata.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);
                Expenses_List();
            }
        });
        Expenses_List();


        return inflatedView;
    }


    public void Expenses_List() {

        dialog.show();


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

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
        Api loginservice = retrofit.create(Api.class);
        Call<PreviousHistoryDataModel> call = loginservice.get_vhicleHistory("1", activity.getIntent().getStringExtra("v_name"));
        call.enqueue(new Callback<PreviousHistoryDataModel>() {
            @Override
            public void onResponse(Call<PreviousHistoryDataModel> call, Response<PreviousHistoryDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {

                    nodata.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                    ArrayList<PreviousHistoryDataModel> arr = response.body().getData();
                    Home_Today_list_Adapter adapter = new Home_Today_list_Adapter(activity, arr);
                    rv.setAdapter(adapter);

                    if (arr.size() == 0) {
                        nodata.setVisibility(View.VISIBLE);
                        rv.setVisibility(View.GONE);
                    }

//                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    nodata.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
                    Toast.makeText(activity, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);


            }

            @Override
            public void onFailure(Call<PreviousHistoryDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
                nodata.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

            }
        });

    }

    public class Home_Today_list_Adapter extends RecyclerView.Adapter<Home_Today_list_Adapter.Holder> {
        private Activity activity;

        ArrayList<PreviousHistoryDataModel> data;

//        public Home_Today_list_Adapter(Activity context, ArrayList<OngoingTruckTypeModel> data) {
//
//        }

        public Home_Today_list_Adapter(Activity activity, ArrayList<PreviousHistoryDataModel> data) {
            this.activity = activity;
            this.data = data;
        }


        @Override
        public int getItemCount() {
            return data.size();
        }

        @NonNull
        @Override
        public Home_Today_list_Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_v_trip_list, parent, false);
            return new Home_Today_list_Adapter.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final Home_Today_list_Adapter.Holder holder, @SuppressLint("RecyclerView") final int position) {


            holder.status.setText("Status: " + data.get(position).getTrip_status());
            holder.carname.setText("" + data.get(position).getTrip_vehicle());
            holder.loacation.setText(" " + data.get(position).getTrip_agency());
//            holder.date.setText("Date : " + data.get(position).getTrip_date());
            holder.driver.setText("" + data.get(position).getTrip_driver());
            holder.distance.setText("" + data.get(position).getTrip_km() + " Km");

            String date1 = data.get(position).getTrip_date();
            try {
                SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = inputDateFormat.parse(date1);

                SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String datefiormate = outputDateFormat.format(date);

                holder.date.setText("Date : " + datefiormate);

            } catch (ParseException e) {
                holder.date.setText("Date : " + date1);
            }

//            System.out.println("Last Trip Date: " + lastTripDateStr);
//            System.out.println("New Date (" + daysBeforeLastTrip + " days before last trip): " + formattedNewDate);


        }

        class Holder extends RecyclerView.ViewHolder {

            TextView status, loacation, date, driver, distance, carname;
//            LinearLayout click;

            public Holder(@NonNull View itemView) {
                super(itemView);


                status = itemView.findViewById(R.id.status);
                loacation = itemView.findViewById(R.id.tv_location);
                date = itemView.findViewById(R.id.date);
                driver = itemView.findViewById(R.id.ll_drivers);
                distance = itemView.findViewById(R.id.distance);
                carname = itemView.findViewById(R.id.tv_car_name);

            }
        }


    }


}