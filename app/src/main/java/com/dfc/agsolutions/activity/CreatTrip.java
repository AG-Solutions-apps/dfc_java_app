package com.dfc.agsolutions.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.cvaghela.spinner.searchablespinner.SearchableSpinner;
//import com.cvaghela.spinner.searchablespinner.interfaces.OnItemSelectedListener;
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

public class CreatTrip extends AppCompatActivity {
    TextView brandname;
    TextView bhsd;
    TextView kilom;
    TextView fhsd;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    private Spinner spinnerBranches;
    private Spinner spinnerdriver;
//    private Spinner spinneragetnt;
    Spinner spinneragetnt;
//    SearchView searchView;
    private Spinner spinnersupplayer;
    ProgressDialog dialog;

    int km = 0;
    int fhsds = 0;
    Double mil = 0.0;
    String currentdDate;
    String trip_vehicle = null;
    String trip_driver = null;
    String trip_agency = "Select Agent";

    EditText edtadvance, edtshsd, edtremark;
    String trip_advance;
    String trip_SHSD, strDate = null;
    String trip_supplier;
    String trip_remarks;
    String trip_bhsd;

    List<String> branchList = new ArrayList<>();
    List<String> milageaaray = new ArrayList<>();
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

//        milageaaray.add("Select Vehicle");

        Date currentDate = new Date();

        // Define the desired date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Format the date
        currentdDate = dateFormat.format(currentDate);

        brandname = findViewById(R.id.tv_login_account_txt);
        bhsd = findViewById(R.id.bhsd);
        kilom = findViewById(R.id.tv_kilo_m);
        fhsd = findViewById(R.id.fhsd);
        edtadvance = findViewById(R.id.et_advance);
        edtshsd = findViewById(R.id.et_shsd);
        edtremark = findViewById(R.id.et_remark);
        relative = findViewById(R.id.relative);
        brandname.setText("" + sp.getString("userBranch", ""));

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        spinnerBranches = findViewById(R.id.spinnerBranches);
        spinnerdriver = findViewById(R.id.spinner_driver);
        spinneragetnt = findViewById(R.id.spinner_agent);
//        searchView = findViewById(R.id.searchView);
//          searchableSpinner = findViewById(R.id.searchableSpinner);
//        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
//        searchEditText.setHintTextColor(Color.WHITE);
//        searchEditText.setTextColor(Color.WHITE);


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

        ArrayAdapter<String> adapterdriver = new ArrayAdapter<>(CreatTrip.this, R.layout.simple_spinner_item, vhicaldraiverarray);
        adapterdriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerdriver.setAdapter(adapterdriver);


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
                        ArrayAdapter<String> adapterdriver = new ArrayAdapter<>(CreatTrip.this, R.layout.simple_spinner_item, vhicaldraiverarray);
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
                String selectedBranch1 =  adapterAgent.getItem(position);
//                String selectedBranch1 = (String) parentView.getItemAtPosition(position);
                trip_agency = selectedBranch1;
                if (position > 0) {
                    String selectedBranch =  adapterAgent.getItem(position);
//                    String selectedBranch = (String) parentView.getItemAtPosition(position);
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

            }
        });
//        spinneragetnt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // Do nothing here
//            }
//        });

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
        findViewById(R.id.ic_creat_trip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("trip_bhsd", "trip_bhsd:-  " + trip_bhsd);
                if (tvDate.getText().toString().length() == 0) {
                    strDate = null;
                }else if (tvDate.getText().toString().contains("Select Date")) {
                    strDate = null;
                } else {
//                    strDate = tvDate.getText().toString().replaceAll("/","");
                }


                if (edtadvance.getText().toString().length() == 0) {
                    trip_advance = "0";
                } else {
                    trip_advance = edtadvance.getText().toString();
                }


                if (trip_supplier.equals("Select Supplier")) {
                    trip_supplier = "";

                } else {

//                    trip_advance = edtadvance.getText().toString();
                }


                if (edtshsd.getText().toString().length() == 0) {
                    trip_SHSD = "0";
                } else {
                    trip_SHSD = edtshsd.getText().toString();
                }


                try {
                    trip_remarks = edtremark.getText().toString();
                } catch (Exception e) {
                }

                if (strDate == null) {
//                    Toast.makeText(CreatTrip.this, "Please Select Vehicle", Toast.LENGTH_SHORT).show();
                    Toast.makeText(CreatTrip.this, "Please select a date", Toast.LENGTH_SHORT).show();

                } else if (trip_vehicle.equals("Select Vehicle")) {
                    Toast.makeText(CreatTrip.this, "Please Select Vehicle", Toast.LENGTH_SHORT).show();
                } else if (trip_driver.equals("Select Driver")) {
                    Toast.makeText(CreatTrip.this, "Please Select Driver", Toast.LENGTH_SHORT).show();
                } else if (trip_agency.equals("Select Agent")) {
                    Toast.makeText(CreatTrip.this, "Please Select Agent", Toast.LENGTH_SHORT).show();
                } else {
                    get_updatedata();
                }

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
        get_Aggetnt();
        get_vendor();

        findViewById(R.id.rlSelectDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datepick();
            }
        });
        tvDate = findViewById(R.id.tvDate);

    }

    TextView tvDate;
    int year,month,dayOfMonth;

    void datepick() {
        // Get current date
            Calendar calendar = Calendar.getInstance();
         year = calendar.get(Calendar.YEAR);
         month = calendar.get(Calendar.MONTH);
         dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        Calendar selectedDate = Calendar.getInstance();
        // Create DatePickerDialog with current date as default
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DatePickerDialogTheme, (view, selectedYear, selectedMonth, selectedDay) -> {
            // Validate selected date

            selectedDate.set(selectedYear, selectedMonth, selectedDay);

            Calendar minDate = Calendar.getInstance();
            minDate.add(Calendar.DAY_OF_MONTH, -3); // Minimum date (current date - 4 days)

            if (selectedDate.before(minDate) || selectedDate.after(Calendar.getInstance())) {
                // Invalid date selected
                Toast.makeText(CreatTrip.this, "Please select a date within the last 4 days or today.", Toast.LENGTH_SHORT).show();
            } else {
                tvDate.setText((selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear).toString());
                strDate = (selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay).toString();
                // Valid date selected
                // Do something with the selected date
//                Toast.makeText(CreatTrip.this, "Selected Date: " + selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear, Toast.LENGTH_SHORT).show();
            }
        }, year, month, dayOfMonth);

        // Set maximum date to today
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // Set minimum date to 4 days ago
        calendar.add(Calendar.DAY_OF_MONTH, -3);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.setTitle("Date");
