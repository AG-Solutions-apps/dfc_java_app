package com.dfc.agsolutions.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.dfc.agsolutions.AppUtils.Myapplication;
import com.dfc.agsolutions.Model.CreatServicaeListDataModel;
import com.dfc.agsolutions.Model.ServiceTypeDataModel;
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

public class ServiceBookList extends AppCompatActivity {
    String totalamount;
    String service_ref;
    ImageView save;
    SharedPreferences sp;
    SharedPreferences.Editor ed;

    ProgressDialog dialog;
    Spinner spinner;
    private ArrayList<CreatServicaeListDataModel> data;
    RecyclerView service_sub_list;

    EditText amount;

    String fainalservicetype;

    Home_Today_list_Adapter home_today_list_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_book_list);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();
        data = new ArrayList<>();
        totalamount = getIntent().getStringExtra("amount");
        service_ref = getIntent().getStringExtra("service_ref");
        dialog = new ProgressDialog(ServiceBookList.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        spinner = findViewById(R.id.spinner);
        service_sub_list = findViewById(R.id.service_sub_list);
        amount = findViewById(R.id.amount);


        save = findViewById(R.id.save);
        serviceType.add("Service Type");


        if (Myapplication.isNetworkAvailable()) {
            get_Service_type();
        } else {
            Myapplication.noInternet(ServiceBookList.this);

        }


        home_today_list_adapter = new Home_Today_list_Adapter(ServiceBookList.this);
        service_sub_list.setAdapter(home_today_list_adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                fainalservicetype = (String) parentView.getItemAtPosition(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });


        findViewById(R.id.cvVerifyOtp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Myapplication.isNetworkAvailable()) {


                    if (!fainalservicetype.equals("Service Type") && amount.getText().toString().length() != 0) {


                        get_create_service_sub_temp(fainalservicetype, amount);


                    }


                } else {
                    Myapplication.noInternet(ServiceBookList.this);

                }


            }
        });


    }

    List<String> serviceType = new ArrayList<>();
    ArrayAdapter<String> adapterdriver;

    public void get_Service_type() {

        dialog.show();
        ;
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
                .baseUrl(getString(R.string.base_url))
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
                    adapterdriver = new ArrayAdapter<>(ServiceBookList.this, R.layout.simple_spinner_item1, serviceType);
                    adapterdriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapterdriver);

                    Log.e("responce..", "branches:-  " + branches.size());


                } else {
                    Toast.makeText(ServiceBookList.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<ServiceTypeDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
    }

    public void get_create_service_sub_temp(String fainalservicetype, EditText amount) {

        dialog.show();
        ;
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
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        Api loginservice = retrofit.create(Api.class);
        Call<CreatServicaeListDataModel> call = loginservice.get_getServicesubType(service_ref, fainalservicetype, amount.getText().toString());
        call.enqueue(new Callback<CreatServicaeListDataModel>() {
            @Override
            public void onResponse(Call<CreatServicaeListDataModel> call, Response<CreatServicaeListDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {
//
                    Toast.makeText(ServiceBookList.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                    home_today_list_adapter.adddata(response.body().getData());

                } else {
                    Toast.makeText(ServiceBookList.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<CreatServicaeListDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
    }


    public class Home_Today_list_Adapter extends RecyclerView.Adapter<Home_Today_list_Adapter.Holder> {
        private Activity context;

        List<CreatServicaeListDataModel> arrayListTopic = new ArrayList<>();

        public Home_Today_list_Adapter(Activity context) {
            this.context = context;
            arrayListTopic = new ArrayList<>();
//            this.arrayListTopic = new List<CreatServicaeListDataModel>() {
//            };
        }


        public void adddata(ArrayList<CreatServicaeListDataModel> arrayListTopics) {
            arrayListTopic.addAll(arrayListTopics);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return arrayListTopic.size();
        }

        @NonNull
        @Override
        public Home_Today_list_Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itam_service_list, parent, false);
            return new Home_Today_list_Adapter.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final Home_Today_list_Adapter.Holder holder, @SuppressLint("RecyclerView") final int position) {


            holder.servicetype.setText(arrayListTopic.get(position).getTemp_service_sub_type());
            holder.textnumber.setText("" + position + 1);
            holder.serviceamt.setText("" + arrayListTopic.get(position).getTemp_service_sub_amount());

            holder.delet.setOnClickListener(v -> {

                try {


                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }

        class Holder extends RecyclerView.ViewHolder {

            TextView servicetype, serviceamt, textnumber;
            ImageView delet;

            public Holder(@NonNull View itemView) {
                super(itemView);


                servicetype = itemView.findViewById(R.id.servicetype);
                textnumber = itemView.findViewById(R.id.textnumber);
                serviceamt = itemView.findViewById(R.id.serviceamt);
                delet = itemView.findViewById(R.id.delet);


            }
        }


    }

}