package com.dfc.agsolutions.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

public class VehicleServiceAmountActivity extends
        AppCompatActivity {

    ImageView back;

    String date, vehicle, garage, totalAmountText, km_, description_;

    SharedPreferences sp;
    SharedPreferences.Editor ed;

    TextView amount, totalAmount;

    FloatingActionButton floating_action_button;
    ImageView save;

    RecyclerView spin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_service_amount);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();

        date = getIntent().getStringExtra("date");
        vehicle = getIntent().getStringExtra("vehicle");
        garage = getIntent().getStringExtra("garage");
        totalAmountText = getIntent().getStringExtra("amount");
        km_ = getIntent().getStringExtra("km");
        description_ = getIntent().getStringExtra("description");

        back = findViewById(R.id.back);
        spin = findViewById(R.id.spin);
        floating_action_button = findViewById(R.id.floating_action_button);
        amount = findViewById(R.id.amount);
        totalAmount = findViewById(R.id.et_total_amount);

        int color = ContextCompat.getColor(this, R.color.white);
        floating_action_button.getDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        save = findViewById(R.id.save);

        back.setOnClickListener(v -> finish());

        totalAmount.setText(totalAmountText);

        Log.e("TotalAmount", "TotalAmount=============: " + totalAmountText);

        getServiceType();

        serviceType.add("Service Type");

    }

    List<String> serviceType = new ArrayList<>();
    ArrayAdapter<String> adapterDriver;

    private OkHttpClient.Builder createHttpClient() {
        return new OkHttpClient.Builder();
    }

    public void getServiceType() {

        OkHttpClient.Builder httpClient = createHttpClient();

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

        Api loginService = retrofit.create(Api.class);
        Call<ServiceTypeDataModel> call = loginService.get_getServiceType();

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ServiceTypeDataModel> call,
                                   @NonNull Response<ServiceTypeDataModel> response) {

                Log.e("response..", " " + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {
//
                    ArrayList<ServiceTypeDataModel> branches = response.body().getData();

                    for (ServiceTypeDataModel branch : branches) {
                        serviceType.add(branch.getService_types());
                        Log.e("getVoucher_type", "getVoucher_type================: "
                                + branch.getService_types());
//                        s = branch.getVoucher_type();
                    }
//                    get_Debit(String.valueOf();
//
                    adapterDriver = new ArrayAdapter<>(VehicleServiceAmountActivity.this,
                            R.layout.simple_spinner_item1,
                            serviceType);
                    adapterDriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    HomeTodayListAdapter adapter = new HomeTodayListAdapter(VehicleServiceAmountActivity.this, response.body().getData());
                    spin.setAdapter(adapter);

                    Log.e("response..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(VehicleServiceAmountActivity.this,
                            "Network Error!!",
                            Toast.LENGTH_SHORT).show();
                }
//                dialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<ServiceTypeDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("ServiceTypeDataModel", " " + t);
//                dialog.dismiss();
            }
        });
    }

    int enteredValue = 0;

    public void Creat_Service_Status() {

        OkHttpClient.Builder httpClient = createHttpClient();

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

        Api loginService = retrofit.create(Api.class);

        String size = String.valueOf(serviceSubDataList.size());
        RequestBodyData requestBody = new RequestBodyData(
                date,
                "2023-24",
                vehicle,
                garage,
                km_,
                totalAmountText,
                size,
                description_,
                serviceSubDataList
        );
        Gson gson = new Gson();
        String requestBodyJson = gson.toJson(requestBody);

// Log the request body
        Log.e("RequestLog", "Request Body: " + requestBodyJson);

        Call<ServiceStatusDataModel> call = loginService.get_getServiceStatus(requestBody);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ServiceStatusDataModel> call,
                                   @NonNull Response<ServiceStatusDataModel> response) {
                Log.e("response..", "" + response);

                Log.e("selectedItems..", "selectedItems---------------------------" + selectedItems);
                Log.e("selectedAmount..", "selectedAmount---------------------------" + selectedAmount);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    Toast.makeText(VehicleServiceAmountActivity.this,
                            " " + response.body().getMsg(),
                            Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(VehicleServiceAmountActivity.this, HomeActivity.class));

                } else {
                    Toast.makeText(VehicleServiceAmountActivity.this,
                            "Network Error!!",
                            Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<ServiceStatusDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("ServiceStatusDataModel", " " + t);
            }
        });

    }
    //    ServiceData serviceData;
    public Map<Integer, String> selectedItems = new HashMap<>();
    public ArrayList<String> selectedAmount = new ArrayList<>();

    //    Map<String, String> serviceSubDataList = new HashMap<>();
    ArrayList<ServiceSubData> serviceSubDataList = new ArrayList<>();
    String selectedItem;

    public String serviceSubType;

    public String serviceSubAmount;

    public class HomeTodayListAdapter extends
            RecyclerView.Adapter<HomeTodayListAdapter.Holder> {

        VehicleServiceAmountActivity context;
        ArrayList<ServiceTypeDataModel> data;

        private int itemCount = 1;

        public HomeTodayListAdapter(VehicleServiceAmountActivity context,
                                    ArrayList<ServiceTypeDataModel> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getItemCount() {
            return getItemCountInternal();
        }

        public void incrementItemCount() {
            itemCount++;
        }

        private int getItemCountInternal() {
            return itemCount;
        }

        @NonNull
        @Override
        public HomeTodayListAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent,
                                                              int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_srvice,
                    parent,
                    false);
            return new HomeTodayListAdapter.Holder(view);
        }

        int b;

        @Override
        public void onBindViewHolder(@NonNull final HomeTodayListAdapter.Holder holder,
                                     @SuppressLint("RecyclerView") final int position) {

            holder.spinner.setAdapter(adapterDriver);

            holder.plush.setOnClickListener(v -> {
                incrementItemCount();
                enteredValue += (holder.getAmountValue());
                amount.setText(enteredValue);
                Log.e("enteredValue", "enteredValue: " + enteredValue);
                Log.e("getAmountValue", "getAmountValue=-===------------------: " + holder.getAmountValue());
            });

            floating_action_button.setOnClickListener(v -> {

                if(holder.getAmountValue() == 0 ||
                        adapterDriver.getPosition(selectedItems.get(position))==0) {

                    if(holder.getAmountValue() == 0) {
                        Toast.makeText(context, "Enter Amount", Toast.LENGTH_SHORT).show();
                    } else if(adapterDriver.getPosition(selectedItems.get(position))==0) {
                        Toast.makeText(context, "Select Type", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(context, "Fill Select Type And Amount", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    enteredValue += (holder.getAmountValue());

                    amount.setText(String.valueOf(enteredValue));
                    Log.e("enteredValue", "enteredValue: " + enteredValue);

                    if (selectedItems.containsKey(position)) {
                        int selectedItemPosition = adapterDriver.getPosition(selectedItems.get(position));
                        holder.spinner.setSelection(selectedItemPosition);

                        serviceType = Collections.singletonList(selectedItem);

                        serviceSubAmount = String.valueOf(holder.getAmountValue());

                        serviceSubDataList.add(new ServiceSubData(serviceSubType, serviceSubAmount));

//                        serviceSubDataList.put(serviceSubType,serviceSubAmount);

                        Log.e("selectedItems", "selectedItems: " + selectedItems);
                    }
                    selectedAmount.add(String.valueOf(holder.getAmountValue()));
                    Log.e("selectedAmount", "selectedAmount: " + selectedAmount);

                    if (amount != null) {

                        String ta = totalAmount.getText().toString();
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
                    String ta = totalAmount.getText().toString();
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
                        int selectedItemPosition = adapterDriver.getPosition(selectedItems.get(position));
                        holder.spinner.setSelection(selectedItemPosition);

                        Log.e("selectedItems", "selectedItems: " + selectedItems);
                    }
                    selectedAmount.add(String.valueOf(holder.getAmountValue()));
                    Log.e("selectedAmount", "selectedAmount: " + selectedAmount);
                }
            });

            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                           int selectedPosition,
                                           long id) {

                    selectedItem = (String) parentView.getItemAtPosition(selectedPosition);
                    selectedItems.put(position, selectedItem);

                    serviceSubType = selectedItem;

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {

                }

            });

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
                    Log.e("NumberFormatException", e.toString());
                    return 0;
                }
            }
        }

    }

}