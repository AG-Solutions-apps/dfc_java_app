package com.dfc.agsolutions.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dfc.agsolutions.model.FetchBHSDDataModel;
import com.dfc.agsolutions.model.FetchDriverDataModel;
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

    TextView brandname;
    TextView bhsd;
    TextView kilom;
    TextView fhsd;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    private Spinner spinnerBranches;
    private Spinner spinnerdriver;
    private Spinner spinneragetnt;
    private Spinner spinnersupplayer;
    ProgressDialog dialog;

    int km = 0;
    int fhsds = 0;
    Double mil = 0.0;
    String currentdDate;
    String trip_vehicle = null;
    String trip_driver = null;
    String trip_agency = null;

    EditText edtadvance, edtshsd, edtremark;
    String trip_advance;
    String trip_SHSD;
    String trip_supplier;
    String trip_remarks;
    String trip_bhsd;

    List<String> branchList = new ArrayList<>();
    List<String> milageaaray = new ArrayList<>();
    RelativeLayout relative;

    boolean driver = false;

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

//        milageaaray.add("Select Vehicle");

        Date currentDate = new Date();

        // Define the desired date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Format the date
        currentdDate = dateFormat.format(currentDate);

        brandname = findViewById(R.id.tv_login_account_txt);

        bhsd = findViewById(R.id.bhsd);
        bhsd.setText(ongoingTruckTypeModel.getTrip_bhsd() + " Ltr");

        kilom = findViewById(R.id.tv_kilo_m);
        kilom.setText(ongoingTruckTypeModel.getTrip_km() + " Km");

        fhsd = findViewById(R.id.fhsd);
        fhsd.setText(ongoingTruckTypeModel.getTrip_hsd() + " Ltr");

        edtadvance = findViewById(R.id.et_advance);
        edtadvance.setText(ongoingTruckTypeModel.getTrip_advance());

        edtshsd = findViewById(R.id.et_shsd);
        edtshsd.setText(ongoingTruckTypeModel.getTrip_hsd_supplied());

        edtremark = findViewById(R.id.et_remark);

        relative = findViewById(R.id.relative);
        brandname.setText("" + sp.getString("userBranch", ""));

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());


        spinnerBranches = findViewById(R.id.spinnerBranches);
        spinnerdriver = findViewById(R.id.spinner_driver);
        spinneragetnt = findViewById(R.id.spinner_agent);
        spinnersupplayer = findViewById(R.id.spinner_supplier);

        spinnerBranches.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        spinnerdriver.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        spinneragetnt.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        spinnersupplayer.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

//        spinnerBranches.setVisibility(View.GONE);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, milageaaray);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        spinnerBranches.setAdapter(adapter);
        vhicaldraiverarray.add("Select Driver");

        ArrayAdapter<String> adapterdriver = new ArrayAdapter<>(UpdateTripActivity.this, R.layout.simple_spinner_item, vhicaldraiverarray);
        adapterdriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerdriver.setAdapter(adapterdriver);


/*
        spinnerBranches.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedBranch1 = (String) parentView.getItemAtPosition(position);
                trip_vehicle = selectedBranch1;

                if (position > 0) {
//                    vhicaldraiverarray.remove(0);
                    String selectedBranch = (String) parentView.getItemAtPosition(position);
                    trip_vehicle = selectedBranch;
//                    vhicaldraiverarray.remove(0);
                    try {
                        get_BHSD(selectedBranch);


                        mil = Double.parseDouble(milageaaray.get(position));
                        Log.e("dsaadasd", "mile:-  " + milageaaray.get(position));
                        Log.e("dsaadasd", "mileSS:-  " + mil);


//                        fhsds = km / mil;
//
//
//                        reloafhsd();


                    } catch (Exception e) {
                        throw new RuntimeException(e);


                    }
                    if (trip_vehicle != null) {
//                        get_driver();

                        vhicaldraiverarray.remove(0);
                        vhicaldraiverarray.addAll(demovhicaldraiverarray);

                        driver = true;
                        ArrayAdapter<String> adapterdriver = new ArrayAdapter<>(UpdateTripActivity.this, R.layout.simple_spinner_item, vhicaldraiverarray);
                        adapterdriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerdriver.setAdapter(adapterdriver);
                    }

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });


        spinnerdriver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedBranch1 = (String) parentView.getItemAtPosition(position);
                trip_driver = selectedBranch1;

                if (!trip_driver.equals("Select Driver")) {

                    get_driver();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        spinneragetnt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedBranch1 = (String) parentView.getItemAtPosition(position);
                trip_agency = selectedBranch1;
                if (position > 0) {
                    String selectedBranch = (String) parentView.getItemAtPosition(position);
                    trip_agency = selectedBranch;

                    try {
                        String name = kmarray.get(position - 1);
                        km = (int) Double.parseDouble(name);
                        Log.e("asdadasada", "pos:-   " + position);
                        kilom.setText("" + name + " Km");
                        fhsds = (int) (km / mil);
                        reloafhsd();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }


                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        spinnersupplayer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedBranch1 = (String) parentView.getItemAtPosition(position);
                trip_supplier = selectedBranch1;

                if (position > 0) {
                    String selectedBranch = (String) parentView.getItemAtPosition(position);
                    trip_supplier = selectedBranch;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here

            }
        });
*/

        findViewById(R.id.cvUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("trip_bhsd", "trip_bhsd:-  " + trip_bhsd);


                if (edtadvance.getText().toString().length() == 0) {
                    trip_advance = "0";
                } else {
                    trip_advance = edtadvance.getText().toString();
                }


            /*    if (trip_supplier.equals("Select Supplier")) {
                    trip_supplier = "";

                } else {

//                    trip_advance = edtadvance.getText().toString();
                }*/


                if (edtshsd.getText().toString().length() == 0) {
                    trip_SHSD = "0";
                } else {
                    trip_SHSD = edtshsd.getText().toString();
                }


                try {
                    trip_remarks = edtremark.getText().toString();
                } catch (Exception e) {
                }

        /*        if (trip_vehicle.equals("Select Vehicle")) {
                    Toast.makeText(UpdateTripActivity.this, "Please Select Vehicle", Toast.LENGTH_SHORT).show();
                } else if (trip_driver.equals("Select Driver")) {
                    Toast.makeText(UpdateTripActivity.this, "Please Select Driver", Toast.LENGTH_SHORT).show();
                } else if (trip_agency.equals("Select Agent")) {
                    Toast.makeText(UpdateTripActivity.this, "Please Select Agent", Toast.LENGTH_SHORT).show();
                } else {*/
                get_updatedata();
//                }

            }
        });

