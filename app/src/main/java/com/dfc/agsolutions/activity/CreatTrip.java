package com.dfc.agsolutions.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dfc.agsolutions.model.CreatTripModel;
import com.dfc.agsolutions.model.FetchAgencyDataModel;
import com.dfc.agsolutions.model.FetchBHSDDataModel;
import com.dfc.agsolutions.model.FetchDriverDataModel;
import com.dfc.agsolutions.model.FetchVendorDataModel;
import com.dfc.agsolutions.model.FetchVehicleDataModel;
import com.dfc.agsolutions.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

public class CreatTrip extends
        AppCompatActivity {

    TextView tv_brand_name;
    TextView tv_bhsd;
    TextView tv_kilo_m;
    TextView tv_fhsd;

    SharedPreferences sp;
    SharedPreferences.Editor ed;

    private Spinner spinnerBranches;
    private Spinner spinnerDriver;
    //    Spinner spinnerAgent;
    private TextView tv_agent;

    private Spinner spinnerSupplier;
    ProgressDialog dialog;

    int km = 0;
    int fhsd = 0;
    Double mil = 0.0;
    String currentDate;
    String trip_vehicle = null;
    String trip_driver = null;
    String trip_agency = "Select Agent";

    EditText edt_advance, edt_shsd, edt_remark;
    String trip_advance;
    String trip_SHSD, strDate = null;
    String trip_supplier;
    String trip_remarks;
    String trip_bhsd;

    List<String> mileage_array = new ArrayList<>();
    RelativeLayout relative;

    boolean driver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();

        dialog = new ProgressDialog(CreatTrip.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        Date currentDate = new Date();

        // Define the desired date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Format the date
        this.currentDate = dateFormat.format(currentDate);

        tv_brand_name = findViewById(R.id.tv_login_account_txt);
        tv_bhsd = findViewById(R.id.bhsd);
        tv_kilo_m = findViewById(R.id.tv_kilo_m);
        tv_fhsd = findViewById(R.id.fhsd);
        edt_advance = findViewById(R.id.et_advance);
        edt_shsd = findViewById(R.id.et_shsd);
        edt_remark = findViewById(R.id.et_remark);
        relative = findViewById(R.id.relative);

        String branchName = " " + sp.getString("userBranch", "");
        tv_brand_name.setText(branchName);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        spinnerBranches = findViewById(R.id.spinnerBranches);
        spinnerDriver = findViewById(R.id.spinner_driver);

//        spinnerAgent = findViewById(R.id.spinner_agent);
        tv_agent = findViewById(R.id.tv_agent);

        spinnerSupplier = findViewById(R.id.spinner_supplier);
        spinnerBranches.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.white),
                PorterDuff.Mode.SRC_ATOP);
        spinnerDriver.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.white),
                PorterDuff.Mode.SRC_ATOP);
