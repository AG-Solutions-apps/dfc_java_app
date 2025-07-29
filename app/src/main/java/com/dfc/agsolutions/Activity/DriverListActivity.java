package com.dfc.agsolutions.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dfc.agsolutions.Model.DriverListDataModel;
import com.dfc.agsolutions.Model.TruckTypeModel;
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

    RecyclerView driverlist;

    ProgressDialog dialog;
    SharedPreferences sp;

    SharedPreferences.Editor ed;
    SwipeRefreshLayout swipeRefreshLayout;
    LottieAnimationView nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_list);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        nodata = findViewById(R.id.nodata);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                nodata.setVisibility(View.VISIBLE);
                driverlist.setVisibility(View.GONE);
                driver_list();
            }
        });
        dialog = new ProgressDialog(DriverListActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        driverlist = findViewById(R.id.driverlist);

        findViewById(R.id.back).setOnClickListener(v -> {
            finish();
        });
        driver_list();

    }

    List<String> fullname = new ArrayList<>();
    List<String> mobile = new ArrayList<>();
    List<String> dl_expiry = new ArrayList<>();
    List<String> user_status = new ArrayList<>();
    List<String> user_image = new ArrayList<>();


    public void driver_list() {

        dialog.show();
        fullname.clear();
        mobile.clear();
        dl_expiry.clear();
        user_status.clear();
        user_image.clear();
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
        Call<DriverListDataModel> call = loginservice.get_driverlist(sp.getString("userBranch", ""));
        call.enqueue(new Callback<DriverListDataModel>() {
            @Override
            public void onResponse(Call<DriverListDataModel> call, Response<DriverListDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {
//
                    ArrayList<DriverListDataModel> branches = response.body().getData();
//                    for (DriverListDataModel branch : branches) {
//
//                        fullname.add(branch.getFull_name());
//                        mobile.add(branch.getMobile());
//                        dl_expiry.add(branch.getDl_expiry());
//                        user_status.add(branch.getUser_status());
//                        user_image.add(branch.getUser_image());
//
//                    }
                    nodata.setVisibility(View.GONE);
                    driverlist.setVisibility(View.VISIBLE);
                    Home_Today_list_Adapter adapter = new Home_Today_list_Adapter(DriverListActivity.this, response.body().getData());
                    driverlist.setAdapter(adapter);

//
////response.body().getData().get(0).getReg_no();
////                    for (ServiceFatchVhicalDataModel branch : branches) {
////                        vhicalarray.add(branch.getReg_no());
//////                        vhicaldraiverarray.add(branch.getVehicle_driver());
//////                        milageaaray.add(branch.getVehicle_mileage());
////                    }
//                    for (ServiceFatchVhicalDataModel branch : branches) {
//                        vhicalarray.add(branch.getReg_no());
//                    }
////
//                    ArrayAdapter<String> adapter = new ArrayAdapter<>(DriverListActivity.this, R.layout.simple_spinner_item, vhicalarray);
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinner.setAdapter(adapter);

//
//                    ArrayAdapter<String> adapterdriver = new ArrayAdapter<>(CreatTrip.this, R.layout.simple_spinner_item, vhicaldraiverarray);
//                    adapterdriver.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinnerdriver.setAdapter(adapterdriver);


//                    setupSpinner(branchNames);
                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    nodata.setVisibility(View.VISIBLE);
                    driverlist.setVisibility(View.GONE);
                    Toast.makeText(DriverListActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);


            }

            @Override
            public void onFailure(Call<DriverListDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
                nodata.setVisibility(View.VISIBLE);
                driverlist.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

            }
        });

    }

    public class Home_Today_list_Adapter extends RecyclerView.Adapter<Home_Today_list_Adapter.Holder> {
        private Activity context;

        ArrayList<DriverListDataModel> arrayListTopic;

        public Home_Today_list_Adapter(Activity context, ArrayList<DriverListDataModel> arrayListTopic) {
            this.context = context;
            this.arrayListTopic = arrayListTopic;
        }

        @Override
        public int getItemCount() {
            return arrayListTopic.size();
        }

        @NonNull
        @Override
        public Home_Today_list_Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.driver_list_item, parent, false);
            return new Home_Today_list_Adapter.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final Home_Today_list_Adapter.Holder holder, @SuppressLint("RecyclerView") final int position) {


            holder.name.setText(arrayListTopic.get(position).getFull_name());
            holder.active.setText("Status : " + arrayListTopic.get(position).getUser_status());
//            holder.date.setText("Lic Expiry : " + arrayListTopic.get(position).getDl_expiry());
            holder.mobile.setText("Phone no : " + arrayListTopic.get(position).getMobile());

            String date1 = arrayListTopic.get(position).getDl_expiry();

            try {
                SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = inputDateFormat.parse(date1);

                SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String datefiormate = outputDateFormat.format(date);

                holder.date.setText("Lic Expiry : " + datefiormate);

            } catch (ParseException e) {

                holder.date.setText("Lic Expiry : " + date1);
            }

            if (arrayListTopic.get(position).getUser_image() == null) {
                Glide.with(DriverListActivity.this).load(Uri.parse("https://dfcgroup.in/crmapi/storage/app/public/profiles/no_profile.png")).error(R.drawable.no_profile).into(holder.profile);
            } else {
                Glide.with(DriverListActivity.this).load(Uri.parse("https://dfcgroup.in/crmapi/storage/app/public/profiles/" + arrayListTopic.get(position).getUser_image())).error(R.drawable.no_profile).into(holder.profile);
            }

            Log.e("TAG", "onBindViewHolder: " + arrayListTopic.get(position).getUser_image());

//            Log.e("TAG", "onBindViewHolder: "+arrayListTopic.get(position).getUser_image() );

//            Glide.with(context).load(arrayListTopic.get(position).getUser_image()).into(holder.profile);
//            String lastTripDateStr = arrayListTopic.get(position).getTrip_date();

            holder.call.setOnClickListener(v -> {

                String phoneNumber = "tel:" + "+91 " + arrayListTopic.get(position).getMobile(); // replace with the actual phone number
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber));
                startActivity(dialIntent);

            });
            holder.whatssap.setOnClickListener(v -> {

                try {
                    Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + "+91 +" + arrayListTopic.get(position).getMobile());

                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                    intent.setPackage("com.whatsapp");

                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }

        class Holder extends RecyclerView.ViewHolder {

            TextView name, mobile, date, active;
            ImageView profile, call, whatssap;

            public Holder(@NonNull View itemView) {
                super(itemView);


                name = itemView.findViewById(R.id.name);
                mobile = itemView.findViewById(R.id.mobile);
                date = itemView.findViewById(R.id.date);
                active = itemView.findViewById(R.id.Active);
                profile = itemView.findViewById(R.id.profile);
                call = itemView.findViewById(R.id.call);
                whatssap = itemView.findViewById(R.id.whatsapp);

            }
        }


    }

}