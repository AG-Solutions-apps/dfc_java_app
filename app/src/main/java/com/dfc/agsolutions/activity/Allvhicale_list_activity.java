package com.dfc.agsolutions.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.dfc.agsolutions.model.FetchAllVehicleDataModel;
import com.dfc.agsolutions.model.VehicleDetailsModel;
import com.dfc.agsolutions.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Allvhicale_list_activity extends AppCompatActivity {

    ImageView icback;

    RecyclerView rv;
    SharedPreferences sp;
    SharedPreferences.Editor ed;

    String totalAmount;
    String totalReceived;

    ProgressDialog dialog;

    TextView totalamount;
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
                Expenses_List();
            }
        });
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        icback = findViewById(R.id.iv_back);
        totalamount = findViewById(R.id.tv_total_amount);
        icback.setOnClickListener(v -> finish());
        Expenses_List();

    }


    public void Expenses_List() {

        dialog.show();
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
        Call<FetchAllVehicleDataModel> call = loginservice.get_allvhicleList(sp.getString("userBranch", ""));
        call.enqueue(new Callback<FetchAllVehicleDataModel>() {
            @Override
            public void onResponse(Call<FetchAllVehicleDataModel> call, Response<FetchAllVehicleDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {

                    nodata.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                    Home_Today_list_Adapter adapter = new Home_Today_list_Adapter(Allvhicale_list_activity.this,
                            response.body().getData());
                    rv.setAdapter(adapter);


//                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    nodata.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
                    Toast.makeText(Allvhicale_list_activity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);


            }

            @Override
            public void onFailure(Call<FetchAllVehicleDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
                nodata.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

            }
        });

    }

    public class Home_Today_list_Adapter extends RecyclerView.Adapter<Home_Today_list_Adapter.Holder> {
        private Context context;
        ArrayList<FetchAllVehicleDataModel> data;

        public Home_Today_list_Adapter(Context context, ArrayList<FetchAllVehicleDataModel> data) {
            this.context = context;
            this.data = data;
        }


        @Override
        public int getItemCount() {
            return data.size();
        }

        @NonNull
        @Override
        public Home_Today_list_Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_vehicle, parent, false);
            return new Home_Today_list_Adapter.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final Home_Today_list_Adapter.Holder holder, @SuppressLint("RecyclerView") final int position) {


//            holder.date.setText(data.get(position).getPayment_details_date());
            try {
                holder.carname.setText(" " + data.get(position).getReg_no());
                holder.details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Car_details(data.get(position).getReg_no());

                    }
                });
                holder.trips.setOnClickListener(v -> {
//                        v_name
                    Intent intent = new Intent(context, VehicleTripHistoryActivity.class);
                    intent.putExtra("v_name",data.get(position).getReg_no());
                    startActivity(intent);

                });

            } catch (Exception e) {

            }


        }

        class Holder extends RecyclerView.ViewHolder {

            TextView carname;
            //            ImageView profile;
            LinearLayout details,trips;


            public Holder(@NonNull View itemView) {
                super(itemView);

                carname = itemView.findViewById(R.id.tv_car_name);
                details = itemView.findViewById(R.id.details);
                trips = itemView.findViewById(R.id.trips);


            }
        }


    }

    public void Car_details(String reg_no) {

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
        Call<VehicleDetailsModel> call = loginservice.get_v_details(reg_no);
        call.enqueue(new Callback<VehicleDetailsModel>() {
            @Override
            public void onResponse(Call<VehicleDetailsModel> call, Response<VehicleDetailsModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode() == 200) {

                    countryDialoglogout(response.body().getData());

//                    Log.e("responce..", "branches:-  " + branches.size());

                } else {

                    Toast.makeText(Allvhicale_list_activity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();


            }

            @Override
            public void onFailure(Call<VehicleDetailsModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();


            }
        });

    }

    private void countryDialoglogout(VehicleDetailsModel.DataModel data) {

        if (data == null) {
            Toast.makeText(this, "Vehicle data is not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Allvhicale_list_activity.this,
                R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
        bottomSheetDialog.show();

        TextView Firm_Name = bottomSheetDialog.findViewById(R.id.Firm_Name);
        Firm_Name.setText(": " + data.getVehicleBranch());

        TextView Mobile = bottomSheetDialog.findViewById(R.id.Mobile);
        Mobile.setText(": " + data.getVehicleCompany());

        TextView Email = bottomSheetDialog.findViewById(R.id.Email);
        Email.setText(": " + data.getVehicleType());

        TextView DOB = bottomSheetDialog.findViewById(R.id.DOB);
        DOB.setText(": " + data.getManufacturingYear());

        TextView Anniversary = bottomSheetDialog.findViewById(R.id.Anniversary);
        Anniversary.setText(": " + data.getInsuranceDue());

        TextView Category = bottomSheetDialog.findViewById(R.id.Category);
        Category.setText(": " + data.getPermitDue());

        TextView Product = bottomSheetDialog.findViewById(R.id.Product);
        Product.setText(": " + data.getFcDue());


        TextView Address = bottomSheetDialog.findViewById(R.id.Address);
        Address.setText(": " + data.getVehicleMileage());

        TextView carname = bottomSheetDialog.findViewById(R.id.tv_car_name);
        carname.setText(": " + data.getRegNo());


    }


}