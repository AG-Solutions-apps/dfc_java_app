package com.dfc.agsolutions.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.dfc.agsolutions.AppUtils.Myapplication;
import com.dfc.agsolutions.Model.MyResponseData;
import com.dfc.agsolutions.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityLogin extends
        AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor ed;

    TextView mobileNo;
    private FirebaseAuth mAuth;
    private OtpTextView otpTextView;

    private View loadingOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mobileNo = findViewById(R.id.mobileNo);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();

        loadingOverlay = findViewById(R.id.loading_overlay);

        mobileNo.setText(sp.getString("mobile", ""));
        if (!Myapplication.isNetworkAvailable()) {
            Myapplication.noInternet(ActivityLogin.this);
        }
        mAuth = FirebaseAuth.getInstance();

        sendVerificationCode("+91" + sp.getString("mobile", ""));

        otpTextView = findViewById(R.id.otp_view);
        otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                // fired when user types something in the OtpBox
            }

            @Override
            public void onOTPComplete(String otp) {
                // fired when user has entered the OTP fully.
                verifyCode(otp);
            }
        });
        
        findViewById(R.id.cvVerifyOtp).setOnClickListener(v -> {

            if (otpTextView.getOTP().isEmpty()) {
                Toast.makeText(ActivityLogin.this,
                        R.string.please_enter_otp_first,
                        Toast.LENGTH_SHORT).show();
            } else {
                showProgressDialog();
                verifyCode(otpTextView.getOTP());
            }

        });

    }

    // show progress dialog
    private void showProgressDialog() {
        if (!loadingOverlay.isShown()) {
            loadingOverlay.setVisibility(View.VISIBLE);
        }
    }

    // hide progress dialog
    private void hideProgressDialog() {
        if (loadingOverlay.isShown()) {
            loadingOverlay.setVisibility(View.GONE);
        }
    }

    private String verificationId;

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s,
                               @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {

            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            hideProgressDialog();
            Log.e("authCheck", "verificationId:-  " + verificationId);

        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            Log.e("authCheck", "code:-  " + code);
            if (code != null) {
                otpTextView.setOTP(code);
//                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(ActivityLogin.this,
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Log.e("taskCheck", "task successfully");

                        hideProgressDialog();
                        getLogin();

                    } else {
                        hideProgressDialog();
                        Log.e("taskCheck", "task failed");
                        Toast.makeText(ActivityLogin.this,
                                Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                });
    }

    private void sendVerificationCode(String number) {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    public void getLogin() {
        showProgressDialog();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api loginService = retrofit.create(Api.class);
        Call<MyResponseData> call = loginService.get_login(sp.getString("mobile", ""),
                sp.getString("password", ""));

        call.enqueue(new Callback<MyResponseData>() {
            @Override
            public void onResponse(@NonNull Call<MyResponseData> call,
                                   @NonNull Response<MyResponseData> response) {
                Log.e("response..", "" + response);

                if (response.isSuccessful()) {
                    MyResponseData responseData = response.body();
                    if (responseData != null) {
                        // Access the data from the response
                        String token = responseData.getData().getToken();
                        ed.putString("token", token);
                        ed.putBoolean("firstTime", true);
                        ed.commit();

                        startActivity(new Intent(ActivityLogin.this,
                                ActivityHome.class));

                    }
                }

                hideProgressDialog();

            }

            @Override
            public void onFailure(@NonNull Call<MyResponseData> call,
                                  @NonNull Throwable t) {
                Log.e("Error: ", "" + t);
                hideProgressDialog();
            }

        });

    }

}