//        spinnerAgent.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.white),
//                PorterDuff.Mode.SRC_ATOP);
        spinnerSupplier.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.white),
                PorterDuff.Mode.SRC_ATOP);

        vehicleDriverArray.add("Select Driver");

        ArrayAdapter<String> adapterDriver = new ArrayAdapter<>(CreatTrip.this,
                R.layout.simple_spinner_item,
                vehicleDriverArray);

        adapterDriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDriver.setAdapter(adapterDriver);

        spinnerBranches.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView,
                                       int position,
                                       long id) {
                trip_vehicle = parentView.getItemAtPosition(position).toString();

                if (position > 0) {

                    String selectedBranch = (String) parentView.getItemAtPosition(position);
                    trip_vehicle = selectedBranch;

                    try {
                        getBHSD(selectedBranch);

                        mil = Double.parseDouble(mileage_array.get(position));
                        Log.e("mileage_array: ", "mile:-  " + mileage_array.get(position));
                        Log.e("mil: ", "mileSS:-  " + mil);

                    } catch (Exception e) {
                        Log.e("mileage_array: ", "mile:-  " + e);
                    }
                    if (trip_vehicle != null) {

                        vehicleDriverArray.remove(0);
                        vehicleDriverArray.addAll(demoVehicleDriverArray);

                        driver = true;
                        ArrayAdapter<String> adapterDriver = new ArrayAdapter<>(CreatTrip.this,
                                R.layout.simple_spinner_item,
                                vehicleDriverArray);
                        adapterDriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerDriver.setAdapter(adapterDriver);
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        spinnerDriver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView,
                                       int position,
                                       long id) {
                trip_driver = parentView.getItemAtPosition(position).toString();

                if (!trip_driver.equals("Select Driver")) {
                    getDriver();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        tv_agent.setOnClickListener(v -> {
            showSearchableDialog();
        });


//        spinnerAgent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView,
//                                       View selectedItemView,
//                                       int position,
//                                       long id) {
//
//                trip_agency = adapterAgent.getItem(position);
//                if (position > 0) {
//
//                    trip_agency = adapterAgent.getItem(position);
//
//                    try {
//                        String name = kmArray.get(position - 1);
//                        km = (int) Double.parseDouble(name);
//                        Log.e("position", "pos:-   " + position);
//
//                        String kms = " " + name + " Km";
//                        tv_kilo_m.setText(kms);
//                        fhsd = (int) (km / mil);
//                        reloadFHSD();
//                    } catch (Exception e) {
//                        Log.e("mileage_array: ", "mile:-  " + e);
//                    }
//
//                }
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//
//            }
//        });

        spinnerSupplier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView,
                                       int position,
                                       long id) {
                trip_supplier = parentView.getItemAtPosition(position).toString();

                if (position > 0) {
                    trip_supplier = parentView.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        findViewById(R.id.ic_creat_trip).setOnClickListener(v -> {

            Log.e("trip_bhsd", "trip_bhsd:-  " + trip_bhsd);
            if (tvDate.getText().toString().isEmpty()) {
                strDate = null;
            } else if (tvDate.getText().toString().contains("Select Date")) {
                strDate = null;
            }

            if (edt_advance.getText().toString().isEmpty()) {
                trip_advance = "0";
            } else {
                trip_advance = edt_advance.getText().toString();
            }


            if (trip_supplier.equals("Select Supplier")) {
                trip_supplier = "";
            }


            if (edt_shsd.getText().toString().isEmpty()) {
                trip_SHSD = "0";
            } else {
                trip_SHSD = edt_shsd.getText().toString();
            }

            try {
                trip_remarks = edt_remark.getText().toString();
            } catch (Exception e) {
                Log.e("trip_remarks", "trip_remarks:-  " + e);
            }

            if (strDate == null) {
                Toast.makeText(CreatTrip.this, "Please select a date", Toast.LENGTH_SHORT).show();
            } else if (trip_vehicle.equals("Select Vehicle")) {
                Toast.makeText(CreatTrip.this, "Please Select Vehicle", Toast.LENGTH_SHORT).show();
            } else if (trip_driver.equals("Select Driver")) {
                Toast.makeText(CreatTrip.this, "Please Select Driver", Toast.LENGTH_SHORT).show();
            } else if (trip_agency.equals("Select Agent")) {
                Toast.makeText(CreatTrip.this, "Please Select Agent", Toast.LENGTH_SHORT).show();
            } else {
                getUpdatedData();
            }

        });

        getBranch();
        getAgent();
        getVendor();

        findViewById(R.id.rlSelectDate).setOnClickListener(v -> datePick());
        tvDate = findViewById(R.id.tvDate);
    }

    TextView tvDate;
    int year,month,dayOfMonth;

    void datePick() {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        Calendar selectedDate = Calendar.getInstance();
        // Create DatePickerDialog with current date as default
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                R.style.DatePickerDialogTheme, (view,
                                                selectedYear,
                                                selectedMonth,
                                                selectedDay) -> {
            // Validate selected date
            selectedDate.set(selectedYear, selectedMonth, selectedDay);

            Calendar minDate = Calendar.getInstance();
            minDate.add(Calendar.DAY_OF_MONTH, -3); // Minimum date (current date - 4 days)

            if (selectedDate.before(minDate) || selectedDate.after(Calendar.getInstance())) {
                // Invalid date selected
                Toast.makeText(CreatTrip.this, "Please select a date within the last 4 days or today.", Toast.LENGTH_SHORT).show();
            } else {
                String date_ = selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear;
                tvDate.setText(date_);

                strDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
            }
        }, year, month, dayOfMonth);

        // Set maximum date to today
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // Set minimum date to 4 days ago
        calendar.add(Calendar.DAY_OF_MONTH, -3);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.setTitle("Date");
        // Show DatePickerDialog
        datePickerDialog.show();
    }

    private void showSearchableDialog() {
        Dialog dialog = new Dialog(CreatTrip.this);
        dialog.setContentView(R.layout.dialog_searchable);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        SearchView searchView = dialog.findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);

        ListView listView = dialog.findViewById(R.id.listView);
        ImageView ivCloseSearchAgentDialog = dialog.findViewById(R.id.ivCloseSearchAgentDialog);

        ivCloseSearchAgentDialog.setOnClickListener(v -> dialog.dismiss());

        adapterAgent = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, agentArray);
        listView.setAdapter(adapterAgent);

        // Search filter
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapterAgent.getFilter().filter(newText);
                return false;
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selected = adapterAgent.getItem(position);
            trip_agency = selected;
            tv_agent.setText(selected);
            dialog.dismiss();
        });

        dialog.show();
    }

    List<String> vehicleArray = new ArrayList<>();
    List<String> vehicleDriverArray = new ArrayList<>();
    List<String> demoVehicleDriverArray = new ArrayList<>();
    List<String> agentArray = new ArrayList<>();
    List<String> vendorArray = new ArrayList<>();
    List<String> kmArray = new ArrayList<>();

    public void reloadFHSD() {
        String fhsd_ = " " + fhsd + " Ltr";
        tv_fhsd.setText(fhsd_);
    }

    public void getBranch() {
        dialog.show();
        vehicleArray.clear();
        mileage_array.clear();

        vehicleArray.add("Select Vehicle");
        mileage_array.add("0");
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder().header("Authorization", "Bearer " + sp.getString("token", "")).method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        Retrofit retrofit = new Retrofit.Builder().baseUrl(getString(R.string.common_url)).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();
        Api loginService = retrofit.create(Api.class);

        Call<FetchVehicleDataModel> call = loginService.fetchVehicleList(sp.getString("userBranch", ""));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<FetchVehicleDataModel> call,
                                   @NonNull Response<FetchVehicleDataModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    ArrayList<FetchVehicleDataModel> branches = response.body().getData();

                    for (FetchVehicleDataModel branch : branches) {
                        vehicleArray.add(branch.getReg_no());
                        demoVehicleDriverArray.add(branch.getVehicle_driver());
                        mileage_array.add(branch.getVehicle_mileage());

                        Log.e("vehicleArray", "vehicleArray: " + vehicleArray);
                        Log.e("vehicleDriverArray", "vehicleDriverArray: " + branch.getVehicle_driver());
                        Log.e("mileageArray", "mileageArray: " + mileage_array);

                    }


                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CreatTrip.this,
                            R.layout.simple_spinner_item,
                            vehicleArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerBranches.setAdapter(adapter);

                    Log.e("response..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(CreatTrip.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<FetchVehicleDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("FetchVehicleDataModel", "" + t);
                dialog.dismiss();
            }
        });
    }

    ArrayList<FetchDriverDataModel> branchesDriver = new ArrayList<>();

    public void getDriver() {
        dialog.show();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();


        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder().header("Authorization", "Bearer " + sp.getString("token", "")).method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        Retrofit retrofit = new Retrofit.Builder().baseUrl(getString(R.string.common_url)).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();
        Api loginService = retrofit.create(Api.class);

        Call<FetchDriverDataModel> call = loginService.fetchDriver(sp.getString("userBranch", ""));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<FetchDriverDataModel> call,
                                   @NonNull Response<FetchDriverDataModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    branchesDriver = response.body().getData();
                    vehicleDriverArray.clear();

                    for (FetchDriverDataModel branch : branchesDriver) {
                        vehicleDriverArray.add(branch.getFull_name());
                    }
                    Log.e("response..", "branches:-  " + branchesDriver.size());

                } else {
                    Toast.makeText(CreatTrip.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<FetchDriverDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("FetchDriverDataModel", "" + t);
                dialog.dismiss();
            }
        });
    }

    ArrayAdapter<String> adapterAgent = null;
    public void getAgent() {
        dialog.show();
        agentArray.clear();
        kmArray.clear();
        agentArray.add("Select Agent");
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder().header("Authorization", "Bearer " + sp.getString("token", "")).method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        Retrofit retrofit = new Retrofit.Builder().baseUrl(getString(R.string.common_url)).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();

        Api loginService = retrofit.create(Api.class);

        Call<FetchAgencyDataModel> call = loginService.fetchAgency(sp.getString("userBranch", ""));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<FetchAgencyDataModel> call,
                                   @NonNull Response<FetchAgencyDataModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    ArrayList<FetchAgencyDataModel> branches = response.body().getData();

                    for (FetchAgencyDataModel branch : branches) {
                        agentArray.add(branch.getAgency_name());
                        kmArray.add(branch.getAgency_rt_km());
                    }

                    adapterAgent = new ArrayAdapter<>(CreatTrip.this, R.layout.simple_spinner_item, agentArray);
//                    adapterAgent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinnerAgent.setAdapter(adapterAgent);

                    Log.e("response..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(CreatTrip.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<FetchAgencyDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("FetchAgencyDataModel", "" + t);
                dialog.dismiss();
            }
        });
    }

    public void getVendor() {
        dialog.show();
        vendorArray.clear();
        vendorArray.add("Select Supplier");
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder().header("Authorization", "Bearer " + sp.getString("token", "")).method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        Retrofit retrofit = new Retrofit.Builder().baseUrl(getString(R.string.common_url)).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();
        Api loginService = retrofit.create(Api.class);

        Call<FetchVendorDataModel> call = loginService.fetchAgent(sp.getString("userBranch", ""), "Diesel");
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<FetchVendorDataModel> call,
                                   @NonNull Response<FetchVendorDataModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    ArrayList<FetchVendorDataModel> branches = response.body().getData();

                    for (FetchVendorDataModel branch : branches) {
                        vendorArray.add(branch.getVendor_name());
                    }

                    ArrayAdapter<String> adapterDriver = new ArrayAdapter<>(CreatTrip.this,
                            R.layout.simple_spinner_item,
                            vendorArray);
                    adapterDriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSupplier.setAdapter(adapterDriver);

                    Log.e("response..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(CreatTrip.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<FetchVendorDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("FetchVendorDataModel: ", "" + t);
                dialog.dismiss();
            }
        });
    }

    String trip_hsd, trip_hsd_supplied;

    public void getBHSD(String vehicleNumber) {
        dialog.show();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder().header("Authorization", "Bearer " +
                    sp.getString("token", "")).method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        Retrofit retrofit = new Retrofit.Builder().baseUrl(getString(R.string.common_url)).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();
        Api loginService = retrofit.create(Api.class);

        Call<FetchBHSDDataModel> call = loginService.fetchVehicleBHSD(vehicleNumber);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<FetchBHSDDataModel> call,
                                   @NonNull Response<FetchBHSDDataModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    ArrayList<FetchBHSDDataModel> branches = response.body().getData();

                    for (FetchBHSDDataModel branch : branches) {
                        String bhsd_ = " " + branch.getTrip_hsd() + " Ltr";
                        tv_bhsd.setText(bhsd_);
                        trip_bhsd = branch.getTrip_hsd();
                        trip_hsd = branch.getTrip_hsd();
                        trip_hsd_supplied = branch.getTrip_hsd_supplied();

                    }
                    Log.e("response..", "branches:-  " + branches.size());
                } else {
                    Toast.makeText(CreatTrip.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<FetchBHSDDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("FetchBHSDDataModel: ", "" + t);
                dialog.dismiss();
            }
        });
    }


    public void getUpdatedData() {
        dialog.show();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder().header("Authorization", "Bearer " + sp.getString("token", "")).method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        Retrofit retrofit = new Retrofit.Builder().baseUrl(getString(R.string.common_url)).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();
        Api loginService = retrofit.create(Api.class);

        Log.e("rahul..", "strDate:-  " + strDate);

        Call<CreatTripModel> call = loginService.createTrip("2023-24",
                sp.getString("userBranch", ""),
                strDate,
                trip_vehicle,
                trip_driver,
                trip_agency,
                String.valueOf(fhsd),
                trip_SHSD,
                trip_advance,
                trip_supplier,
                trip_remarks,
                trip_bhsd);


        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<CreatTripModel> call,
                                   @NonNull Response<CreatTripModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    Toast.makeText(CreatTrip.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                    strDate = null;
                    finish();

                } else {
                    Toast.makeText(CreatTrip.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<CreatTripModel> call,
                                  @NonNull Throwable t) {
                Log.e("CreatTripModel: ", " " + t);
                dialog.dismiss();
            }
        });
    }

}