package com.dfc.agsolutions.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.dfc.agsolutions.R;
import com.dfc.agsolutions.model.FetchAllVehicleDataModel;
import com.dfc.agsolutions.model.VehicleDetailsModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AllVehicleListActivity extends AppCompatActivity {

    ImageView iv_back;

    RecyclerView rv;
    SharedPreferences sp;
    SharedPreferences.Editor ed;

    ProgressDialog dialog;

    SwipeRefreshLayout swipeRefreshLayout;
    LottieAnimationView nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_vehicle_list);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        nodata = findViewById(R.id.lav_no_data);
        rv = findViewById(R.id.rv);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                nodata.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);
                expensesList();
            }
        });

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        iv_back = findViewById(R.id.iv_back);

        iv_back.setOnClickListener(v -> finish());
        expensesList();
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

        Call<FetchAllVehicleDataModel> call = loginService.getAllVehicleList(sp.getString("userBranch", ""));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<FetchAllVehicleDataModel> call,
                                   @NonNull Response<FetchAllVehicleDataModel> response) {
                Log.e("response..", " " + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    nodata.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                    HomeTodayListAdapter adapter = new HomeTodayListAdapter(response.body().getData());
                    rv.setAdapter(adapter);

                } else {
                    nodata.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
                    Toast.makeText(AllVehicleListActivity.this,
                            "Network Error!!",
                            Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);


            }

            @Override
            public void onFailure(@NonNull Call<FetchAllVehicleDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("FetchAllVehicleDataModel: ", "" + t);
                dialog.dismiss();
                nodata.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    public class HomeTodayListAdapter extends RecyclerView.Adapter<HomeTodayListAdapter.Holder> {

        ArrayList<FetchAllVehicleDataModel> data;

        public HomeTodayListAdapter(ArrayList<FetchAllVehicleDataModel> data) {
            this.data = data;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @NonNull
        @Override
        public HomeTodayListAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_vehicle,
                    parent,
                    false);
            return new HomeTodayListAdapter.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final HomeTodayListAdapter.Holder holder,
                                     @SuppressLint("RecyclerView") final int position) {

            try {
                String carName = " " + data.get(position).getReg_no();
                holder.tv_car_name.setText(carName);

                holder.ll_details.setOnClickListener(v -> Car_details(data.get(position).getReg_no()));

                holder.ll_trips.setOnClickListener(v -> {
                    Intent intent = new Intent(holder.itemView.getContext(), VehicleTripHistoryActivity.class);
                    intent.putExtra("v_name",data.get(position).getReg_no());
                    startActivity(intent);

                });

            } catch (Exception e) {
                Log.e("error", "" + e);
            }


        }

        class Holder extends RecyclerView.ViewHolder {

            TextView tv_car_name;
            LinearLayout ll_details, ll_trips;

            public Holder(@NonNull View itemView) {
                super(itemView);
                tv_car_name = itemView.findViewById(R.id.tv_car_name);
                ll_details = itemView.findViewById(R.id.details);
                ll_trips = itemView.findViewById(R.id.trips);
            }
        }

    }

    public void Car_details(String reg_no) {

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
        Api loginservice = retrofit.create(Api.class);
        Call<VehicleDetailsModel> call = loginservice.getVehicleDetails(reg_no);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<VehicleDetailsModel> call,
                                   @NonNull Response<VehicleDetailsModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode() == 200) {

                    countryDialogLogout(response.body().getData());

                } else {
                    Toast.makeText(AllVehicleListActivity.this,
                            "Network Error!!",
                            Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<VehicleDetailsModel> call,
                                  @NonNull Throwable t) {
                Log.e("VehicleDetailsModel: ", "" + t);
                dialog.dismiss();
            }
        });

    }

    private void countryDialogLogout(VehicleDetailsModel.DataModel data) {

        if (data == null) {
            Toast.makeText(this, "Vehicle data is not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AllVehicleListActivity.this,
                R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
        bottomSheetDialog.show();

        TextView tv_firm_name = bottomSheetDialog.findViewById(R.id.Firm_Name);

        String firmName = ": " + data.getVehicleBranch();
        if (tv_firm_name != null) {
            tv_firm_name.setText(firmName);
        }

        TextView tv_mobile = bottomSheetDialog.findViewById(R.id.Mobile);
        String mobile = ": " + data.getVehicleCompany();
        if (tv_mobile != null) {
            tv_mobile.setText(mobile);
        }

        TextView tv_email = bottomSheetDialog.findViewById(R.id.Email);
        String email = ": " + data.getVehicleType();
        if (tv_email != null) {
            tv_email.setText(email);
        }

        TextView tv_dob = bottomSheetDialog.findViewById(R.id.DOB);
        String dob = ": " + data.getManufacturingYear();
        if (tv_dob != null) {
            tv_dob.setText(dob);
        }

        TextView tv_anniversary = bottomSheetDialog.findViewById(R.id.Anniversary);
        String anniversary = ": " + data.getInsuranceDue();
        if (tv_anniversary != null) {
            tv_anniversary.setText(anniversary);
        }

        TextView tv_category = bottomSheetDialog.findViewById(R.id.Category);
        String category = ": " + data.getPermitDue();
        if (tv_category != null) {
            tv_category.setText(category);
        }

        TextView tv_product = bottomSheetDialog.findViewById(R.id.Product);
        String product = ": " + data.getFcDue();
        if (tv_product != null) {
            tv_product.setText(product);
        }

        TextView tv_address = bottomSheetDialog.findViewById(R.id.Address);
        String address = ": " + data.getVehicleMileage();
        if (tv_address != null) {
            tv_address.setText(address);
        }

        TextView tv_car_name = bottomSheetDialog.findViewById(R.id.tv_car_name);
        String carName = ": " + data.getRegNo();
        if (tv_car_name != null) {
            tv_car_name.setText(carName);
        }

    }

}