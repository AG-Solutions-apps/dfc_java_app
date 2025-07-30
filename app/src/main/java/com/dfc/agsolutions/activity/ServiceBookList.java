package com.dfc.agsolutions.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dfc.agsolutions.app_utils.Myapplication;
import com.dfc.agsolutions.model.CreateServiceListDataModel;
import com.dfc.agsolutions.model.DeleteModel;
import com.dfc.agsolutions.model.ServiceTypeDataModel;
import com.dfc.agsolutions.model.ServiceSubFinalModel;
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

public class ServiceBookList extends
        AppCompatActivity {

    String service_ref;
    ImageView save;
    SharedPreferences sp;
    SharedPreferences.Editor ed;

    ProgressDialog dialog;
    Spinner spinner;

    RecyclerView service_sub_list;

    EditText amount, et_total_amount, amount1;

    String finalServiceType;

    HomeTodayListAdapter home_today_list_adapter;

    String totalAmount;

    int plushAmount;

    CardView cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_book_list);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();

        service_ref = getIntent().getStringExtra("service_ref");

        dialog = new ProgressDialog(ServiceBookList.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        spinner = findViewById(R.id.spinner);
        service_sub_list = findViewById(R.id.service_sub_list);
        amount = findViewById(R.id.amount);
        et_total_amount = findViewById(R.id.et_total_amount);
        amount1 = findViewById(R.id.amount1);

        save = findViewById(R.id.save);
        cd = findViewById(R.id.cd);
        serviceType.add("Service Type");

        totalAmount = getIntent().getStringExtra("amount");

        et_total_amount.setText(totalAmount);
        amount1.setText(String.valueOf(amountOne));
        if (Myapplication.isNetworkAvailable()) {
            get_Service_type();
        } else {
            Myapplication.noInternet(ServiceBookList.this);
        }

        home_today_list_adapter = new HomeTodayListAdapter(ServiceBookList.this);
        service_sub_list.setAdapter(home_today_list_adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                finalServiceType = (String) parentView.getItemAtPosition(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });


        findViewById(R.id.continues).setOnClickListener(v -> {
            int plu;
            if (Myapplication.isNetworkAvailable()) {

                if (!finalServiceType.equals("Service Type") && !amount.getText().toString().isEmpty()) {

                    plu = Integer.parseInt(amount.getText().toString());

                    plushAmount += plu;

                    if (Integer.parseInt(totalAmount) < plushAmount) {
                        Toast.makeText(ServiceBookList.this, "Your Amount High",
                                Toast.LENGTH_SHORT).show();

                        plushAmount -= plu;
                        amount.setText("");
                    } else if (Integer.parseInt(totalAmount) > plushAmount) {
                        if (plu != 0) {
                            setData(plushAmount);
                            get_create_service_sub_temp(finalServiceType, amount);
                            amount.setText("");
                            spinner.setSelection(0);
                        }
                    } else if (Integer.parseInt(totalAmount) == plushAmount) {
                        setData(plushAmount);
                        get_create_service_sub_temp(finalServiceType, amount);
                        save.setVisibility(View.VISIBLE);
                        cd.setVisibility(View.GONE);
                    }
                }


            } else {
                Myapplication.noInternet(ServiceBookList.this);

            }

        });

        save.setOnClickListener(v -> creat_service(finalServiceType));

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Integer.parseInt(totalAmount) == plushAmount) {
            save.setVisibility(View.VISIBLE);
            cd.setVisibility(View.GONE);
        }

    }

    List<String> serviceType = new ArrayList<>();
    ArrayAdapter<String> adapterDriver;

    int amtPosition;

    private OkHttpClient.Builder createHttpClient() {
        return new OkHttpClient.Builder();
    }

    public void get_Service_type() {

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
                .baseUrl(getString(R.string.commn_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        Api loginService = retrofit.create(Api.class);

        Call<ServiceTypeDataModel> call = loginService.getServiceType();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ServiceTypeDataModel> call,
                                   @NonNull Response<ServiceTypeDataModel> response) {
                Log.e("ServiceTypeDataModel: ", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    ArrayList<ServiceTypeDataModel> branches = response.body().getData();

                    for (ServiceTypeDataModel branch : branches) {
                        serviceType.add(branch.getService_types());
                        Log.e("getVoucher_type", "getVoucher_type================: " + branch.getService_types());
//                        s = branch.getVoucher_type();
                    }
                    adapterDriver = new ArrayAdapter<>(ServiceBookList.this, R.layout.simple_spinner_item1, serviceType);
                    adapterDriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapterDriver);

                    Log.e("response..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(ServiceBookList.this,
                            "Network Error!!",
                            Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<ServiceTypeDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("ServiceTypeDataModel Failure: ", "" + t);
                dialog.dismiss();
            }
        });
    }

    public void get_create_service_sub_temp(String finalServiceType, EditText amount) {

        dialog.show();

        OkHttpClient.Builder httpClient = createHttpClient();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer "
                            + sp.getString("token", ""))
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

        Call<CreateServiceListDataModel> call = loginService.getServiceSubType(service_ref,
                finalServiceType,
                amount.getText().toString());

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<CreateServiceListDataModel> call,
                                   @NonNull Response<CreateServiceListDataModel> response) {
                Log.e("CreateServiceListDataModel: ", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    Toast.makeText(ServiceBookList.this,
                            response.body().getMsg(),
                            Toast.LENGTH_SHORT).show();
                    home_today_list_adapter.addData(response.body().getData());

                } else {
                    Toast.makeText(ServiceBookList.this,
                            response.body().getMsg(),
                            Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<CreateServiceListDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("CreateServiceListDataModel", " " + t);
                dialog.dismiss();
            }
        });
    }

    public void creat_service(String finalServiceType) {

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
                .baseUrl(getString(R.string.commn_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        Api loginService = retrofit.create(Api.class);

        Call<ServiceSubFinalModel> call = loginService.fetchServiceFinal(finalServiceType);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ServiceSubFinalModel> call,
                                   @NonNull Response<ServiceSubFinalModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    Toast.makeText(ServiceBookList.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(() -> startActivity(new Intent(ServiceBookList.this,
                            HomeActivity.class)), 1000);
                } else {
                    Toast.makeText(ServiceBookList.this,
                            response.body().getMsg(),
                            Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<ServiceSubFinalModel> call,
                                  @NonNull Throwable t) {
                Log.e("ServiceSubFinalModel: ", "" + t);
                dialog.dismiss();
            }
        });
    }

    public void delete(String idd, String finalServiceType, int pos) {

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
                .baseUrl(getString(R.string.commn_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        Api loginService = retrofit.create(Api.class);

        Call<DeleteModel> call = loginService.deleteServiceType(idd, finalServiceType);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<DeleteModel> call,
                                   @NonNull Response<DeleteModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {
                    Toast.makeText(ServiceBookList.this,
                            response.body().getMsg(),
                            Toast.LENGTH_SHORT).show();
                    home_today_list_adapter.remove(pos);
                    int s = plushAmount -= positionalAmount;
                    setData(s);
                } else {
                    Toast.makeText(ServiceBookList.this,
                            response.body().getMsg(),
                            Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<DeleteModel> call,
                                  @NonNull Throwable t) {
                Log.e("DeleteModel: ", "" + t);
                dialog.dismiss();
            }
        });
    }

    public void editService(String idd,
                            String finaleServiceType,
                            String amm, String sType,
                            int posi) {

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

        Log.e("TAG", "edit_service: " + idd + " : " + finaleServiceType + " : " + amm + " : " + sType);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.commn_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        Api loginService = retrofit.create(Api.class);

        Call<CreateServiceListDataModel> call = loginService.editServiceType(idd, finaleServiceType, sType, amm);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<CreateServiceListDataModel> call,
                                   @NonNull Response<CreateServiceListDataModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    Toast.makeText(ServiceBookList.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                    home_today_list_adapter.addData(response.body().getData());

                    ArrayList<CreateServiceListDataModel> branches = response.body().getData();

                    Log.e("branches", "branches111111111: " + branches.size());

                    setData(posi);

                    Log.e("TAG", "onResponse: " + response.body().getData());


                } else {
                    Toast.makeText(ServiceBookList.this,
                            response.body().getMsg(),
                            Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<CreateServiceListDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("CreateServiceListDataModel: ", "" + t);
                dialog.dismiss();
            }
        });
    }

    int amountOne = 0;
    String id;

    String finalServiceType1;

    int positionalAmount;

    public class HomeTodayListAdapter extends
            RecyclerView.Adapter<HomeTodayListAdapter.Holder> {

        private final Activity context;

        List<CreateServiceListDataModel> arrayListTopic;

        public HomeTodayListAdapter(Activity context) {
            this.context = context;
            arrayListTopic = new ArrayList<>();
        }

        public void addData(ArrayList<CreateServiceListDataModel> arrayListTopics) {
            arrayListTopic.clear();
            arrayListTopic.addAll(arrayListTopics);
            Log.e("arrayListTopics", "arrayListTopics: " + arrayListTopics);
            arrayListTopic.notify();
        }

        public void remove(int pos) {
            arrayListTopic.remove(pos);
            Log.e("arrayListTopics", "arrayListTopics: " + pos);
            arrayListTopic.notify();
        }

        @Override
        public int getItemCount() {
            return arrayListTopic.size();
        }

        @NonNull
        @Override
        public HomeTodayListAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent,
                                                              int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itam_service_list,
                    parent,
                    false);
            return new HomeTodayListAdapter.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final HomeTodayListAdapter.Holder holder,
                                     @SuppressLint("RecyclerView") final int position) {
            int count = 1 + position;

            holder.serviceType.setText(arrayListTopic.get(position).getTemp_service_sub_type());
            holder.text_number.setText(String.valueOf(count));
            String serviceAmount = " " + arrayListTopic.get(position).getTemp_service_sub_amount();
            holder.service_amt.setText(serviceAmount);


            holder.spinner_amount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                           int position, long id) {
                    finalServiceType1 = (String) parentView.getItemAtPosition(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }
            });

            adapterDriver = new ArrayAdapter<>(ServiceBookList.this, R.layout.simple_spinner_item1, serviceType);
            adapterDriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinner_amount.setAdapter(adapterDriver);
            int amountPosition = amtPosition;

            holder.edt.setOnClickListener(v -> {

                holder.lin.setVisibility(View.VISIBLE);
                holder.iv_delete.setVisibility(View.GONE);
                holder.edt.setVisibility(View.GONE);
                holder.edt_service_amount.setText(arrayListTopic.get(position).getTemp_service_sub_amount());
                holder.spinner_amount.setSelection(amountPosition);

            });

            holder.done.setOnClickListener(v -> {
                positionalAmount = Integer.parseInt(arrayListTopic.get(position).getTemp_service_sub_amount());
                id = arrayListTopic.get(position).getId();

                String edt_service_amount = holder.edt_service_amount.getText().toString().trim();
                int ta = plushAmount;
                int minvalue = ta- positionalAmount;
                ta = minvalue+Integer.parseInt(edt_service_amount);

                if (Integer.parseInt(totalAmount) < ta) {
                    Toast.makeText(ServiceBookList.this, "Your Amount High", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(totalAmount) > ta) {
                    if (finalServiceType1.equals("Service Type")) {
                        Toast.makeText(context, "Please Select Service Type", Toast.LENGTH_SHORT).show();
                    } else {

                        editService(id, service_ref, edt_service_amount, finalServiceType1, ta);
                        holder.lin.setVisibility(View.GONE);
                        holder.iv_delete.setVisibility(View.VISIBLE);
                        holder.edt.setVisibility(View.VISIBLE);
                    }
                } else if (Integer.parseInt(totalAmount) == ta) {
                    if (finalServiceType1.equals("Service Type")) {
                        Toast.makeText(context, "Please Select Service Type", Toast.LENGTH_SHORT).show();
                    } else {
                        editService(id, service_ref, edt_service_amount, finalServiceType1, ta);
                        holder.lin.setVisibility(View.GONE);
                        holder.iv_delete.setVisibility(View.VISIBLE);
                        holder.edt.setVisibility(View.VISIBLE);
                    }
                }
            });

            holder.iv_delete.setOnClickListener(v -> {
                try {
                    int posi = count - 1;
                    id = arrayListTopic.get(position).getId();
                    delete(id, finalServiceType, posi);
                    positionalAmount = Integer.parseInt(arrayListTopic.get(position).getTemp_service_sub_amount());

                } catch (Exception e) {
                    Log.e("Exception: ", e.toString());
                }
            });
        }

        class Holder extends RecyclerView.ViewHolder {

            TextView serviceType, service_amt, text_number;

            EditText edt_service_amount;

            Spinner spinner_amount;
            ImageView iv_delete, edt;

            CardView done;
            LinearLayout lin;

            public Holder(@NonNull View itemView) {
                super(itemView);
                serviceType = itemView.findViewById(R.id.tv_service_type);
                text_number = itemView.findViewById(R.id.tv_text_number);
                service_amt = itemView.findViewById(R.id.tv_service_amt);
                edt_service_amount = itemView.findViewById(R.id.et_service_amount);
                spinner_amount = itemView.findViewById(R.id.spinner_amount);
                iv_delete = itemView.findViewById(R.id.iv_delete);
                edt = itemView.findViewById(R.id.edt);
                done = itemView.findViewById(R.id.done);
                lin = itemView.findViewById(R.id.lin);
            }
        }

    }

    public void setData(int amount) {

        amount1.setText(String.valueOf(amount));
        String t = amount1.getText().toString();

        if (Integer.parseInt(totalAmount) == Integer.parseInt(t)) {
            save.setVisibility(View.VISIBLE);
            cd.setVisibility(View.GONE);
        }

    }

}