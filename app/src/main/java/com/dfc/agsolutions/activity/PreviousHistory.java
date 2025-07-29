package com.dfc.agsolutions.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class PreviousHistory extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    ProgressDialog dialog;
    SharedPreferences sp;

    SharedPreferences.Editor ed;
    public static Fragment newInstance(int i) {
        PreviousHistory fragment = new PreviousHistory();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, i);
        fragment.setArguments(args);
        return fragment;
    }

    RecyclerView rv;
    LottieAnimationView nodata;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_previous_history, container, false);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ed = sp.edit();
        nodata = view.findViewById(R.id.lav_no_data);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rv = view.findViewById(R.id.rv);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                previousHistory();
            }
        });
        previousHistory();

        return view;

    }

    public void previousHistory() {

//        dialog.show();
//        fullname.clear();
//        mobile.clear();
//        dl_expiry.clear();
//        user_status.clear();
//        user_image.clear();
//        milageaaray.clear();

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
        Call<PreviousHistoryDataModel> call = loginservice.get_previousHistory(String.valueOf(2));
        call.enqueue(new Callback<PreviousHistoryDataModel>() {
            @Override
            public void onResponse(Call<PreviousHistoryDataModel> call, Response<PreviousHistoryDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {
//
                    ArrayList<PreviousHistoryDataModel> branches = response.body().getData();
//                    for (DriverListDataModel branch : branches) {
//
//                        fullname.add(branch.getFull_name());
//                        mobile.add(branch.getMobile());
//                        dl_expiry.add(branch.getDl_expiry());
//                        user_status.add(branch.getUser_status());
//                        user_image.add(branch.getUser_image());
//
//                    }

                    if(response.body().getData().size() == 0)
                    {
                        nodata.setVisibility(View.VISIBLE);
                        rv.setVisibility(View.GONE);
                    }else {
                        nodata.setVisibility(View.GONE);
                        rv.setVisibility(View.VISIBLE);
                        Home_Today_list_Adapter adapter = new Home_Today_list_Adapter(getActivity(), response.body().getData());
                        rv.setAdapter(adapter);
                    }
//
////response.body().getData().get(0).getReg_no();
////                    for (ServiceFatchVhicalDataModel branch : branches) {
////                        vhicalarray.add(branch.getReg_no());
//////                        vhicaldraiverarray.add(branch.getVehicle_driver());
//////                        milageaaray.add(branch.getVehicle_mileage());
////                    }
//                    for (ServiceFatchVhicalDataModel branch : branches) {
//                        vhicalarray.add(branch.getReg_no());
//                    }
////
//                    ArrayAdapter<String> adapter = new ArrayAdapter<>(DriverListActivity.this, R.layout.simple_spinner_item, vhicalarray);
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinner.setAdapter(adapter);

//
//                    ArrayAdapter<String> adapterdriver = new ArrayAdapter<>(CreatTrip.this, R.layout.simple_spinner_item, vhicaldraiverarray);
//                    adapterdriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinnerdriver.setAdapter(adapterdriver);


//                    setupSpinner(branchNames);
                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(getActivity(),  "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<PreviousHistoryDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    public class Home_Today_list_Adapter extends RecyclerView.Adapter<Home_Today_list_Adapter.Holder> {
        private FragmentActivity activity;

        ArrayList<PreviousHistoryDataModel> data;

//        public Home_Today_list_Adapter(Activity context, ArrayList<OngoingTruckTypeModel> data) {
//
//        }

        public Home_Today_list_Adapter(FragmentActivity activity, ArrayList<PreviousHistoryDataModel> data) {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_on_going_trip, parent, false);
            return new Home_Today_list_Adapter.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final Home_Today_list_Adapter.Holder holder, @SuppressLint("RecyclerView") final int position) {


            holder.status.setText("Status:- " + data.get(position).getTrip_status());
            holder.carname.setText("" + data.get(position).getTrip_vehicle());
            holder.loacation.setText("Destination : " + data.get(position).getTrip_agency());
//            holder.date.setText("Date : " + data.get(position).getTrip_date());
            holder.driver.setText("Driver : " + data.get(position).getTrip_driver());
            holder.distance.setText("Distance : " + data.get(position).getTrip_km() + " Km");

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

            TextView status,loacation,date,driver,distance,carname;
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