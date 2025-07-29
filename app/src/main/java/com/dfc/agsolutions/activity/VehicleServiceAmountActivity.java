package com.dfc.agsolutions.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dfc.agsolutions.model.RequestBodyData;
import com.dfc.agsolutions.model.ServiceStatusDataModel;
import com.dfc.agsolutions.model.ServiceSubData;
import com.dfc.agsolutions.model.ServiceTypeDataModel;
import com.dfc.agsolutions.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VehicleServiceAmountActivity extends AppCompatActivity {

    ImageView back;

    String Date, Vehicle, Garage, TotalAmount, Km, Description;

//    EditText tamount;

    SharedPreferences sp;
    SharedPreferences.Editor ed;

    TextView totalamount, amount, tamount;

    int ta;

    FloatingActionButton floating_action_button;
    ImageView save;

    RecyclerView spin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_service_amount);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();


        Date = getIntent().getStringExtra("date");
        Vehicle = getIntent().getStringExtra("vehicle");
        Garage = getIntent().getStringExtra("garage");
        TotalAmount = getIntent().getStringExtra("amount");
        Km = getIntent().getStringExtra("km");
        Description = getIntent().getStringExtra("description");



        back = findViewById(R.id.back);
        spin = findViewById(R.id.spin);
        floating_action_button = findViewById(R.id.floating_action_button);
        amount = findViewById(R.id.amount);
        tamount = findViewById(R.id.et_total_amount);

        int color = getResources().getColor(R.color.white);// Replace with your color code
        floating_action_button.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);

