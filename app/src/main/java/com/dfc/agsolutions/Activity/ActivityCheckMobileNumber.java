package com.dfc.agsolutions.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import com.dfc.agsolutions.AppUtils.Myapplication;
import com.dfc.agsolutions.Model.CheckNomberModel;
import com.dfc.agsolutions.R;
import com.google.android.material.checkbox.MaterialCheckBox;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityCheckMobileNumber extends
        AppCompatActivity {

    EditText edtMobile;
    ProgressDialog dialog;
    SharedPreferences sp;
    SharedPreferences.Editor ed;

    MaterialCheckBox checkBox;

    //    TextView privacy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_mobile_number);

        edtMobile = findViewById(R.id.edtMobile);
        checkBox = findViewById(R.id.checkBox);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();

        dialog = new ProgressDialog(ActivityCheckMobileNumber.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        findViewById(R.id.cvVerifyOtp).setOnClickListener(v -> {

            if (checkBox.isChecked()) {

                if (edtMobile.getText().toString().isEmpty()) {
                    Toast.makeText(ActivityCheckMobileNumber.this,
                            R.string.please_enter_mobile_number,
                            Toast.LENGTH_SHORT).show();
                } else if (edtMobile.getText().toString().trim().length() < 10) {
                    Toast.makeText(ActivityCheckMobileNumber.this,
                            R.string.valid_mobile_number_required,
                            Toast.LENGTH_SHORT).show();
                } else {

                    if (Myapplication.isNetworkAvailable()) {
                        getCheckMobile();
                    } else {
                        Myapplication.noInternet(ActivityCheckMobileNumber.this);
                    }

                }

            } else {
                Toast.makeText(ActivityCheckMobileNumber.this, R.string.chack_privacy,
                        Toast.LENGTH_SHORT).show();
            }

        });

        // privacy policy click
        findViewById(R.id.privacy).setOnClickListener(this::onClick);

    }


    @SuppressLint("SetJavaScriptEnabled")
    private void showUpdateAppDialog() {

        Dialog dialog = new Dialog(this);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();

        if (window == null) return;
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = -1;
        layoutParams.height = -2;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this,
                android.R.color.transparent)));
        dialog.requestWindowFeature(1);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_update_app);
        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
        WebView webView = dialog.findViewById(R.id.webwiew);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // Load a URL
        webView.loadUrl("https://dfcgroup.in/crmapi/privacypolicy.html");

        dialog.findViewById(R.id.ln_try_again).setOnClickListener(view -> {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                Log.e("Error: ", e.toString());
            }
        });

    }

    public void getCheckMobile() {

        dialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api loginService = retrofit.create(Api.class);
        Call<CheckNomberModel> call = loginService.get_check_mobail(edtMobile.getText().toString());

        call.enqueue(new Callback<CheckNomberModel>() {
            @Override
            public void onResponse(@NonNull Call<CheckNomberModel> call,
                                   @NonNull Response<CheckNomberModel> response) {

                Log.e("response..", "" + response);

                CheckNomberModel apiResponse = response.body();

                if (apiResponse != null && apiResponse.getCode() == 200) {

                    CheckNomberModel.UserData userData = apiResponse.getData();

                    if (userData != null) {

                        String mobileNumber = userData.getMobile();
                        String userBranch = userData.getUser_branch();
                        String cPassword = userData.getCpassword();
                        String fullName = userData.getFull_name();
                        String user_type_id = userData.getUser_type_id();

                        ed.putString("mobile", mobileNumber);
                        ed.putString("userBranch", userBranch);
                        ed.putString("password", cPassword);
                        ed.putString("fullName", fullName);
                        ed.putString("user_type", user_type_id);

                        ed.commit();

                        // Handle the data as needed
                        Log.e("mobileCheck", "getMobileNumber:-  " + mobileNumber);
                        Toast.makeText(ActivityCheckMobileNumber.this,
                                R.string.mobile_number_is_active,
                                Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(ActivityCheckMobileNumber.this,
                                ActivityLogin.class));

                    }

                } else {
                    Toast.makeText(ActivityCheckMobileNumber.this,
                            R.string.mobile_number_is_not_registered,
                            Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<CheckNomberModel> call,
                                  @NonNull Throwable t) {
                Log.e("Error: ", "" + t);
                dialog.dismiss();
            }

        });
    }

    private void onClick(View v) {
        showUpdateAppDialog();
    }

}