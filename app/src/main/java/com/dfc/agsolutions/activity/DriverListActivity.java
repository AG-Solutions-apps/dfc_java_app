package com.dfc.agsolutions.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.dfc.agsolutions.model.DriverListDataModel;
import com.dfc.agsolutions.R;

import java.text.ParseException;
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

public class DriverListActivity extends AppCompatActivity {

    RecyclerView rv_driver_list;

    ProgressDialog dialog;
    SharedPreferences sp;

    SharedPreferences.Editor ed;
    SwipeRefreshLayout swipeRefreshLayout;
    LottieAnimationView lav_no_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_list);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        lav_no_data = findViewById(R.id.lav_no_data);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            lav_no_data.setVisibility(View.VISIBLE);
            rv_driver_list.setVisibility(View.GONE);
            driverList();
        });

        dialog = new ProgressDialog(DriverListActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        rv_driver_list = findViewById(R.id.rv_driver_list);

        findViewById(R.id.back).setOnClickListener(v -> finish());
        driverList();
    }

    List<String> full_name = new ArrayList<>();
    List<String> mobile = new ArrayList<>();
    List<String> dl_expiry = new ArrayList<>();
    List<String> user_status = new ArrayList<>();
    List<String> user_image = new ArrayList<>();

    public void driverList() {

        dialog.show();
        full_name.clear();
        mobile.clear();
        dl_expiry.clear();
        user_status.clear();
        user_image.clear();

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

        Call<DriverListDataModel> call = loginService.getDriverList(sp.getString("userBranch", ""));
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<DriverListDataModel> call,
                                   @NonNull Response<DriverListDataModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    ArrayList<DriverListDataModel> branches = response.body().getData();

                    lav_no_data.setVisibility(View.GONE);
                    rv_driver_list.setVisibility(View.VISIBLE);
                    HomeTodayListAdapter adapter = new HomeTodayListAdapter(response.body().getData());
                    rv_driver_list.setAdapter(adapter);

                    Log.e("response..", "branches:-  " + branches.size());

                } else {
                    lav_no_data.setVisibility(View.VISIBLE);
                    rv_driver_list.setVisibility(View.GONE);
                    Toast.makeText(DriverListActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);


            }

            @Override
            public void onFailure(@NonNull Call<DriverListDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("DriverListDataModel: ", "" + t);
                dialog.dismiss();
                lav_no_data.setVisibility(View.VISIBLE);
                rv_driver_list.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

            }
        });

    }

    public class HomeTodayListAdapter extends RecyclerView.Adapter<HomeTodayListAdapter.Holder> {

        ArrayList<DriverListDataModel> arrayListTopic;

        public HomeTodayListAdapter(ArrayList<DriverListDataModel> arrayListTopic) {
            this.arrayListTopic = arrayListTopic;
        }

        @Override
        public int getItemCount() {
            return arrayListTopic.size();
        }

        @NonNull
        @Override
        public HomeTodayListAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.driver_list_item, parent, false);
            return new HomeTodayListAdapter.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final HomeTodayListAdapter.Holder holder,
                                     @SuppressLint("RecyclerView") final int position) {

            holder.tv_name.setText(arrayListTopic.get(position).getFull_name());

            String status = "Status : " + arrayListTopic.get(position).getUser_status();
            holder.tv_active_status.setText(status);
//            holder.date.setText("Lic Expiry : " + arrayListTopic.get(position).getDl_expiry());

            String phoneNo = "Phone no : " + arrayListTopic.get(position).getMobile();
            holder.iv_mobile.setText(phoneNo);

            String date1 = arrayListTopic.get(position).getDl_expiry();

            try {
                SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = inputDateFormat.parse(date1);

                SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String dateFormated;
                if (date != null) {
                    dateFormated = outputDateFormat.format(date);
                    String licExpiry = "Lic Expiry : " + dateFormated;
                    holder.tv_date.setText(licExpiry);
                }

            } catch (ParseException e) {
                Log.e("TAG", "onBindViewHolder: " + e);
                String licExpiry = "Lic Expiry : " + date1;
                holder.tv_date.setText(licExpiry);
            }

            if (arrayListTopic.get(position).getUser_image() == null) {
                Glide.with(DriverListActivity.this).load(Uri.parse("https://dfcgroup.in/crmapi/storage/app/public/profiles/no_profile.png")).error(R.drawable.no_profile).into(holder.iv_profile);
            } else {
                Glide.with(DriverListActivity.this).load(Uri.parse("https://dfcgroup.in/crmapi/storage/app/public/profiles/" + arrayListTopic.get(position).getUser_image())).error(R.drawable.no_profile).into(holder.iv_profile);
            }

            Log.e("TAG", "onBindViewHolder: " + arrayListTopic.get(position).getUser_image());

            holder.iv_call.setOnClickListener(v -> {

                String phoneNumber = "tel:" + "+91 " + arrayListTopic.get(position).getMobile(); // replace with the actual phone number
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber));
                startActivity(dialIntent);

            });

            holder.iv_whats_app.setOnClickListener(v -> {

                try {
                    Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" +
                            "+91 +" + arrayListTopic.get(position).getMobile());

                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                    intent.setPackage("com.whatsapp");

                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("TAG", "onBindViewHolder: " + e);
                }

            });
        }

        class Holder extends RecyclerView.ViewHolder {

            TextView tv_name, iv_mobile, tv_date, tv_active_status;
            ImageView iv_profile, iv_call, iv_whats_app;

            public Holder(@NonNull View itemView) {
                super(itemView);

                tv_name = itemView.findViewById(R.id.name);
                iv_mobile = itemView.findViewById(R.id.mobile);
                tv_date = itemView.findViewById(R.id.date);
                tv_active_status = itemView.findViewById(R.id.Active);
                iv_profile = itemView.findViewById(R.id.profile);
                iv_call = itemView.findViewById(R.id.call);
                iv_whats_app = itemView.findViewById(R.id.whatsapp);

            }
        }

    }

}