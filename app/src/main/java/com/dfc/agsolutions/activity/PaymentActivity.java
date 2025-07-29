package com.dfc.agsolutions.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.icu.util.Calendar;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
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

    ImageView icback;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    private Spinner spinnervoucher;
    private Spinner spinnerdebit;
    private Spinner spinnerpayment;
    EditText edtadvance, transation, edtnarration;
    TextView date, voucher_select, debit_select, payment_select, transation1;
    CardView date_cd;

    ImageView i1, i2, i3;
    String selectedvoucher;
    String selectedebit;
    String selectepayment;

    EditText tamount, km, description;

    String selectedItem, selectdate1, selectedDate;

    String Advance, Transation, narration, edate;

    ImageView creattrip;

    String voucher, debit, payment1;

    LinearLayout nr;

    String payment_voucher = null, payment_debit, payment_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();

        if (Myapplication.isNetworkAvailable()) {
            get_voucher();
        } else {
            Myapplication.noInternet(PaymentActivity.this);
        }

        icback = findViewById(R.id.iv_back);
        spinnervoucher = findViewById(R.id.spinner_voucher);
        spinnerdebit = findViewById(R.id.spinner_debit);
        spinnerpayment = findViewById(R.id.spinner_payment);
        edtadvance = findViewById(R.id.et_advance);
        transation = findViewById(R.id.et_transaction);
        edtnarration = findViewById(R.id.et_narration);
        date = findViewById(R.id.date);
        date_cd = findViewById(R.id.date_cd);
        creattrip = findViewById(R.id.ic_creat_trip);
        transation1 = findViewById(R.id.tv_transaction1);
        nr = findViewById(R.id.nr);

        icback.setOnClickListener(v -> {
            finish();
        });

        edtadvance.setKeyListener(DigitsKeyListener.getInstance("0123456789"));

        // Set InputFilter to allow only numbers
        edtadvance.setFilters(new InputFilter[]{new NumberInputFilter()});
        debitarray.add("Select Debit");
        payment_debit = "Select Debit";
        debitspinner();

        date_cd.setOnClickListener(v -> {

            date = findViewById(R.id.date);
            tamount = findViewById(R.id.et_total_amount);
            km = findViewById(R.id.km);
            description = findViewById(R.id.description);

            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create DatePickerDialog and show it
            DatePickerDialog datePickerDialog = new DatePickerDialog(PaymentActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
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

                            if (selectedYear > currentYear || (selectedYear == currentYear && selectedMonth > currentMonth) || (selectedYear == currentYear && selectedMonth == currentMonth && selectedDay > currentDay)) {
                                Toast.makeText(PaymentActivity.this, "Please select a past date", Toast.LENGTH_SHORT).show();

                            } else {
                                if (differenceInDays > 10) {
                                    Toast.makeText(PaymentActivity.this, "Please select a date within the past 10 days", Toast.LENGTH_SHORT).show();
                                } else {
                                    selectdate1 = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                                    String setselectdate1 = selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear;
//                                     String  setselectdate1 = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                                    date.setText(setselectdate1);
                                }
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

        vhicalarray.add("Select Voucher Type");
//        debitarray.add("Select Debit");
        payment.add("Select Payment Mode");
        payment.add("Cash");
        payment.add("Bank");
        payment.add("NEFT");
        payment.add("Cheque statik");

        spinnerpayment.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        spinnerpayment.setSelection(0, false);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(PaymentActivity.this, R.layout.simple_spinner_item, payment);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerpayment.setAdapter(adapter);
        spinnerpayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                selectepayment = (String) parentView.getItemAtPosition(position);
                payment_mode = selectepayment;
                Log.e("debit", "payment_mode: " + payment_mode);

                if (position > 0) {
                    selectepayment = (String) parentView.getItemAtPosition(position);
                    payment_mode = selectepayment;
                    if (selectepayment.equals("Cash")) {
                        transation1.setVisibility(View.VISIBLE);
                        transation.setVisibility(View.GONE);
                    } else {
                        transation1.setVisibility(View.GONE);
                        transation.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });


        spinnervoucher.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        spinnervoucher.setSelection(0, false);
        spinnervoucher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedvoucher = (String) parentView.getItemAtPosition(position);
                payment_voucher = selectedvoucher;
                Log.e("debit", "payment_voucher: " + payment_voucher);

                if (position > 0) {
                    selectedvoucher = (String) parentView.getItemAtPosition(position);
                    payment_voucher = selectedvoucher;
                    Log.e("selectedvoucher", "selectedvoucher: " + selectedvoucher);
                    get_Debit(selectedvoucher);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        spinnerdebit.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        spinnerdebit.setSelection(0, false);
        spinnerdebit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedebit = (String) parentView.getItemAtPosition(position);
                payment_debit = selectedebit;
                Log.e("debit", "onItemSelected: " + payment_debit);
                if (position > 0) {
                    selectedebit = (String) parentView.getItemAtPosition(position);
                    payment_debit = selectedebit;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        creattrip.setOnClickListener(v -> {

//            Advance = edtadvance.getText().toString().trim();
//            Transation = transation.getText().toString().trim();
//            narration = edtnarration.getText().toString().trim();
            try {
                if (transation.getText().toString().length() == 0) {
                    Transation = "0";
                } else {
                    Transation = transation.getText().toString();
                }
                if (edtnarration.getText().toString().length() == 0) {
                    narration = "0";
                } else {
                    narration = transation.getText().toString();
                }
                if (edtadvance.getText().toString().length() == 0) {
                    Advance = "0";
                } else {
                    Advance = edtadvance.getText().toString();
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
                    Creat_Payment();
                }
            } catch (Exception e) {
//                throw new RuntimeException(e);
            }
        });
    }

    private static class NumberInputFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            // Only allow numbers
            for (int i = start; i < end; i++) {
                if (!Character.isDigit(source.charAt(i))) {
                    return ""; // Remove the non-numeric character
                }
            }
            return null; // Accept the input
        }
    }

    List<String> vhicalarray = new ArrayList<>();
    List<String> debitarray = new ArrayList<>();
    List<String> payment = new ArrayList<>();

    String s = "";

    public void get_voucher() {
//        dialog.show();
//        vhicalarray.clear();
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
        Call<VoucherTypeDataModel> call = loginservice.get_VoucherType();
        call.enqueue(new Callback<VoucherTypeDataModel>() {
            @Override
            public void onResponse(Call<VoucherTypeDataModel> call, Response<VoucherTypeDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {
//
                    ArrayList<VoucherTypeDataModel> branches = response.body().getData();

//                  response.body().getData().get(0).getReg_no();
//                    for (ServiceFatchVhicalDataModel branch : branches) {
//                        vhicalarray.add(branch.getReg_no());
////                        vhicaldraiverarray.add(branch.getVehicle_driver());
////                        milageaaray.add(branch.getVehicle_mileage());
//                    }
                    for (VoucherTypeDataModel branch : branches) {
                        vhicalarray.add(branch.getVoucher_type());
                        Log.e("getVoucher_type", "getVoucher_type================: " + branch.getVoucher_type());
//                        s = branch.getVoucher_type();
                    }
//                    get_Debit(String.valueOf();
//


                    ArrayAdapter<String> adapter = new ArrayAdapter<>(PaymentActivity.this, R.layout.simple_spinner_item, vhicalarray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnervoucher.setAdapter(adapter);

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
                    Toast.makeText(PaymentActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
//                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<VoucherTypeDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
//                dialog.dismiss();
            }
        });
    }

    public void debitspinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(PaymentActivity.this, R.layout.simple_spinner_item, debitarray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerdebit.setAdapter(adapter);
    }


    public void get_Debit(String voucher) {
//        dialog.show();
//        vhicalarray.clear();
//        milageaaray.clear();
        Log.e("get_Debit", "get_Debit------------------------------: " + voucher);
        debitarray.clear();

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
        Call<DebitTypeDataModel> call = loginservice.get_DebitType(voucher, sp.getString("userBranch", ""));
        call.enqueue(new Callback<DebitTypeDataModel>() {
            @Override
            public void onResponse(Call<DebitTypeDataModel> call, Response<DebitTypeDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {
//
                    ArrayList<DebitTypeDataModel> branches = response.body().getData();

//response.body().getData().get(0).getReg_no();
//                    for (ServiceFatchVhicalDataModel branch : branches) {
//                        vhicalarray.add(branch.getReg_no());
////                        vhicaldraiverarray.add(branch.getVehicle_driver());
////                        milageaaray.add(branch.getVehicle_mileage());
//                    }
//                    debitarray.clear();;
                    for (DebitTypeDataModel branch : branches) {
                        debitarray.add(branch.getCommon_name());
                        Log.e("getCommon_name", "getCommon_name: " + branch.getCommon_name());
                    }
//                        Log.e("getCommon_name", "getCommon_name: "+branch.getCommon_name());
//                    ArrayAdapter<String> adapter = new ArrayAdapter<>(PaymentActivity.this, R.layout.simple_spinner_item, debitarray);
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spinnerdebit.setAdapter(adapter);

                    debitspinner();
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
                    Toast.makeText(PaymentActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
//                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<DebitTypeDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
//                dialog.dismiss();
            }
        });
    }

    public void Creat_Payment() {

//        dialog.show();
//        fullname.clear/();
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

        Log.e("TAG", "Creat_Payment-------------------------------------------: " + selectdate1 + payment_mode + payment_voucher + payment_debit + Advance + Transation + narration);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.commn_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        Api loginservice = retrofit.create(Api.class);
        Call<CreatePaymentDataModel> call = loginservice.get_CreatePayment(selectdate1, payment_mode, payment_voucher, payment_debit, Advance, sp.getString("userBranch", ""), Transation, narration);
        call.enqueue(new Callback<CreatePaymentDataModel>() {
            @Override
            public void onResponse(Call<CreatePaymentDataModel> call, Response<CreatePaymentDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {
//
//                    ArrayList<DriverListDataModel> branches = response.body().getData();
////                    for (DriverListDataModel branch : branches) {
////
                    Toast.makeText(PaymentActivity.this, "" + response.body().getMsg(), Toast.LENGTH_SHORT).show();
////                        fullname.add(branch.getFull_name());
////                        mobile.add(branch.getMobile());
////                        dl_expiry.add(branch.getDl_expiry());
////                        user_status.add(branch.getUser_status());
////                        user_image.add(branch.getUser_image());
////
////                    }

                    startActivity(new Intent(PaymentActivity.this, HomeActivity.class));

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
                    Toast.makeText(PaymentActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
//                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<CreatePaymentDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
//                dialog.dismiss();
            }
        });

    }

}