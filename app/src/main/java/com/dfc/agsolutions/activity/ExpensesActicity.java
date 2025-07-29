package com.dfc.agsolutions.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
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
import com.dfc.agsolutions.model.ExpensesListDataModel;
import com.dfc.agsolutions.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExpensesActicity extends AppCompatActivity {

    ImageView icback;

    RecyclerView rv;
    SharedPreferences sp;
    SharedPreferences.Editor ed;

    String totalAmount;
    String totalReceived;

    ProgressDialog dialog;

    TextView totalamount;
    SwipeRefreshLayout swipeRefreshLayout;
    LottieAnimationView nodata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_activity);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        nodata = findViewById(R.id.lav_no_data);
        rv = findViewById(R.id.rv);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            nodata.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
            Expenses_List();
        });

        dialog = new ProgressDialog(ExpensesActicity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        icback = findViewById(R.id.iv_back);
        totalamount = findViewById(R.id.tv_total_amount);
        icback.setOnClickListener(v -> {
            finish();
        });
        Expenses_List();
    }

    public void Expenses_List() {

        dialog.show();
//        fullname.clear();
//        mobile.clear();
//        dl_expiry.clear();
//        user_status.clear();
//        user_image.clear();
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
        Call<ExpensesListDataModel> call = loginservice.get_ExpensesList(sp.getString("userBranch", ""));
        call.enqueue(new Callback<ExpensesListDataModel>() {
            @Override
            public void onResponse(Call<ExpensesListDataModel> call, Response<ExpensesListDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {
//
                    ArrayList<ExpensesListDataModel> branches = response.body().getData();
                    totalAmount = response.body().getTotalExpensive();
                    totalReceived = response.body().getTotalReceived();

                    Log.e("amtcheck","totalAmount:-  " + totalAmount);
                    Log.e("amtcheck","totalReceived:- " + totalReceived);

                    try {
                        int totalamounts = Integer.parseInt(totalAmount);
                        int totalrc = Integer.parseInt(totalReceived);


                        int fainalamt = totalrc -totalamounts ;

                        totalamount.setText("  \u20B9 " + fainalamt + " ");


//                        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
//                            totalamount.setText("" + Html.fromHtml("<b>Balance:</b>") + fainalamt);
//                        } else {
//                            // Use HtmlCompat.fromHtml() for Nougat (API 24) and above
//                            totalamount.setText("" +  HtmlCompat.fromHtml("<b>Balance:</b>", HtmlCompat.FROM_HTML_MODE_LEGACY) +fainalamt );
//                        }

                    } catch (NumberFormatException e) {
//                        throw new RuntimeException(e);
                    }

//                    ArrayList<ExpensesListDataModel> branches1 = response.body().getTotalAmount();
//                    ArrayList<ExpensesListDataModel> branches2 = response.body().getTotalReceived();
//                    for (ExpensesListDataModel branch : branches) {
//
//                        fullname.add(branch.getFull_name());
//                        mobile.add(branch.getMobile());
//                        dl_expiry.add(branch.getDl_expiry());

//                        user_status.add(branch.getUser_status());
//                        user_image.add(branch.getUser_image());
//
//                    }
                    nodata.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                    Home_Today_list_Adapter adapter = new Home_Today_list_Adapter(ExpensesActicity.this, response.body().getData());
                    rv.setAdapter(adapter);

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
                    rv.setVisibility(View.GONE);
                    Toast.makeText(ExpensesActicity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);


            }

            @Override
            public void onFailure(Call<ExpensesListDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
                nodata.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

            }
        });

    }

    public class Home_Today_list_Adapter extends RecyclerView.Adapter<Home_Today_list_Adapter.Holder> {
        private ExpensesActicity context;

        ArrayList<ExpensesListDataModel> data;

        public Home_Today_list_Adapter(ExpensesActicity context, ArrayList<ExpensesListDataModel> data) {
            this.context = context;
            this.data = data;
        }

//        public Home_Today_list_Adapter(ExpensesActicity context, ArrayList<ExpensesListDataModel> data) {
//
//        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @NonNull
        @Override
        public Home_Today_list_Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expenses, parent, false);
            return new Home_Today_list_Adapter.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final Home_Today_list_Adapter.Holder holder, @SuppressLint("RecyclerView") final int position) {


//            holder.date.setText(data.get(position).getPayment_details_date());
            try {
                holder.money.setText("\u20B9 " + data.get(position).getPayment_details_amount());
                holder.voucher.setText(data.get(position).getPayment_details_voucher_type());
                holder.debit.setText(data.get(position).getPayment_details_debit());

                String date1 = data.get(position).getPayment_details_date();

                try {
                    SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = inputDateFormat.parse(date1);

                    SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    String datefiormate = outputDateFormat.format(date);

                    holder.date.setText("Date : "+datefiormate);
                } catch (ParseException e) {
                    holder.date.setText("Date : "+date1);

                }
            } catch (Exception e) {
//                throw new RuntimeException(e);
                holder.money.setText("\u20B9 " + data.get(position).getPayment_details_amount());
                holder.voucher.setText("Voucher : "+data.get(position).getPayment_details_voucher_type());
                holder.debit.setText("Debit : "+data.get(position).getPayment_details_debit());
            }

//            holder.profile.set(arrayListTopic.get(position).getDl_expiry());
//            Glide.with(context).load(arrayListTopic.get(position).getUser_image()).into(holder.profile);
//            String lastTripDateStr = arrayListTopic.get(position).getTrip_date();


//            if (arrayListTopic.get(position).getTrip_date().equals("")) {
//                holder.tripdate.setText("-" + " / " + "0" + "days");
//
//            } else {
//
//                long daysDifference;
//                try {
//                    String givenDateString = arrayListTopic.get(position).getTrip_date();
//
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//
//                    // Parse the given date string
//                    Date givenDate = sdf.parse(givenDateString);
//
//                    // Get the current date
//                    Date currentDate = new Date();
//
//                    // Calculate the difference in milliseconds
//                    long timeDifference = currentDate.getTime() - givenDate.getTime();
//
//                    // Convert milliseconds to days
//                    daysDifference = timeDifference / (24 * 60 * 60 * 1000);
//                    holder.tripdate.setText(arrayListTopic.get(position).getTrip_date() + " / " + daysDifference + " days");
//
//
//
//                    System.out.println("Days difference between " + givenDateString + " and today: " + daysDifference + " days");
//
//
//                } catch (ParseException e) {
////                e.printStackTrace();
//                    holder.tripdate.setText(arrayListTopic.get(position).getTrip_date() + " / " + "0" + "days");
//
//                }
////            System.out.println("Last Trip Date: " + lastTripDateStr);
////            System.out.println("New Date (" + daysBeforeLastTrip + " days before last trip): " + formattedNewDate);
//
//            }


        }

        class Holder extends RecyclerView.ViewHolder {

            TextView date, money, voucher, debit;
//            ImageView profile;

            public Holder(@NonNull View itemView) {
                super(itemView);

                date = itemView.findViewById(R.id.date);
                money = itemView.findViewById(R.id.money);
                voucher = itemView.findViewById(R.id.voucher);
                debit = itemView.findViewById(R.id.debit);

            }
        }


    }
}