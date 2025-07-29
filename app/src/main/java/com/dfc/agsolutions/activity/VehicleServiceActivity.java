package com.dfc.agsolutions.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.icu.util.Calendar;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dfc.agsolutions.model.GarageDataModel;
import com.dfc.agsolutions.model.ServiceFatchVhicalDataModel;
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

    String selectedItem, selectedItem1, selectedDate;

    TextView date;

    EditText tamount, km, description;

    SharedPreferences sp;

    SharedPreferences.Editor ed;

    String vhical;

    Spinner spinner, spinner1;

    ProgressDialog dialog;

    ImageView nextbtn;

    String edate,evehicle,pump,totalamount,ekm,edescription;
    String Brance = "BPC DHARWAD";
    String Brance1 = "HPC Mangalore";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicle_service_activity);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();

//        ed.putString("userbranch", );
//        ed.commit();

        nextbtn = findViewById(R.id.iv_next_btn);
        date = findViewById(R.id.date);
        tamount = findViewById(R.id.et_total_amount);
        km = findViewById(R.id.km);
        description = findViewById(R.id.description);

        dialog = new ProgressDialog(VehicleServiceActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        LinearLayout date1 = findViewById(R.id.date_picker);

        date1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Create DatePickerDialog and show it
                DatePickerDialog datePickerDialog = new DatePickerDialog(VehicleServiceActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                                // Do something with the selected date
                                // selectedYear, selectedMonth, and selectedDay are the selected date values
                                selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                                String setselectdate1 = selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear;

//                                 selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                                date.setText(setselectdate1);

                            }
                        }, year, month, day);


                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

                datePickerDialog.show();
            }
        });

        spinner = findViewById(R.id.spinner);
        spinner1 = findViewById(R.id.spinner1);
        spinner.getBackground().setColorFilter(getResources().getColor(R.color.black1), PorterDuff.Mode.SRC_ATOP);
        spinner1.getBackground().setColorFilter(getResources().getColor(R.color.black1), PorterDuff.Mode.SRC_ATOP);


//        spinner = findViewById(R.id.spinner);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_items, R.layout.spinnritam);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);

//        spinner1 = findViewById(R.id.spinner1);
//        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.spinner_items1, R.layout.spinnritam);
//        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner1.setAdapter(adapter1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected item here
//                selectedItem = parentView.getItemAtPosition(position).toString();
                evehicle = (String) parentView.getItemAtPosition(position);
