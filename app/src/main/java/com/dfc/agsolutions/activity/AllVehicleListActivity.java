package com.dfc.agsolutions.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Debug;
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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
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

        Api retrofitService = retrofit.create(Api.class);

        Call<ResponseBody> call = retrofitService.getVehicleDetails(reg_no);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                   @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonString = response.body().string();
                        JSONObject json = new JSONObject(jsonString);

                        JSONObject vehicle = json.getJSONObject("data");
                        // show vehicle detail
                        showVehicleDetailsDialog(vehicle);
                    } else {
                        Toast.makeText(AllVehicleListActivity.this,
                                "Network Error!!",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("VehicleDetailsError", "Exception: " + e.getMessage(), e);
                    Toast.makeText(AllVehicleListActivity.this,
                            "Parsing error occurred.",
                            Toast.LENGTH_SHORT).show();
                } finally {
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call,
                                  @NonNull Throwable t) {
                Log.e("VehicleDetailsError", "Failure: " + t.getMessage(), t);
                Toast.makeText(AllVehicleListActivity.this,
                        "Request failed: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

    }

    private void showVehicleDetailsDialog(JSONObject vehicleData) {

        if (vehicleData == null) {
            Toast.makeText(this, "Vehicle data is not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AllVehicleListActivity.this,
                    R.style.SheetDialog);
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
            bottomSheetDialog.show();

            TextView tv_branch_name = bottomSheetDialog.findViewById(R.id.tv_branch_name);

            String vehicleBranch = ": " + vehicleData.getString("vehicle_branch");
            if (tv_branch_name != null) {
                tv_branch_name.setText(vehicleBranch);
            }

            TextView tv_vehicle_company = bottomSheetDialog.findViewById(R.id.tv_vehicle_company);
            String mobile = ": " + vehicleData.getString("vehicle_company");
            if (tv_vehicle_company != null) {
                tv_vehicle_company.setText(mobile);
            }

            TextView tv_vehicle_type = bottomSheetDialog.findViewById(R.id.tv_vehicle_type);
            String email = ": " + vehicleData.getString("vehicle_type");
            if (tv_vehicle_type != null) {
                tv_vehicle_type.setText(email);
            }

            TextView tv_mfg_year = bottomSheetDialog.findViewById(R.id.tv_mfg_year);
            String dob = ": " + vehicleData.getString("mfg_year");
            if (tv_mfg_year != null) {
                tv_mfg_year.setText(dob);
            }

            TextView tv_ins_due = bottomSheetDialog.findViewById(R.id.tv_ins_due);
            String anniversary = ": " + vehicleData.getString("ins_due");
            if (tv_ins_due != null) {
                tv_ins_due.setText(anniversary);
            }

            TextView tv_permit_due = bottomSheetDialog.findViewById(R.id.tv_permit_due);
            String category = ": " + vehicleData.getString("permit_due");
            if (tv_permit_due != null) {
                tv_permit_due.setText(category);
            }

            TextView tv_fc_due = bottomSheetDialog.findViewById(R.id.tv_fc_due);
            String product = ": " + vehicleData.getString("fc_due");
            if (tv_fc_due != null) {
                tv_fc_due.setText(product);
            }

            TextView tv_vehicle_mileage = bottomSheetDialog.findViewById(R.id.tv_vehicle_mileage);
            String address = ": " + vehicleData.getString("vehicle_mileage");
            if (tv_vehicle_mileage != null) {
                tv_vehicle_mileage.setText(address);
            }

            TextView tv_vehicle_number = bottomSheetDialog.findViewById(R.id.tv_vehicle_number);
            String carName = ": " + vehicleData.getString("reg_no");
            if (tv_vehicle_number != null) {
                tv_vehicle_number.setText(carName);
            }

        } catch (Exception e) {
            Log.e("VehicleDetailsError", "Exception: " + e.getMessage(), e);
        }

    }

}