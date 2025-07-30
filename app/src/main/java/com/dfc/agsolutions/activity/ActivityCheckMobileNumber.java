package com.dfc.agsolutions.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.preference.PreferenceManager;

import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.dfc.agsolutions.app_utils.Myapplication;
import com.dfc.agsolutions.model.CheckNumberModel;
import com.dfc.agsolutions.R;
import com.google.android.material.checkbox.MaterialCheckBox;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityCheckMobileNumber extends
        AppCompatActivity {

    EditText edtMobile, edtPassword;
    MaterialCheckBox checkBox;
    TextView tvCountryCode;

    Dialog dialog;
    SharedPreferences sp;
    SharedPreferences.Editor ed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_mobile_number);

//        getPhoneNumberHint();

        edtMobile = findViewById(R.id.et_mobile);
        edtPassword = findViewById(R.id.edt_password);
        checkBox = findViewById(R.id.checkBox);
        tvCountryCode = findViewById(R.id.tvCountryCode);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();

        dialog = new Dialog(ActivityCheckMobileNumber.this);
        dialog.setContentView(R.layout.dialog_progress);
        dialog.setCancelable(false);

        findViewById(R.id.continues).setOnClickListener(v -> {
            try {

                if (checkBox.isChecked()) {
                    String mobileNumber = edtMobile.getText().toString();

                    if (mobileNumber.isEmpty()) {
                        Toast.makeText(ActivityCheckMobileNumber.this, "Please Enter Mobile Number!!", Toast.LENGTH_SHORT).show();
                    } else if (mobileNumber.trim().length() < 10) {
                        Toast.makeText(ActivityCheckMobileNumber.this, "Valid Mobile Number required!", Toast.LENGTH_SHORT).show();
                    } else /*if (edtPassword.getText().toString().isEmpty()) {
                        Toast.makeText(ActivityCheckMobileNumber.this, "Please Enter Password!!", Toast.LENGTH_SHORT).show();
                    } else if (edtPassword.getText().toString().trim().length() < 6) {
                        Toast.makeText(ActivityCheckMobileNumber.this, "Valid Password required!!", Toast.LENGTH_SHORT).show();
                    } else*/ {
                        if (Myapplication.isNetworkAvailable()) {
                            getCheckMobile();
                        } else {
                            Myapplication.noInternet(ActivityCheckMobileNumber.this);
                        }
                    }
                } else {
                    Toast.makeText(ActivityCheckMobileNumber.this, "Check Privacy policy first", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(ActivityCheckMobileNumber.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.privacy2).setOnClickListener(v -> showUpdateAppDialog());

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void showUpdateAppDialog() {

        Dialog dialog = new Dialog(this);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        layoutParams.width = -1;
        layoutParams.height = -2;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent)));
        dialog.requestWindowFeature(1);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_show_privacy_policy);
        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);

        WebView webView = dialog.findViewById(R.id.wv_web_view_privacy_policy);
        webView.getSettings().setJavaScriptEnabled(true);

        // Open links within WebView instead of the browser
        webView.setWebViewClient(new WebViewClient());

        // Load your URL
        webView.loadUrl("https://dfcgroup.in/crmapi/privacypolicy.html");

        dialog.findViewById(R.id.tv_privacy_policy_ok).setOnClickListener(view -> {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(ActivityCheckMobileNumber.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getCheckMobile() {
        try {
            dialog.show();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.commn_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Api loginService = retrofit.create(Api.class);

            String userMobileNumber = edtMobile.getText().toString();
            Call<CheckNumberModel> call = loginService.getCheckMobile(userMobileNumber);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<CheckNumberModel> call,
                                       @NonNull Response<CheckNumberModel> response) {

                    CheckNumberModel apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getCode() == 200) {

                        CheckNumberModel.UserData userData = apiResponse.getData();
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
                            ed.putString("userType", user_type_id);

                            ed.commit();

                            Intent intent = new Intent(ActivityCheckMobileNumber.this, LoginActivity.class);
                            intent.putExtra("password", edtPassword.getText().toString());
                            startActivity(intent);

                        }

                    } else {
                        Toast.makeText(ActivityCheckMobileNumber.this, "Mobile Number is Not Registered", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Call<CheckNumberModel> call,
                                      @NonNull Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(ActivityCheckMobileNumber.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(ActivityCheckMobileNumber.this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

}