//                vhical = selectedBranch;
//                vhical =
                // Do something with the selected item
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });


        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected item here
                pump = parentView.getItemAtPosition(position).toString();
                // Do something with the selected item
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        findViewById(R.id.back).setOnClickListener(v -> {
            finish();
        });

        get_vehicle();

        vhicalarray.add("Select Vehicle");
        garag.add("Select Garage");

        get_garag();

        nextbtn.setOnClickListener(v -> {

            edate = date.getText().toString().trim();
            totalamount = tamount.getText().toString().trim();
            ekm = km.getText().toString().trim();
            edescription = description.getText().toString().trim();

            if(!totalamount.isEmpty() && !ekm.isEmpty() && !evehicle.isEmpty() && !pump.isEmpty() && !selectedDate.isEmpty()) {
//                Creat_Payment();

                Creat_Service_Status(selectedDate,evehicle,pump,totalamount,ekm,edescription);
//                startActivity(new Intent(VehicleServiceActivity.this,VehicleServiceAmountActivity.class).
//                        putExtra("date",edate).
//                        putExtra("vehicle",evehicle).
//                        putExtra("garage",pump).
//                        putExtra("amount",totalamount).
//                        putExtra("km",ekm).
//                        putExtra("description",edescription));
            }else{
//                startActivity(new Intent(VehicleServiceActivity.this,VehicleServiceAmountActivity.class));
//                if (edate.isEmpty()) {
//                    Toast.makeText(this, "Select Date", Toast.LENGTH_SHORT).show();
                if (totalamount.isEmpty()) {
                    tamount.setError("Enter Total Amount");
                } if (ekm.isEmpty()) {
                    km.setError("Enter KM");
                } if (edate.isEmpty()) {
                    date.setError("Enter Date");
                }else {
                    Toast.makeText(this, "Enter Details", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //    ArrayList<ServiceFatchVhicalDataModel> vhicalarray;
    List<String> vhicalarray = new ArrayList<>();
    List<String> garag = new ArrayList<>();


    public void Creat_Service_Status(String edate, String evehicle, String pump, String totalamount, String ekm, String edescription) {



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




        Call<ServiceStatusDataModel> call = loginservice.get_getServiceStatus(edate,"2023-24",evehicle,pump,ekm,totalamount,edescription);

        call.enqueue(new Callback<ServiceStatusDataModel>() {
            @Override
            public void onResponse(Call<ServiceStatusDataModel> call, Response<ServiceStatusDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {


                    ServiceStatusDataModel apiResponse = response.body();

                    ServiceStatusDataModel.UserData userData = apiResponse.getData();

                    if (userData != null) {
                        String amount = userData.getService_amount();
                        String service_ref = userData.getService_ref();

                        startActivity(new Intent(VehicleServiceActivity.this, ServiceBookList.class)
                                .putExtra("amount",amount).putExtra("service_ref",service_ref));


                    }

                    Toast.makeText(VehicleServiceActivity.this, "" + response.body().getMsg(), Toast.LENGTH_SHORT).show();
//                    Log.e("responce..", "branches:-  " + branches.size());


                } else {
                    Toast.makeText(VehicleServiceActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
//                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<ServiceStatusDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
//                dialog.dismiss();
            }
        });

    }



    public void get_vehicle() {
        dialog.show();
//        vhicalarray.clear();
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
        Call<ServiceFatchVhicalDataModel> call = loginservice.get_servicefatchvhiclelist(sp.getString("userBranch", ""));
        call.enqueue(new Callback<ServiceFatchVhicalDataModel>() {
            @Override
            public void onResponse(Call<ServiceFatchVhicalDataModel> call, Response<ServiceFatchVhicalDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {
//
                    ArrayList<ServiceFatchVhicalDataModel> branches = response.body().getData();


                    for (ServiceFatchVhicalDataModel branch : branches) {
                        vhicalarray.add(branch.getReg_no());
                        Log.e("getReg_no", "getReg_no: "+branch.getReg_no());
                    }
//
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(VehicleServiceActivity.this, R.layout.simple_spinner_item1, vhicalarray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);

                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(VehicleServiceActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<ServiceFatchVhicalDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
    }

    String Diesel = "Diesel";

    public void get_garag() {
        dialog.show();
//        garag.clear();
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
        Call<GarageDataModel> call = loginservice.get_Garage(Brance1,"Garage");
        call.enqueue(new Callback<GarageDataModel>() {
            @Override
            public void onResponse(Call<GarageDataModel> call, Response<GarageDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {
//
                    ArrayList<GarageDataModel> branches = response.body().getData();

//response.body().getData().get(0).getReg_no();
//                    for (GarageDataModel branch : branches) {
//                        vhicalarray.add(branch.getReg_no());
////                        vhicaldraiverarray.add(branch.getVehicle_driver());
////                        milageaaray.add(branch.getVehicle_mileage());
//                    }
                    for (GarageDataModel branch : branches) {
                        garag.add(branch.getVendor_name());
                    }
//
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<>(VehicleServiceActivity.this, R.layout.simple_spinner_item1, garag);
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner1.setAdapter(adapter1);
//
//                    ArrayAdapter<String> adapter1 = new ArrayAdapter<>(VehicleServiceActivity.this, R.layout.simple_spinner_item1, vhicalarray);
//                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinner1.setAdapter(adapter1);

//
//                    ArrayAdapter<String> adapterdriver = new ArrayAdapter<>(CreatTrip.this, R.layout.simple_spinner_item, vhicaldraiverarray);
//                    adapterdriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinnerdriver.setAdapter(adapterdriver);


//                    setupSpinner(branchNames);
                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(VehicleServiceActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<GarageDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
    }






}