//        totalamount = findViewById(R.id.totalamount);
//        tamount = findViewById(R.id.tamount);
        save = findViewById(R.id.save);

        back.setOnClickListener(v -> finish());

        tamount.setText(TotalAmount);


        Log.e("TotalAmount", "TotalAmount=============: " + TotalAmount);


        get_Service_type();

        serviceType.add("Service Type");

    }

    List<String> serviceType = new ArrayList<>();
    ArrayAdapter<String> adapterdriver;
    public void get_Service_type() {


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
        Call<ServiceTypeDataModel> call = loginservice.get_getServiceType();
        call.enqueue(new Callback<ServiceTypeDataModel>() {
            @Override
            public void onResponse(Call<ServiceTypeDataModel> call, Response<ServiceTypeDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {
//
                    ArrayList<ServiceTypeDataModel> branches = response.body().getData();

                    for (ServiceTypeDataModel branch : branches) {
                        serviceType.add(branch.getService_types());
                        Log.e("getVoucher_type", "getVoucher_type================: " + branch.getService_types());
//                        s = branch.getVoucher_type();
                    }
//                    get_Debit(String.valueOf();
//
                    adapterdriver = new ArrayAdapter<>(VehicleServiceAmountActivity.this, R.layout.simple_spinner_item1, serviceType);
                    adapterdriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    Home_Today_list_Adapter adapter = new Home_Today_list_Adapter(VehicleServiceAmountActivity.this, response.body().getData());
                    spin.setAdapter(adapter);
////
//                    ArrayAdapter<String> adapter1 = new ArrayAdapter<>(PaymentActivity.this, R.layout.simple_spinner_item1, vhicalarray);
//                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinner1.setAdapter(adapter1);

//
//                    ArrayAdapter<String> adapterdriver = new ArrayAdapter<>(CreatTrip.this, R.layout.simple_spinner_item, vhicaldraiverarray);
//                    adapterdriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinnerdriver.setAdapter(adapterdriver);

//                    setupSpinner(branchNames);
                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(VehicleServiceAmountActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
//                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<ServiceTypeDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
//                dialog.dismiss();
            }
        });
    }

    int enteredValue = 0;

    public void Creat_Service_Status() {

//        dialog.show();
//        fullname.clear();
//        mobile.clear();
//        dl_expiry.clear();
//        user_status.clear();
//        user_image.clear();
//        milageaaray.clear();

//        Date = getIntent().getStringExtra("date");
//        Vehicle = getIntent().getStringExtra("vehicle");
//        Garage = getIntent().getStringExtra("garage");
//        TotalAmount = getIntent().getStringExtra("amount");
//        Km = getIntent().getStringExtra("km");
//        Description = getIntent().getStringExtra("description");

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

String size = String.valueOf(serviceSubDataList.size());
        RequestBodyData requestBody = new RequestBodyData(
                Date,
                "2023-24",
                Vehicle,
                Garage,
                Km,
                TotalAmount,
                size,
                Description,
                serviceSubDataList
        );
        Gson gson = new Gson();
        String requestBodyJson = gson.toJson(requestBody);

// Log the request body
        Log.e("RequestLog", "Request Body: " + requestBodyJson);




        Call<ServiceStatusDataModel> call = loginservice.get_getServiceStatus(requestBody);
//
//        Call<ServiceStatusDataModel> call = loginservice.get_getServiceStatus(Date,
//                "2023-24",
//                Vehicle,
//                Garage,
//                Km,
//                TotalAmount,
//                "2",
//                Description,
//                new Map[]{serviceSubDataList});

        call.enqueue(new Callback<ServiceStatusDataModel>() {
            @Override
            public void onResponse(Call<ServiceStatusDataModel> call, Response<ServiceStatusDataModel> response) {
                Log.e("responce..", "" + response.toString());

                Log.e("selectedItems..", "selectedItems---------------------------" + selectedItems);
                Log.e("selectedamount..", "selectedamount---------------------------" + selectedamount);

                if (response.body().getCode().equalsIgnoreCase("200")) {
//
//                    ArrayList<DriverListDataModel> branches = response.body().getData();
////                    for (DriverListDataModel branch : branches) {
////
                    Toast.makeText(VehicleServiceAmountActivity.this, "" + response.body().getMsg(), Toast.LENGTH_SHORT).show();
////                        fullname.add(branch.getFull_name());
////                        mobile.add(branch.getMobile());
////                        dl_expiry.add(branch.getDl_expiry());
////                        user_status.add(branch.getUser_status());
////                        user_image.add(branch.getUser_image());
////
////                    }

                    startActivity(new Intent(VehicleServiceAmountActivity.this, HomeActivity.class));

//                    DriverListActivity.Home_Today_list_Adapter adapter = new DriverListActivity.Home_Today_list_Adapter(DriverListActivity.this,response.body().getData());
//                    driverlist.setAdapter(adapter);
//
////
//////response.body().getData().get(0).getReg_no();
//////                    for (ServiceFatchVhicalDataModel branch : branches) {
//////                        vhicalarray.add(branch.getReg_no());
////////                        vhicaldraiverarray.add(branch.getVehicle_driver());
////////                        milageaaray.add(branch.getVehicle_mileage());
//////                    }
////                    for (ServiceFatchVhicalDataModel branch : branches) {
////                        vhicalarray.add(branch.getReg_no());
////                    }
//////
////                    ArrayAdapter<String> adapter = new ArrayAdapter<>(DriverListActivity.this, R.layout.simple_spinner_item, vhicalarray);
////                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
////                    spinner.setAdapter(adapter);
//
////
////                    ArrayAdapter<String> adapterdriver = new ArrayAdapter<>(CreatTrip.this, R.layout.simple_spinner_item, vhicaldraiverarray);
////                    adapterdriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
////                    spinnerdriver.setAdapter(adapterdriver);
//
//
////                    setupSpinner(branchNames);
//                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(VehicleServiceAmountActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
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
//    ServiceData serviceData;
    public Map<Integer, String> selectedItems = new HashMap<>();
    public ArrayList<String> selectedamount = new ArrayList<>();

//    Map<String, String> serviceSubDataList = new HashMap<>();
    ArrayList<ServiceSubData> serviceSubDataList = new ArrayList<>();
    String selectedItem;

    public String serviceSubType;

    public String serviceSubAmount;

    public class Home_Today_list_Adapter extends RecyclerView.Adapter<Home_Today_list_Adapter.Holder> {
        VehicleServiceAmountActivity context;
        ArrayList<ServiceTypeDataModel> data;
//        Map<Integer, String> selectedItems;
//        ArrayList<String> selectedamount;

        //        public Home_Today_list_Adapter(VehicleServiceAmountActivity context, ArrayList<ServiceTypeDataModel> data, Map<Integer, String> selectedItems, ArrayList<String> selectedamount) {
//
//            this.selectedItems = new HashMap<>();
//            this.selectedamount = new ArrayList<>();
//        }
//
        private int itemCount = 1;
//
//        public Home_Today_list_Adapter() {
//            VehicleServiceAmountActivity context;
//            ArrayList<ServiceTypeDataModel> data;
//            Map<Integer, String> selectedItems;
//            ArrayList<String> selectedamount;
//        }


        public Home_Today_list_Adapter(VehicleServiceAmountActivity context, ArrayList<ServiceTypeDataModel> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getItemCount() {
            return getItemCountInternal();
        }

        public void incrementItemCount() {
            itemCount++;
//            notifyDataSetChanged();
        }

        private int getItemCountInternal() {
            return itemCount;
        }

        int count = 0;

        @NonNull
        @Override
        public Home_Today_list_Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_srvice, parent, false);
            return new Home_Today_list_Adapter.Holder(view);
        }

        //        int abcd = 0;
//        int a;
        int b;


        @Override
        public void onBindViewHolder(@NonNull final Home_Today_list_Adapter.Holder holder, @SuppressLint("RecyclerView") final int position) {

//            ArrayAdapter<String> adapterdriver = new ArrayAdapter<>(context, R.layout.simple_spinner_item1, serviceType);
//            adapterdriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinner.setAdapter(adapterdriver);

            holder.plush.setOnClickListener(v -> {
                incrementItemCount();
                enteredValue += (holder.getAmountValue());
                amount.setText(enteredValue);
                Log.e("enteredValue", "enteredValue: " + enteredValue);
                Log.e("getAmountValue", "getAmountValue=-===------------------: " + holder.getAmountValue());
            });

            floating_action_button.setOnClickListener(v -> {

                if(holder.getAmountValue() == 0 || adapterdriver.getPosition(selectedItems.get(position))==0)
                {
                    if(holder.getAmountValue() == 0)
                    {
                        Toast.makeText(context, "Enter Amount", Toast.LENGTH_SHORT).show();
                    }else if(adapterdriver.getPosition(selectedItems.get(position))==0)
                    {
                        Toast.makeText(context, "Select Type", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "Fill Select Type And Amount", Toast.LENGTH_SHORT).show();
                    }
                }else {


                    enteredValue += (holder.getAmountValue());

                    amount.setText(String.valueOf(enteredValue));
                    Log.e("enteredValue", "enteredValue: " + enteredValue);
                    if (selectedItems.containsKey(position)) {
                        int selectedItemPosition = adapterdriver.getPosition(selectedItems.get(position));
                        holder.spinner.setSelection(selectedItemPosition);

//                        ServiceSubData engineOilChange = new ServiceSubData();
//                        serviceSubDataList.put(selectedItems.get(position), String.valueOf(holder.getAmountValue()));
//                        serviceData = new ServiceData(Date, Vehicle, Garage, TotalAmount, Km, Description, (List<Map<String, String>>) serviceSubDataList);
//                        Log.e("engineOilChange", "engineOilChange--------------------------------: "+ engineOilChange);

                        serviceType = Collections.singletonList(selectedItem);


                        serviceSubAmount = String.valueOf(holder.getAmountValue());

                        serviceSubDataList.add(new ServiceSubData(serviceSubType, serviceSubAmount));

//                        serviceSubDataList.put(serviceSubType,serviceSubAmount);

                        Log.e("selectedItems", "selectedItems: " + selectedItems);
                    }
                    selectedamount.add(String.valueOf(holder.getAmountValue()));
                    Log.e("selectedamount", "selectedamount: " + selectedamount);

                    if (amount != null) {
                        String ta = tamount.getText().toString();
                        String am = amount.getText().toString();
                        if (Integer.parseInt(ta) == Integer.parseInt(am)) {
                            Toast.makeText(context, "Match Your Amount", Toast.LENGTH_SHORT).show();
                            save.setVisibility(View.VISIBLE);
                            floating_action_button.setVisibility(View.GONE);
                            b = 1;
                        } else {
                            incrementItemCount();
                        }
                    }



                }
            });

            save.setOnClickListener(v -> {
                if (b == 1) {
                    String ta = tamount.getText().toString();
                    String am = amount.getText().toString();
                    if (Integer.parseInt(ta) <= Integer.parseInt(am)) {
                        Creat_Service_Status();
//                    startActivity(new Intent(context,HomeActivity.class));
                    } else {
                        Toast.makeText(context, "Amount Not Match", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    floating_action_button.setVisibility(View.GONE);
                    enteredValue += (holder.getAmountValue());
                    amount.setText(String.valueOf(enteredValue));
                    if (selectedItems.containsKey(position)) {
                        int selectedItemPosition = adapterdriver.getPosition(selectedItems.get(position));
                        holder.spinner.setSelection(selectedItemPosition);

                        Log.e("selectedItems", "selectedItems: " + selectedItems);
                    }
                    selectedamount.add(String.valueOf(holder.getAmountValue()));
                    Log.e("selectedamount", "selectedamount: " + selectedamount);
                }
            });

            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int selectedPosition, long id) {
                    selectedItem = (String) parentView.getItemAtPosition(selectedPosition);
                    selectedItems.put(position, selectedItem);

                    serviceSubType = selectedItem;

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {

                }
            });

//            holder.amount.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//
//                }
//            });

        }

        class Holder extends RecyclerView.ViewHolder {

            ImageView plush;
            EditText amount;
            Spinner spinner;

            public Holder(@NonNull View itemView) {
                super(itemView);

                amount = itemView.findViewById(R.id.amount);
                spinner = itemView.findViewById(R.id.spinner);
                plush = itemView.findViewById(R.id.plush);

            }
            public int getAmountValue() {
                String amountText = amount.getText().toString();
                try {
                    return Integer.parseInt(amountText);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        }

    }
}