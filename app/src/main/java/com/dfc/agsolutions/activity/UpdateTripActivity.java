package com.dfc.agsolutions.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dfc.agsolutions.model.FetchVendorDataModel;
import com.dfc.agsolutions.model.OngoingTruckTypeModel;
import com.dfc.agsolutions.model.UpdateTripModel;
import com.dfc.agsolutions.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdateTripActivity extends AppCompatActivity {

    TextView tv_branch_name;
    TextView bhsd;
    TextView tv_kilM;
    TextView tv_fhsd;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    private Spinner spinnerBranches;
    private Spinner spinnerDriver;
    private Spinner spinnerAgent;
    private Spinner spinnerSupplier;
    ProgressDialog dialog;

    String currentDate;

    EditText edtAdvance, edtShsd, edtRemark;
    String trip_advance;
    String trip_SHSD;

    String trip_remarks;
    String trip_bhsd;

    List<String> mileageArray = new ArrayList<>();
    RelativeLayout relative;

    OngoingTruckTypeModel ongoingTruckTypeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_trip);

        ongoingTruckTypeModel = (OngoingTruckTypeModel) getIntent().getSerializableExtra("pass_data");

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();
        dialog = new ProgressDialog(UpdateTripActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        Date currentDate = new Date();

        // Define the desired date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Format the date
        this.currentDate = dateFormat.format(currentDate);

        tv_branch_name = findViewById(R.id.tv_login_account_txt);

        bhsd = findViewById(R.id.bhsd);
        String tripBhsd = ongoingTruckTypeModel.getTrip_bhsd() + " Ltr";
        bhsd.setText(tripBhsd);

        tv_kilM = findViewById(R.id.tv_kilo_m);

        String tripKilM = ongoingTruckTypeModel.getTrip_km() + " Km";
        tv_kilM.setText(tripKilM);

        tv_fhsd = findViewById(R.id.fhsd);
        String trip_hsd = ongoingTruckTypeModel.getTrip_hsd() + " Ltr";
        tv_fhsd.setText(trip_hsd);

        edtAdvance = findViewById(R.id.et_advance);
        edtAdvance.setText(ongoingTruckTypeModel.getTrip_advance());

        edtShsd = findViewById(R.id.et_shsd);
        edtShsd.setText(ongoingTruckTypeModel.getTrip_hsd_supplied());

        edtRemark = findViewById(R.id.et_remark);

        relative = findViewById(R.id.relative);

        String branchName = " " + sp.getString("userBranch", "");
        tv_branch_name.setText(branchName);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());


        spinnerBranches = findViewById(R.id.spinnerBranches);
        spinnerDriver = findViewById(R.id.spinner_driver);
        spinnerAgent = findViewById(R.id.spinner_agent);
        spinnerSupplier = findViewById(R.id.spinner_supplier);

        spinnerBranches.getBackground().setColorFilter(ContextCompat.getColor(this,
                        R.color.white),
                PorterDuff.Mode.SRC_ATOP);
        spinnerDriver.getBackground().setColorFilter(ContextCompat.getColor(this,
                        R.color.white),
                PorterDuff.Mode.SRC_ATOP);
        spinnerAgent.getBackground().setColorFilter(ContextCompat.getColor(this,
                        R.color.white),
                PorterDuff.Mode.SRC_ATOP);
        spinnerSupplier.getBackground().setColorFilter(ContextCompat.getColor(this,
                        R.color.white),
                PorterDuff.Mode.SRC_ATOP);

        vehicleDriverArray.add("Select Driver");

        ArrayAdapter<String> adapterDriver = new ArrayAdapter<>(UpdateTripActivity.this, R.layout.simple_spinner_item, vehicleDriverArray);
        adapterDriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDriver.setAdapter(adapterDriver);

        findViewById(R.id.cvUpdate).setOnClickListener(v -> {

            Log.e("trip_bhsd", "trip_bhsd:-  " + trip_bhsd);

            if (edtAdvance.getText().toString().isEmpty()) {
                trip_advance = "0";
            } else {
                trip_advance = edtAdvance.getText().toString();
            }

            if (edtShsd.getText().toString().isEmpty()) {
                trip_SHSD = "0";
            } else {
                trip_SHSD = edtShsd.getText().toString();
            }


            try {
                trip_remarks = edtRemark.getText().toString();
            } catch (Exception e) {
                Log.e("Exception", e.toString());
            }

            getUpdateData();

        });

        getBranch();
        getDriver();
        getAgent();
        get_vendor();

    }

    List<String> vehicleArray = new ArrayList<>();
    List<String> vehicleDriverArray = new ArrayList<>();

    List<String> agentArray = new ArrayList<>();
    List<String> vendorArray = new ArrayList<>();
    List<String> kmArray = new ArrayList<>();

    private OkHttpClient.Builder createHttpClient() {
        return new OkHttpClient.Builder();
    }

    public void getBranch() {

        vehicleArray.clear();
        mileageArray.clear();

        vehicleArray.add(ongoingTruckTypeModel.getTrip_vehicle());
        mileageArray.add("0");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(UpdateTripActivity.this,
                R.layout.simple_spinner_item,
                vehicleArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranches.setAdapter(adapter);

    }

    public void getDriver() {

        vehicleDriverArray.clear();
        vehicleDriverArray.add(ongoingTruckTypeModel.getTrip_driver());

        ArrayAdapter<String> adapterDriver = new ArrayAdapter<>(this,
                R.layout.simple_spinner_item,
                vehicleDriverArray);
        adapterDriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDriver.setAdapter(adapterDriver);

    }

    public void getAgent() {
        agentArray.clear();
        kmArray.clear();

        agentArray.add(ongoingTruckTypeModel.getTrip_agency());
        ArrayAdapter<String> adapterDriver = new ArrayAdapter<>(UpdateTripActivity.this,
                R.layout.simple_spinner_item,
                agentArray);
        adapterDriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAgent.setAdapter(adapterDriver);
    }

    public void get_vendor() {

        vendorArray.clear();
        vendorArray.add("Select Supplier");

        OkHttpClient.Builder httpClient = createHttpClient();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer " +
                            sp.getString("token", ""))
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

        Call<FetchVendorDataModel> call = loginService.fetchAgent(sp.getString("userBranch",
                        ""),
                "Diesel");

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<FetchVendorDataModel> call,
                                   @NonNull Response<FetchVendorDataModel> response) {
                Log.e("response..", " " + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    ArrayList<FetchVendorDataModel> branches = response.body().getData();

                    for (FetchVendorDataModel branch : branches) {
                        vendorArray.add(branch.getVendor_name());
                    }

                    ArrayAdapter<String> adapterDriver = new ArrayAdapter<>(UpdateTripActivity.this,
                            R.layout.simple_spinner_item,
                            vendorArray);

                    adapterDriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSupplier.setAdapter(adapterDriver);
                    spinnerSupplier.setSelection(1);

                    Log.e("response..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(UpdateTripActivity.this,
                            "Network Error!!",
                            Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<FetchVendorDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("FetchVendorDataModel", "" + t);
                dialog.dismiss();
            }
        });
    }

    public void getUpdateData() {
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

        Api loginService = retrofit.create(Api.class);

        Call<UpdateTripModel> call = loginService.updateTrip(ongoingTruckTypeModel.getId(),
                trip_advance,
                trip_bhsd,
                trip_remarks);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<UpdateTripModel> call,
                                   @NonNull Response<UpdateTripModel> response) {

                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    Toast.makeText(UpdateTripActivity.this,
                            response.body().getMsg(),
                            Toast.LENGTH_SHORT).show();

                    finish();

                } else {
                    Toast.makeText(UpdateTripActivity.this,
                            response.body().getMsg(),
                            Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<UpdateTripModel> call,
                                  @NonNull Throwable t) {
                Log.e("UpdateTripModel", "" + t);
                dialog.dismiss();
            }

        });
    }

}