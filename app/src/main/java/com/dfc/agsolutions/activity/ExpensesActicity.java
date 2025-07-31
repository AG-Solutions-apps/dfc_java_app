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

    ImageView iv_back;

    RecyclerView rv;
    SharedPreferences sp;
    SharedPreferences.Editor ed;

    String totalAmount;
    String totalReceived;

    ProgressDialog dialog;

    TextView tv_total_amount;
    SwipeRefreshLayout swipeRefreshLayout;
    LottieAnimationView lav_no_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_activity);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        lav_no_data = findViewById(R.id.lav_no_data);
        rv = findViewById(R.id.rv);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            lav_no_data.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
            expensesList();
        });

        dialog = new ProgressDialog(ExpensesActicity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        iv_back = findViewById(R.id.iv_back);
        tv_total_amount = findViewById(R.id.tv_total_amount);
        iv_back.setOnClickListener(v -> finish());
        expensesList();
    }

    public void expensesList() {

        dialog.show();

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
                .baseUrl(getString(R.string.common_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        Api loginService = retrofit.create(Api.class);
        Call<ExpensesListDataModel> call = loginService.get_ExpensesList(sp.getString("userBranch", ""));

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ExpensesListDataModel> call,
                                   @NonNull Response<ExpensesListDataModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    ArrayList<ExpensesListDataModel> branches = response.body().getData();
                    totalAmount = response.body().getTotalExpensive();
                    totalReceived = response.body().getTotalReceived();

                    Log.e("amtCheck","totalAmount:-  " + totalAmount);
                    Log.e("amtCheck","totalReceived:- " + totalReceived);

                    try {
                        int totalAmounts = Integer.parseInt(totalAmount);
                        int total_rc = Integer.parseInt(totalReceived);

                        int finalAmt = total_rc -totalAmounts ;

                        String totalAmount = "  ₹ " + finalAmt + " ";
                        tv_total_amount.setText(totalAmount);

                    } catch (NumberFormatException e) {
                        Log.e("Error: ", e.toString());
                    }

                    lav_no_data.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                    HomeTodayListAdapter adapter = new HomeTodayListAdapter(response.body().getData());
                    rv.setAdapter(adapter);

                    Log.e("response..", "branches:-  " + branches.size());

                } else {
                    lav_no_data.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
                    Toast.makeText(ExpensesActicity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onFailure(@NonNull Call<ExpensesListDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("ExpensesListDataModel: ", "" + t);
                dialog.dismiss();
                lav_no_data.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

            }
        });

    }

    public static class HomeTodayListAdapter extends
            RecyclerView.Adapter<HomeTodayListAdapter.Holder> {

        ArrayList<ExpensesListDataModel> data;

        public HomeTodayListAdapter(ArrayList<ExpensesListDataModel> data) {
            this.data = data;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @NonNull
        @Override
        public HomeTodayListAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expenses, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final HomeTodayListAdapter.Holder holder,
                                     @SuppressLint("RecyclerView") final int position) {

            try {
                String money = "₹ " + data.get(position).getPayment_details_amount();
                holder.tv_money.setText(money);
                holder.tv_voucher.setText(data.get(position).getPayment_details_voucher_type());
                holder.tv_debit.setText(data.get(position).getPayment_details_debit());

                String date1 = data.get(position).getPayment_details_date();

                try {
                    SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = inputDateFormat.parse(date1);

                    SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    String dateFormated = null;
                    if (date != null) {
                        dateFormated = outputDateFormat.format(date);
                    }

                    String formatedDate = "Date : "+dateFormated;
                    holder.tv_date.setText(formatedDate);
                } catch (ParseException e) {
                    String date = "Date : "+date1;
                    holder.tv_date.setText(date);

                }
            } catch (Exception e) {
                Log.e("TAG", "onBindViewHolder: " + e);
                String money = "₹ " + data.get(position).getPayment_details_amount();
                holder.tv_money.setText(money);
                String voucher = "Voucher : "+data.get(position).getPayment_details_voucher_type();
                holder.tv_voucher.setText(voucher);
                String debit = "Debit : "+data.get(position).getPayment_details_debit();
                holder.tv_debit.setText(debit);
            }

        }

        static class Holder extends RecyclerView.ViewHolder {

            TextView tv_date, tv_money, tv_voucher, tv_debit;

            public Holder(@NonNull View itemView) {
                super(itemView);

                tv_date = itemView.findViewById(R.id.tv_date);
                tv_money = itemView.findViewById(R.id.money);
                tv_voucher = itemView.findViewById(R.id.voucher);
                tv_debit = itemView.findViewById(R.id.debit);

            }
        }

    }

}