//        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Done", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                datePickerDialog.dismiss();
////                tvDate.setText(calendar.getTime().toString());
//
//            }
//        });
//        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                datePickerDialog.dismiss();
//            }
//        });
        // Show DatePickerDialog
        datePickerDialog.show();
    }


/*
    public class CustomAdapter extends ArrayAdapter<String> implements Filterable {

        private List<String> dataList;
        private List<String> filteredList;

        public CustomAdapter(Context context, List<String> dataList) {
            super(context, android.R.layout.simple_spinner_item, dataList);
            this.dataList = dataList;
            this.filteredList = new ArrayList<>(dataList);
        }

        @Override
        public int getCount() {
            return filteredList.size();
        }

        @Override
        public String getItem(int position) {
            return filteredList.get(position);
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    List<String> results = new ArrayList<>();

                    if (constraint == null || constraint.length() == 0) {
                        results.addAll(dataList);
                    } else {
                        String filterPattern = constraint.toString().toLowerCase().trim();
                        for (String item : dataList) {
                            if (item.toLowerCase().contains(filterPattern)) {
                                results.add(item);
                            }
                        }
                    }

                    filterResults.values = results;
                    filterResults.count = results.size();
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredList.clear();
                    if (results != null && results.count > 0) {
                        filteredList.addAll((List<String>) results.values);
                        notifyDataSetChanged();
                    }
                }
            };
        }
    }
*/

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
        dialog.show();
        vhicalarray.clear();
        milageaaray.clear();
//        vhicaldraiverarray.clear();
//        branchList.clear();
        vhicalarray.add("Select Vehicle");
        milageaaray.add("0");
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