//        spinnerdriver.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                get_driver();
//
//                return false;
//            }
//        });


        spinnerdriver.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
//                     Dropdown was clicked, perform your action here

//                    if (!trip_driver.equals("Select Driver")) {
//
//                        get_driver();
//                    }
//                    Toast.makeText(CreatTrip.this, "Dropdown clicked", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        get_branch();
        get_driver();
        get_Aggetnt();
        get_vendor();

    }

    List<String> vhicalarray = new ArrayList<>();
    List<String> vhicaldraiverarray = new ArrayList<>();
    List<String> demovhicaldraiverarray = new ArrayList<>();
    List<String> aggentrarray = new ArrayList<>();
    List<String> vendorrarray = new ArrayList<>();
    List<String> kmarray = new ArrayList<>();

    public void reloafhsd() {

        fhsd.setText("" + fhsds + " Ltr");

    }

    public void get_branch() {
//        dialog.show();
        vhicalarray.clear();
        milageaaray.clear();
//        vhicaldraiverarray.clear();
//        branchList.clear();
        vhicalarray.add(ongoingTruckTypeModel.getTrip_vehicle());
        milageaaray.add("0");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(UpdateTripActivity.this, R.layout.simple_spinner_item, vhicalarray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranches.setAdapter(adapter);

 /*
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
        Call<FatchVhicalDataModel> call = loginservice.get_fatchvhiclelist(sp.getString("userbranch", ""));
        call.enqueue(new Callback<FatchVhicalDataModel>() {
            @Override
            public void onResponse(Call<FatchVhicalDataModel> call, Response<FatchVhicalDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {

                    ArrayList<FatchVhicalDataModel> branches = response.body().getData();

                    for (FatchVhicalDataModel branch : branches) {
                        vhicalarray.add(branch.getReg_no());
                        demovhicaldraiverarray.add(branch.getVehicle_driver());
                        milageaaray.add(branch.getVehicle_mileage());

                        Log.e("vhicalarrayv", "vhicalarray: " + vhicalarray);
                        Log.e("vhicaldraiverarray", "vhicaldraiverarray: " + branch.getVehicle_driver());
                        Log.e("milageaaray", "milageaaray: " + milageaaray);

                    }


                    ArrayAdapter<String> adapter = new ArrayAdapter<>(UpdateTripActivity.this, R.layout.simple_spinner_item, vhicalarray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerBranches.setAdapter(adapter);


//                    setupSpinner(branchNames);
                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(UpdateTripActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
//                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<FatchVhicalDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
//                dialog.dismiss();
            }
        });*/
    }

    ArrayList<FetchDriverDataModel> branchesdriver = new ArrayList<>();

    public void get_driver() {
//        dialog.show();
        vhicaldraiverarray.clear();
        vhicaldraiverarray.add(ongoingTruckTypeModel.getTrip_driver());


        ArrayAdapter<String> adapterdriver = new ArrayAdapter<>(this, R.layout.simple_spinner_item, vhicaldraiverarray);
        adapterdriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerdriver.setAdapter(adapterdriver);

/*
//        vhicaldraiverarray.add("Select Driver");
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
        Call<FatchDriverDataModel> call = loginservice.get_fatchdriver(sp.getString("userbranch", ""));
        call.enqueue(new Callback<FatchDriverDataModel>() {
            @Override
            public void onResponse(Call<FatchDriverDataModel> call, Response<FatchDriverDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {

                    branchesdriver = response.body().getData();
                    vhicaldraiverarray.clear();

                    for (FatchDriverDataModel branch : branchesdriver) {
                        vhicaldraiverarray.add(branch.getFull_name());
                    }
//
//                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CreatTrip.this, R.layout.simple_spinner_item, vhicalarray);
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinnerBranches.setAdapter(adapter);


//                    ArrayAdapter<String> adapterdriver = new ArrayAdapter<>(CreatTrip.this, R.layout.simple_spinner_item, vhicaldraiverarray);
//                    adapterdriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinnerdriver.setAdapter(adapterdriver);
//

//                    setupSpinner(branchNames);
                    Log.e("responce..", "branches:-  " + branchesdriver.size());

                } else {
                    Toast.makeText(UpdateTripActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<FatchDriverDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });*/
    }

    public void get_Aggetnt() {
//        dialog.show();
        aggentrarray.clear();
        kmarray.clear();
        aggentrarray.add(ongoingTruckTypeModel.getTrip_agency());
        ArrayAdapter<String> adapterdriver = new ArrayAdapter<>(UpdateTripActivity.this, R.layout.simple_spinner_item, aggentrarray);
        adapterdriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinneragetnt.setAdapter(adapterdriver);

/*
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
        Call<FatchAggencyDataModel> call = loginservice.get_fetch_agency(sp.getString("userbranch", ""));
        call.enqueue(new Callback<FatchAggencyDataModel>() {
            @Override
            public void onResponse(Call<FatchAggencyDataModel> call, Response<FatchAggencyDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {

                    ArrayList<FatchAggencyDataModel> branches = response.body().getData();

                    for (FatchAggencyDataModel branch : branches) {
                        aggentrarray.add(branch.getAgency_name());
                        kmarray.add(branch.getAgency_rt_km());
                    }
//
//                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CreatTrip.this, R.layout.simple_spinner_item, vhicalarray);
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinnerBranches.setAdapter(adapter);


                    ArrayAdapter<String> adapterdriver = new ArrayAdapter<>(UpdateTripActivity.this, R.layout.simple_spinner_item, aggentrarray);
                    adapterdriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinneragetnt.setAdapter(adapterdriver);


//                    setupSpinner(branchNames);
                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(UpdateTripActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<FatchAggencyDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });*/
    }

    public void get_vendor() {
//        dialog.show();
        vendorrarray.clear();
        vendorrarray.add("Select Supplier");


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
        Call<FetchVendorDataModel> call = loginservice.get_fatchaggent(sp.getString("userBranch", ""), "Diesel");
        call.enqueue(new Callback<FetchVendorDataModel>() {
            @Override
            public void onResponse(Call<FetchVendorDataModel> call, Response<FetchVendorDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {

                    ArrayList<FetchVendorDataModel> branches = response.body().getData();

                    for (FetchVendorDataModel branch : branches) {
                        vendorrarray.add(branch.getVendor_name());
                    }
//
//                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CreatTrip.this, R.layout.simple_spinner_item, vhicalarray);
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinnerBranches.setAdapter(adapter);


                    ArrayAdapter<String> adapterdriver = new ArrayAdapter<>(UpdateTripActivity.this, R.layout.simple_spinner_item, vendorrarray);
                    adapterdriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnersupplayer.setAdapter(adapterdriver);
                    spinnersupplayer.setSelection(1);

//                    setupSpinner(branchNames);
                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(UpdateTripActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<FetchVendorDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
    }

    String trip_hsd, trip_hsd_supplied;

    public void get_BHSD(String vhnomber) {
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
        Call<FetchBHSDDataModel> call = loginservice.get_fetch_bhsd(vhnomber);
        call.enqueue(new Callback<FetchBHSDDataModel>() {
            @Override
            public void onResponse(Call<FetchBHSDDataModel> call, Response<FetchBHSDDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {

                    ArrayList<FetchBHSDDataModel> branches = response.body().getData();

                    for (FetchBHSDDataModel branch : branches) {
                        bhsd.setText("" + branch.getTrip_hsd() + " Ltr");
                        trip_bhsd = branch.getTrip_hsd();
                        trip_hsd = branch.getTrip_hsd();
                        trip_hsd_supplied = branch.getTrip_hsd_supplied();

//                        vendorrarray.add(branch.getTrip_hsd());

                    }

//                    setupSpinner(branchNames);
                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(UpdateTripActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<FetchBHSDDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
    }


    public void get_updatedata() {
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

        Call<UpdateTripModel> call = loginservice.get_update_trip(ongoingTruckTypeModel.getId(),
                trip_advance,
                trip_bhsd,
                trip_remarks);


        call.enqueue(new Callback<UpdateTripModel>() {
            @Override
            public void onResponse(Call<UpdateTripModel> call, Response<UpdateTripModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {

                    Toast.makeText(UpdateTripActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                    finish();

//                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(UpdateTripActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<UpdateTripModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
    }
}