package com.dfc.agsolutions.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dfc.agsolutions.model.GarageDataModel;
import com.dfc.agsolutions.model.ServiceFetchVehicleDataModel;
import com.dfc.agsolutions.model.ServiceStatusDataModel;
import com.dfc.agsolutions.R;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VehicleServiceActivity extends AppCompatActivity {

    String selectedDate;

    TextView date;

    EditText tAmount, km, description;

    SharedPreferences sp;

    SharedPreferences.Editor ed;

    Spinner spinner, spinner1;

    ProgressDialog dialog;

    ImageView iv_next_btn;

    String eDate, eVehicle, pump, totalAmount, ekm, eDescription;
//    String branch = "BPC DHARWAD";
    String branch1 = "HPC Mangalore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicle_service_activity);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();

        iv_next_btn = findViewById(R.id.iv_next_btn);
        date = findViewById(R.id.date);
        tAmount = findViewById(R.id.et_total_amount);
        km = findViewById(R.id.km);
        description = findViewById(R.id.description);

        dialog = new ProgressDialog(VehicleServiceActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        LinearLayout date1 = findViewById(R.id.date_picker);

        date1.setOnClickListener(v -> {

            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create DatePickerDialog and show it
            DatePickerDialog datePickerDialog = new DatePickerDialog(VehicleServiceActivity.this,
                    (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                        // Do something with the selected date
                        // selectedYear, selectedMonth, and selectedDay are the selected date values
                        selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                        String setSelectDate1 = selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear;

                        date.setText(setSelectDate1);

                    }, year, month, day);

            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            datePickerDialog.show();
        });

        spinner = findViewById(R.id.spinner);
        spinner1 = findViewById(R.id.spinner_amount);

        spinner.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.black1),
                PorterDuff.Mode.SRC_ATOP);
        spinner1.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.black1),
                PorterDuff.Mode.SRC_ATOP);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected item here
                eVehicle = (String) parentView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView,
                                       int position,
                                       long id) {
                // Handle the selected item here
                pump = parentView.getItemAtPosition(position).toString();
                // Do something with the selected item
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        findViewById(R.id.back).setOnClickListener(v -> finish());

        getVehicle();

        vehicleArray.add("Select Vehicle");
        garage.add("Select Garage");

        getGarage();

        iv_next_btn.setOnClickListener(v -> {

            eDate = date.getText().toString().trim();
            totalAmount = tAmount.getText().toString().trim();
            ekm = km.getText().toString().trim();
            eDescription = description.getText().toString().trim();

            if(!totalAmount.isEmpty() &&
                    !ekm.isEmpty() &&
                    !eVehicle.isEmpty() &&
                    !pump.isEmpty() &&
                    !selectedDate.isEmpty()) {

                creatServiceStatus(selectedDate, eVehicle,pump, totalAmount,ekm, eDescription);

            } else {

                if (totalAmount.isEmpty()) {
                    tAmount.setError("Enter Total Amount");
                } if (ekm.isEmpty()) {
                    km.setError("Enter KM");
                } if (eDate.isEmpty()) {
                    date.setError("Enter Date");
                } else {
                    Toast.makeText(this, "Enter Details", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    List<String> vehicleArray = new ArrayList<>();
    List<String> garage = new ArrayList<>();

    public void creatServiceStatus(String eDate,
                                   String eVehicle,
                                   String pump,
                                   String totalAmount,
                                   String ekm,
                                   String eDescription) {

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
                .baseUrl(getString(R.string.commn_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        Api loginService = retrofit.create(Api.class);

        Call<ServiceStatusDataModel> call = loginService.getServiceStatus(eDate,"2023-24",
                eVehicle,pump,ekm,totalAmount,eDescription);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ServiceStatusDataModel> call,
                                   @NonNull Response<ServiceStatusDataModel> response) {
                Log.e("response..", response.toString());

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {


                    ServiceStatusDataModel apiResponse = response.body();

                    ServiceStatusDataModel.UserData userData = apiResponse.getData();

                    if (userData != null) {
                        String amount = userData.getService_amount();
                        String service_ref = userData.getService_ref();

                        startActivity(new Intent(VehicleServiceActivity.this, ServiceBookList.class)
                                .putExtra("amount",amount).putExtra("service_ref",service_ref));


                    }

                    Toast.makeText(VehicleServiceActivity.this,
                            response.body().getMsg(),
                            Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(VehicleServiceActivity.this,
                            "Network Error!!",
                            Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<ServiceStatusDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("ServiceStatusDataModel", t.toString());
            }
        });

    }

    public void getVehicle() {
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
                .baseUrl(getString(R.string.commn_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        Api loginService = retrofit.create(Api.class);
        Call<ServiceFetchVehicleDataModel> call = loginService.getServiceFetchVehicleList(sp.getString("userBranch", ""));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ServiceFetchVehicleDataModel> call,
                                   @NonNull Response<ServiceFetchVehicleDataModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {
//
                    ArrayList<ServiceFetchVehicleDataModel> branches = response.body().getData();


                    for (ServiceFetchVehicleDataModel branch : branches) {
                        vehicleArray.add(branch.getReg_no());
                        Log.e("getReg_no", "getReg_no: "+branch.getReg_no());
                    }
//
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(VehicleServiceActivity.this, R.layout.simple_spinner_item1, vehicleArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);

                    Log.e("response..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(VehicleServiceActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<ServiceFetchVehicleDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("ServiceFetchVehicleDataModel", "" + t);
                dialog.dismiss();
            }
        });
    }

    public void getGarage() {
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
                .baseUrl(getString(R.string.commn_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        Api loginService = retrofit.create(Api.class);
        Call<GarageDataModel> call = loginService.get_Garage(branch1,"Garage");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<GarageDataModel> call,
                                   @NonNull Response<GarageDataModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    ArrayList<GarageDataModel> branches = response.body().getData();

                    for (GarageDataModel branch : branches) {
                        garage.add(branch.getVendor_name());
                    }

                    ArrayAdapter<String> adapter1 = new ArrayAdapter<>(VehicleServiceActivity.this,
                            R.layout.simple_spinner_item1, garage);
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner1.setAdapter(adapter1);

                    Log.e("response..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(VehicleServiceActivity.this,
                            "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<GarageDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("GarageDataModel", "" + t);
                dialog.dismiss();
            }

        });

    }

}