//        if (token != null) {
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder().header("Authorization", "Bearer " + sp.getString("token", "")).method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
//        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(getString(R.string.commn_url)).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();
        Api loginservice = retrofit.create(Api.class);
        Call<FetchVehicleDataModel> call = loginservice.get_fatchvhiclelist(sp.getString("userBranch", ""));
        call.enqueue(new Callback<FetchVehicleDataModel>() {
            @Override
            public void onResponse(Call<FetchVehicleDataModel> call, Response<FetchVehicleDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {

                    ArrayList<FetchVehicleDataModel> branches = response.body().getData();

                    for (FetchVehicleDataModel branch : branches) {
                        vhicalarray.add(branch.getReg_no());
                        demovhicaldraiverarray.add(branch.getVehicle_driver());
                        milageaaray.add(branch.getVehicle_mileage());

                        Log.e("vhicalarrayv", "vhicalarray: " + vhicalarray);
                        Log.e("vhicaldraiverarray", "vhicaldraiverarray: " + branch.getVehicle_driver());
                        Log.e("milageaaray", "milageaaray: " + milageaaray);

                    }


                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CreatTrip.this, R.layout.simple_spinner_item, vhicalarray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerBranches.setAdapter(adapter);


//                    setupSpinner(branchNames);
                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(CreatTrip.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<FetchVehicleDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
    }

    ArrayList<FetchDriverDataModel> branchesdriver = new ArrayList<>();

    public void get_driver() {
        dialog.show();
//        vhicaldraiverarray.clear();
//        vhicaldraiverarray.add("Select Driver");
//        vhicaldraiverarray.add("Select Driver");
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

//        if (token != null) {
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder().header("Authorization", "Bearer " + sp.getString("token", "")).method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
//        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(getString(R.string.commn_url)).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();
        Api loginservice = retrofit.create(Api.class);
        Call<FetchDriverDataModel> call = loginservice.get_fatchdriver(sp.getString("userBranch", ""));
        call.enqueue(new Callback<FetchDriverDataModel>() {
            @Override
            public void onResponse(Call<FetchDriverDataModel> call, Response<FetchDriverDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {

                    branchesdriver = response.body().getData();
                    vhicaldraiverarray.clear();

                    for (FetchDriverDataModel branch : branchesdriver) {
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
                    Toast.makeText(CreatTrip.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<FetchDriverDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
    }

    ArrayAdapter<String> adapterAgent = null;
    public void get_Aggetnt() {
        dialog.show();
        aggentrarray.clear();
        kmarray.clear();
        aggentrarray.add("Select Agent");
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

//        if (token != null) {
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder().header("Authorization", "Bearer " + sp.getString("token", "")).method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
//        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(getString(R.string.commn_url)).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();
        Api loginservice = retrofit.create(Api.class);
        Call<FetchAgencyDataModel> call = loginservice.get_fetch_agency(sp.getString("userBranch", ""));
        call.enqueue(new Callback<FetchAgencyDataModel>() {
            @Override
            public void onResponse(Call<FetchAgencyDataModel> call, Response<FetchAgencyDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {

                    ArrayList<FetchAgencyDataModel> branches = response.body().getData();

                    for (FetchAgencyDataModel branch : branches) {
                        aggentrarray.add(branch.getAgency_name());
                        kmarray.add(branch.getAgency_rt_km());
                    }
//
//                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CreatTrip.this, R.layout.simple_spinner_item, vhicalarray);
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinnerBranches.setAdapter(adapter);


                    adapterAgent = new ArrayAdapter<>(CreatTrip.this, R.layout.simple_spinner_item, aggentrarray);
                    adapterAgent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinneragetnt.setAdapter(adapterAgent);
//                    searchableSpinner.setAdapter(adapterdriver);

//                    searchableSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(View view, int position, long id) {
//
//                        }
//
//                        @Override
//                        public void onNothingSelected() {
//
//                        }
//                    });

//                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                        @Override
//                        public boolean onQueryTextSubmit(String query) {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onQueryTextChange(String newText) {
//                            adapterdriver.getFilter().filter(newText);
//                            return false;
//                        }
//                    });

//                    setupSpinner(branchNames);
                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(CreatTrip.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<FetchAgencyDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
    }

    public void get_vendor() {
        dialog.show();
        vendorrarray.clear();
        vendorrarray.add("Select Supplier");
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

//        if (token != null) {
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder().header("Authorization", "Bearer " + sp.getString("token", "")).method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
//        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(getString(R.string.commn_url)).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();
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


                    ArrayAdapter<String> adapterdriver = new ArrayAdapter<>(CreatTrip.this, R.layout.simple_spinner_item, vendorrarray);
                    adapterdriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnersupplayer.setAdapter(adapterdriver);


//                    setupSpinner(branchNames);
                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(CreatTrip.this, "Network Error!!", Toast.LENGTH_SHORT).show();
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
            Request.Builder requestBuilder = original.newBuilder().header("Authorization", "Bearer " + sp.getString("token", "")).method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
//        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(getString(R.string.commn_url)).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();
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
                    Toast.makeText(CreatTrip.this, "Network Error!!", Toast.LENGTH_SHORT).show();
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
            Request.Builder requestBuilder = original.newBuilder().header("Authorization", "Bearer " + sp.getString("token", "")).method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
//        }
        Retrofit retrofit = new Retrofit.Builder().baseUrl(getString(R.string.commn_url)).addConverterFactory(GsonConverterFactory.create()).client(httpClient.build()).build();
        Api loginservice = retrofit.create(Api.class);

//
//        @POST("create-trip")
//        Call<CreatTripModel> get_createtip(@Query("trip_year") String trip_year
//                , @Query("trip_branch") String trip_branch,
//                @Query("trip_date") String trip_date,
//                @Query("trip_vehicle") String trip_vehicle,
//                @Query("trip_driver") String trip_driver,
//                @Query("trip_agency") String trip_agency,
//                @Query("trip_hsd") String trip_hsd,
//                @Query("trip_hsd_supplied") String trip_hsd_supplied,
//                @Query("trip_advance") String trip_advance,
//                @Query("trip_supplier") String trip_supplier,
//                @Query("trip_remarks") String trip_remarks,
//                @Query("trip_bhsd") String trip_bhsd);


        Log.e("rahul..", "strDate:-  " + strDate);

        Call<CreatTripModel> call = loginservice.get_createtip("2023-24",
                sp.getString("userBranch", ""),
                strDate,
                trip_vehicle,
                trip_driver, trip_agency, String.valueOf(fhsds), trip_SHSD, trip_advance, trip_supplier, trip_remarks, trip_bhsd);


        call.enqueue(new Callback<CreatTripModel>() {
            @Override
            public void onResponse(Call<CreatTripModel> call, Response<CreatTripModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {

                    Toast.makeText(CreatTrip.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                    strDate = null;
                    finish();

//                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(CreatTrip.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<CreatTripModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
    }
}