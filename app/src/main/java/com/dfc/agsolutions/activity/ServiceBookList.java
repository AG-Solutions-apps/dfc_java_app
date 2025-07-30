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

public class ServiceBookList extends AppCompatActivity {
    String totalamount;
    String service_ref;
    ImageView save;
    SharedPreferences sp;
    SharedPreferences.Editor ed;

    ProgressDialog dialog;
    Spinner spinner;
    private ArrayList<CreateServiceListDataModel> data;
    RecyclerView service_sub_list;

    EditText amount, tamount, amount1;

    String fainalservicetype;

    Home_Today_list_Adapter home_today_list_adapter;

    String tamountttt;

    int plushamount;

    CardView cd;

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
        tamount = findViewById(R.id.et_total_amount);
        amount1 = findViewById(R.id.amount1);

        save = findViewById(R.id.save);
        cd = findViewById(R.id.cd);
        serviceType.add("Service Type");

        tamountttt = getIntent().getStringExtra("amount");

        tamount.setText(tamountttt);
        amount1.setText(String.valueOf(amontttt));
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
//
//                possss = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });


        findViewById(R.id.continues).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int plu = 0;
                if (Myapplication.isNetworkAvailable()) {


                    if (!fainalservicetype.equals("Service Type") && amount.getText().toString().length() != 0) {

                        plu = Integer.parseInt(amount.getText().toString());

                        plushamount += plu;

                        if (Integer.parseInt(tamountttt) < plushamount) {
                            Toast.makeText(ServiceBookList.this, "Your Amount High", Toast.LENGTH_SHORT).show();
//                            int i = plu - plushamount;
                            plushamount -= plu;
                            amount.setText("");
                        } else if (Integer.parseInt(tamountttt) > plushamount) {
                            if (plu != 0) {
                                setdata(plushamount);
                                get_create_service_sub_temp(fainalservicetype, amount);
                                amount.setText("");
                                spinner.setSelection(0);
                            }
                        } else if (Integer.parseInt(tamountttt) == plushamount) {
                            setdata(plushamount);
                            get_create_service_sub_temp(fainalservicetype, amount);
                            save.setVisibility(View.VISIBLE);
                            cd.setVisibility(View.GONE);
                        }
                    }


                } else {
                    Myapplication.noInternet(ServiceBookList.this);

                }

            }
        });

        save.setOnClickListener(v -> {

            creat_service(fainalservicetype);

        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Integer.parseInt(tamountttt) == plushamount) {
            save.setVisibility(View.VISIBLE);
            cd.setVisibility(View.GONE);
        }

    }

    List<String> serviceType = new ArrayList<>();
    ArrayAdapter<String> adapterdriver;

    int possss;

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
                .baseUrl(getString(R.string.commn_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        Api loginservice = retrofit.create(Api.class);
        Call<CreateServiceListDataModel> call = loginservice.get_getServicesubType(service_ref, fainalservicetype, amount.getText().toString());
        call.enqueue(new Callback<CreateServiceListDataModel>() {
            @Override
            public void onResponse(Call<CreateServiceListDataModel> call, Response<CreateServiceListDataModel> response) {
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
            public void onFailure(Call<CreateServiceListDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
    }

    public void creat_service(String fainalservicetype) {

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
                .baseUrl(getString(R.string.commn_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        Api loginservice = retrofit.create(Api.class);
        Call<ServiceSubFinalModel> call = loginservice.Service_final(fainalservicetype);
        call.enqueue(new Callback<ServiceSubFinalModel>() {
            @Override
            public void onResponse(Call<ServiceSubFinalModel> call, Response<ServiceSubFinalModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {
//
                    Toast.makeText(ServiceBookList.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
//                    home_today_list_adapter.adddata(response.body().getData());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(ServiceBookList.this, HomeActivity.class));
                        }
                    }, 1000);

                } else {
                    Toast.makeText(ServiceBookList.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<ServiceSubFinalModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
    }

    public void delete(String idd, String fainalservicetype, int pos) {

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
                .baseUrl(getString(R.string.commn_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        Api loginservice = retrofit.create(Api.class);
        Call<DeleteModel> call = loginservice.delete_ServiceType(idd, fainalservicetype);
        call.enqueue(new Callback<DeleteModel>() {
            @Override
            public void onResponse(Call<DeleteModel> call, Response<DeleteModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {
//
//                    Toast.makeText(ServiceBookList.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
//                    home_today_list_adapter.adddata(response.body().getData());
                    Toast.makeText(ServiceBookList.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
//                    home_today_list_adapter.notifyDataSetChanged();
                    home_today_list_adapter.removee(pos);
                    int s = plushamount -= positionamount;
                    setdata(s);


                } else {
                    Toast.makeText(ServiceBookList.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<DeleteModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
    }

    public void edit_service(String idd, String fainalservicetype, String amm, String sType, int positionamount, int posi) {

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

        Log.e("TAG", "edit_service: " + idd + " : " + fainalservicetype + " : " + amm + " : " + sType);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.commn_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        Api loginservice = retrofit.create(Api.class);
        Call<CreateServiceListDataModel> call = loginservice.Edit_ServiceType(idd, fainalservicetype, sType, amm);
        call.enqueue(new Callback<CreateServiceListDataModel>() {
            @Override
            public void onResponse(Call<CreateServiceListDataModel> call, Response<CreateServiceListDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {

                    Toast.makeText(ServiceBookList.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                    home_today_list_adapter.adddata(response.body().getData());

                    ArrayList<CreateServiceListDataModel> branches = response.body().getData();
//                    for (CreatServicaeListDataModel branch : branches) {
//
//
                    Log.e("branches", "branches111111111: " + branches.size());
//
//                    }

//                    int s = plushamount - positionamount;
//                    plushamount = s + Integer.parseInt(amm);

                    setdata(posi);

                    Log.e("TAG", "onResponse: " + response.body().getData());


                } else {
                    Toast.makeText(ServiceBookList.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<CreateServiceListDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
    }

    int amontttt = 0;
    String id;

    String fainalservicetype1;

    int positionamount;

    public class Home_Today_list_Adapter extends RecyclerView.Adapter<Home_Today_list_Adapter.Holder> {
        private Activity context;

        List<CreateServiceListDataModel> arrayListTopic = new ArrayList<>();

        public Home_Today_list_Adapter(Activity context) {
            this.context = context;
            arrayListTopic = new ArrayList<>();

//            this.arrayListTopic = new List<CreatServicaeListDataModel>() {
//            };
        }


        public void adddata(ArrayList<CreateServiceListDataModel> arrayListTopics) {
            arrayListTopic.clear();
            arrayListTopic.addAll(arrayListTopics);
            Log.e("arrayListTopics", "arrayListTopics: " + arrayListTopics);
            notifyDataSetChanged();
        }

        public void refresh(ArrayList<CreateServiceListDataModel> arrayListTopics) {
            arrayListTopic.clear();
            arrayListTopic.addAll(arrayListTopics);
            Log.e("arrayListTopics", "arrayListTopics: " + arrayListTopics);
            notifyDataSetChanged();
        }

        public void removee(int pos) {
//            arrayListTopic.clear();
            arrayListTopic.remove(pos);
            Log.e("arrayListTopics", "arrayListTopics: " + pos);
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
            int count = 1 + position;

            holder.servicetype.setText(arrayListTopic.get(position).getTemp_service_sub_type());
            holder.textnumber.setText(String.valueOf(count));
            holder.serviceamt.setText("" + arrayListTopic.get(position).getTemp_service_sub_amount());


            holder.spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    fainalservicetype1 = (String) parentView.getItemAtPosition(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }
            });

            adapterdriver = new ArrayAdapter<>(ServiceBookList.this, R.layout.simple_spinner_item1, serviceType);
            adapterdriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinner1.setAdapter(adapterdriver);
            int posssssss = possss;

            holder.edt.setOnClickListener(v -> {

                holder.lin.setVisibility(View.VISIBLE);
                holder.delet.setVisibility(View.GONE);
                holder.edt.setVisibility(View.GONE);
                holder.edta.setText(arrayListTopic.get(position).getTemp_service_sub_amount());
                holder.spinner1.setSelection(posssssss);

            });

            holder.done.setOnClickListener(v -> {
                positionamount = Integer.parseInt(arrayListTopic.get(position).getTemp_service_sub_amount());
                id = arrayListTopic.get(position).getId();
                int posi = count - 1;
                String edta = holder.edta.getText().toString().trim();
                int ta = plushamount;
                int minvalue = ta-positionamount;
                ta = minvalue+Integer.parseInt(edta);

                if (Integer.parseInt(tamountttt) < ta) {
                    Toast.makeText(ServiceBookList.this, "Your Amount High", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(tamountttt) > ta) {
                    if (fainalservicetype1.equals("Service Type")) {
                        Toast.makeText(context, "Pleash Select Service Type", Toast.LENGTH_SHORT).show();
                    } else {

                        edit_service(id, service_ref, edta, fainalservicetype1, positionamount, ta);
                        holder.lin.setVisibility(View.GONE);
                        holder.delet.setVisibility(View.VISIBLE);
                        holder.edt.setVisibility(View.VISIBLE);
                    }
                } else if (Integer.parseInt(tamountttt) == ta) {
                    if (fainalservicetype1.equals("Service Type")) {
                        Toast.makeText(context, "Pleash Select Service Type", Toast.LENGTH_SHORT).show();
                    } else {
                        edit_service(id, service_ref, edta, fainalservicetype1, positionamount, ta);
                        holder.lin.setVisibility(View.GONE);
                        holder.delet.setVisibility(View.VISIBLE);
                        holder.edt.setVisibility(View.VISIBLE);
                    }
                }
            });

            holder.delet.setOnClickListener(v ->
            {
                try {
                    int posi = count - 1;
                    id = arrayListTopic.get(position).getId();
                    delete(id, fainalservicetype, posi);
                    positionamount = Integer.parseInt(arrayListTopic.get(position).getTemp_service_sub_amount());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

//        public void refresh(ArrayList<EditServiceModell> data) {
//
////            arrayListTopic.clear();
////            arrayListTopic.addAll(data);
//            Log.e("arrayListTopics", "arrayListTopics: " + data);
//            notifyDataSetChanged();
//
//        }

        class Holder extends RecyclerView.ViewHolder {

            TextView servicetype, serviceamt, textnumber;

            EditText edta;

            Spinner spinner1;
            ImageView delet, edt;

            CardView done;
            LinearLayout lin;

            public Holder(@NonNull View itemView) {
                super(itemView);
                servicetype = itemView.findViewById(R.id.tv_service_type);
                textnumber = itemView.findViewById(R.id.tv_text_number);
                serviceamt = itemView.findViewById(R.id.tv_service_amt);
                edta = itemView.findViewById(R.id.et_service_amount);
                spinner1 = itemView.findViewById(R.id.spinner1);
                delet = itemView.findViewById(R.id.iv_delete);
                edt = itemView.findViewById(R.id.edt);
                done = itemView.findViewById(R.id.done);
                lin = itemView.findViewById(R.id.lin);
            }
        }

    }

    public void setdata(int amontt) {

        amount1.setText(String.valueOf(amontt));
        String t = amount1.getText().toString();

         if (Integer.parseInt(tamountttt) == Integer.parseInt(t)) {
            save.setVisibility(View.VISIBLE);
            cd.setVisibility(View.GONE);
        }

    }

}