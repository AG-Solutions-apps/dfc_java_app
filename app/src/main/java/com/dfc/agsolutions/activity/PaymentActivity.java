package com.dfc.agsolutions.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.dfc.agsolutions.app_utils.Myapplication;
import com.dfc.agsolutions.model.CreatePaymentDataModel;
import com.dfc.agsolutions.model.DebitTypeDataModel;
import com.dfc.agsolutions.model.VoucherTypeDataModel;
import com.dfc.agsolutions.R;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentActivity extends AppCompatActivity {

    ImageView iv_back;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    private Spinner spinnerVoucher;
    private Spinner spinner_debit;
    EditText edt_advance, edt_transaction, edt_narration;
    TextView tv_date, tv_transaction1;
    CardView date_cd;

    String selectedVoucher;
    String selectedDebit;
    String selectedPayment;

    EditText et_amount, et_km, et_description;

    String selectDate1;

    String advance_, transaction_, narration;

    ImageView iv_create_trip;

    LinearLayout nr;

    String payment_voucher = null, payment_debit, payment_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();

        if (Myapplication.isNetworkAvailable()) {
            getVoucher();
        } else {
            Myapplication.noInternet(PaymentActivity.this);
        }

        iv_back = findViewById(R.id.iv_back);
        spinnerVoucher = findViewById(R.id.spinner_voucher);
        spinner_debit = findViewById(R.id.spinner_debit);
        Spinner spinnerPayment = findViewById(R.id.spinner_payment);
        edt_advance = findViewById(R.id.et_advance);
        edt_transaction = findViewById(R.id.et_transaction);
        edt_narration = findViewById(R.id.et_narration);
        tv_date = findViewById(R.id.date);
        date_cd = findViewById(R.id.date_cd);
        iv_create_trip = findViewById(R.id.ic_creat_trip);
        tv_transaction1 = findViewById(R.id.tv_transaction1);
        nr = findViewById(R.id.nr);

        iv_back.setOnClickListener(this::onClick);

        edt_advance.setKeyListener(DigitsKeyListener.getInstance("0123456789"));

        // Set InputFilter to allow only numbers
        edt_advance.setFilters(new InputFilter[]{new NumberInputFilter()});
        debitArray.add("Select Debit");
        payment_debit = "Select Debit";
        debitSpinner();

        date_cd.setOnClickListener(v -> {

            tv_date = findViewById(R.id.date);
            et_amount = findViewById(R.id.et_total_amount);
            et_km = findViewById(R.id.km);
            et_description = findViewById(R.id.description);

            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create DatePickerDialog and show it
            DatePickerDialog datePickerDialog = new DatePickerDialog(PaymentActivity.this,
                    (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                        Calendar currentDate = Calendar.getInstance();
                        int currentYear = currentDate.get(Calendar.YEAR);
                        int currentMonth = currentDate.get(Calendar.MONTH);
                        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);

                        // Create a Calendar object for the selected date
                        Calendar selectedDate = Calendar.getInstance();
//                                selectedDate.set(selectedDay, selectedMonth, selectedYear);

                        selectedDate.set(selectedYear, selectedMonth, selectedDay);

                        // Calculate the difference in days
                        long differenceInMillis = currentDate.getTimeInMillis() - selectedDate.getTimeInMillis();
                        long differenceInDays = TimeUnit.MILLISECONDS.toDays(differenceInMillis);

                        if (selectedYear > currentYear ||
                                (selectedYear == currentYear && selectedMonth > currentMonth) ||
                                (selectedYear == currentYear && selectedMonth == currentMonth && selectedDay > currentDay)) {
                            Toast.makeText(PaymentActivity.this,
                                    "Please select a past date",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            if (differenceInDays > 10) {
                                Toast.makeText(PaymentActivity.this,
                                        "Please select a date within the past 10 days",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                selectDate1 = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                                String setSelectDate1 = selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear;

                                tv_date.setText(setSelectDate1);
                            }
                        }

                    }, year, month, day);

            Calendar minDateCalendar = Calendar.getInstance();
            minDateCalendar.add(Calendar.DAY_OF_MONTH, -10);
            long minDateMillis = minDateCalendar.getTimeInMillis();

            // Set the minimum date
            datePickerDialog.getDatePicker().setMinDate(minDateMillis);

            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        vehicleArray.add("Select Voucher Type");

        payment.add("Select Payment Mode");
        payment.add("Cash");
        payment.add("Bank");
        payment.add("NEFT");
        payment.add("Cheque statik");

        spinnerPayment.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.white),
                PorterDuff.Mode.SRC_ATOP);
        spinnerPayment.setSelection(0, false);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(PaymentActivity.this, R.layout.simple_spinner_item, payment);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPayment.setAdapter(adapter);

        spinnerPayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                selectedPayment = (String) parentView.getItemAtPosition(position);
                payment_mode = selectedPayment;
                Log.e("debit", "payment_mode: " + payment_mode);

                if (position > 0) {
                    selectedPayment = (String) parentView.getItemAtPosition(position);
                    payment_mode = selectedPayment;
                    if (selectedPayment.equals("Cash")) {
                        tv_transaction1.setVisibility(View.VISIBLE);
                        edt_transaction.setVisibility(View.GONE);
                    } else {
                        tv_transaction1.setVisibility(View.GONE);
                        edt_transaction.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        spinnerVoucher.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.white),
                PorterDuff.Mode.SRC_ATOP);
        spinnerVoucher.setSelection(0, false);
        spinnerVoucher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedVoucher = (String) parentView.getItemAtPosition(position);
                payment_voucher = selectedVoucher;
                Log.e("debit", "payment_voucher: " + payment_voucher);

                if (position > 0) {
                    selectedVoucher = (String) parentView.getItemAtPosition(position);
                    payment_voucher = selectedVoucher;
                    Log.e("selectedVoucher", "selectedVoucher: " + selectedVoucher);
                    get_Debit(selectedVoucher);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        spinner_debit.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.white),
                PorterDuff.Mode.SRC_ATOP);

        spinner_debit.setSelection(0, false);
        spinner_debit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView,
                                       int position,
                                       long id) {
                selectedDebit = (String) parentView.getItemAtPosition(position);
                payment_debit = selectedDebit;
                Log.e("debit", "onItemSelected: " + payment_debit);
                if (position > 0) {
                    selectedDebit = (String) parentView.getItemAtPosition(position);
                    payment_debit = selectedDebit;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        iv_create_trip.setOnClickListener(v -> {

            try {
                if (edt_transaction.getText().toString().isEmpty()) {
                    transaction_ = "0";
                } else {
                    transaction_ = edt_transaction.getText().toString();
                }
                if (edt_narration.getText().toString().isEmpty()) {
                    narration = "0";
                } else {
                    narration = edt_transaction.getText().toString();
                }
                if (edt_advance.getText().toString().isEmpty()) {
                    advance_ = "0";
                } else {
                    advance_ = edt_advance.getText().toString();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            try {
                if (payment_voucher.equals("Select Voucher Type")) {
                    Toast.makeText(this, "Please Select Voucher Type", Toast.LENGTH_SHORT).show();
                } else if (payment_debit.equals("Select Debit")) {
                    Toast.makeText(this, "Please Select Debit", Toast.LENGTH_SHORT).show();
                } else if (payment_mode.equals("Select Payment Mode")) {
                    Toast.makeText(this, "Please Select Payment Mode", Toast.LENGTH_SHORT).show();
                } else {
                    createPayment();
                }
            } catch (Exception e) {
                Log.e("Error: ", e.toString());
            }
        });
    }

    private void onClick(View v) {
        finish();
    }

    private static class NumberInputFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source,
                                   int start,
                                   int end,
                                   Spanned dest,
                                   int d_start,
                                   int d_end) {
            // Only allow numbers
            for (int i = start; i < end; i++) {
                if (!Character.isDigit(source.charAt(i))) {
                    return ""; // Remove the non-numeric character
                }
            }
            return null; // Accept the input
        }
    }

    List<String> vehicleArray = new ArrayList<>();
    List<String> debitArray = new ArrayList<>();
    List<String> payment = new ArrayList<>();

    public void getVoucher() {

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

        Call<VoucherTypeDataModel> call = loginService.getVoucherType();

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<VoucherTypeDataModel> call,
                                   @NonNull Response<VoucherTypeDataModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    ArrayList<VoucherTypeDataModel> branches = response.body().getData();

                    for (VoucherTypeDataModel branch : branches) {
                        vehicleArray.add(branch.getVoucher_type());
                        Log.e("getVoucher_type", "getVoucher_type================: " + branch.getVoucher_type());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(PaymentActivity.this, R.layout.simple_spinner_item, vehicleArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerVoucher.setAdapter(adapter);

                    Log.e("response..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(PaymentActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<VoucherTypeDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("VoucherTypeDataModel: ", " " + t);
            }
        });
    }

    public void debitSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(PaymentActivity.this,
                R.layout.simple_spinner_item,
                debitArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_debit.setAdapter(adapter);
    }


    public void get_Debit(String voucher) {

        Log.e("get_Debit", "get_Debit------------------------------: " + voucher);
        debitArray.clear();

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

        Call<DebitTypeDataModel> call = loginService.getDebitType(voucher,
                sp.getString("userBranch", ""));

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<DebitTypeDataModel> call,
                                   @NonNull Response<DebitTypeDataModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    ArrayList<DebitTypeDataModel> branches = response.body().getData();

                    for (DebitTypeDataModel branch : branches) {
                        debitArray.add(branch.getCommon_name());
                        Log.e("getCommon_name", "getCommon_name: " + branch.getCommon_name());
                    }

                    debitSpinner();

                    Log.e("response..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(PaymentActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<DebitTypeDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("DebitTypeDataModel :", " " + t);
            }
        });
    }

    public void createPayment() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer " + sp.getString("token", ""))
                    .method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        Log.e("TAG", "Creat_Payment-------------------------------------------: " + selectDate1 + payment_mode + payment_voucher + payment_debit + advance_ + transaction_ + narration);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.commn_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        Api loginService = retrofit.create(Api.class);

        Call<CreatePaymentDataModel> call = loginService.createPayment(selectDate1,
                payment_mode,
                payment_voucher,
                payment_debit,
                advance_,
                sp.getString("userBranch", ""),
                transaction_,
                narration);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<CreatePaymentDataModel> call,
                                   @NonNull Response<CreatePaymentDataModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {

                    Toast.makeText(PaymentActivity.this,
                            " " + response.body().getMsg(),
                            Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(PaymentActivity.this,
                            HomeActivity.class));

                } else {
                    Toast.makeText(PaymentActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<CreatePaymentDataModel> call, @NonNull Throwable t) {
                Log.e("CreatePaymentDataModel: ", "" + t);
            }
        });

    }

}