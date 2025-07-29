package com.dfc.agsolutions.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.preference.PreferenceManager;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dfc.agsolutions.Model.ProfileModel;
import com.dfc.agsolutions.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileFragment extends Fragment {

    ProgressDialog dialog;
    SharedPreferences sp;

    SharedPreferences.Editor ed;

    TextView name,mobile,email,vehicle,adress,dl_no,dl_expire,lice_no,lice_expiry;

    ImageView profile_image;

    @Override
    public View onCreateView(LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile_fragment,
            container, false);

        sp = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        ed = sp.edit();

        name = view.findViewById(R.id.name);
        mobile = view.findViewById(R.id.mobile);
        email = view.findViewById(R.id.email);
        vehicle = view.findViewById(R.id.vehicle);
        adress = view.findViewById(R.id.adress);
        dl_no = view.findViewById(R.id.dl_no);
        dl_expire = view.findViewById(R.id.dl_expire);
        lice_no = view.findViewById(R.id.lice_no);
        lice_expiry = view.findViewById(R.id.lice_expiry);
        profile_image = view.findViewById(R.id.profile_image);

        dialog = new ProgressDialog(requireActivity());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        currantHistory();

        return view;

    }

    // show progress dialog
    private void showProgressDialog() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    // hide progress dialog
    private void hideProgressDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void currantHistory() {
        // show progress dialog
        showProgressDialog();

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


        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(getString(R.string.commn_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build();
        Api loginservice = retrofit.create(Api.class);
        Call<ProfileModel> call = loginservice.get_profile();
        call.enqueue(new Callback<ProfileModel>() {
            @Override
            public void onResponse(@NonNull Call<ProfileModel> call,
                @NonNull Response<ProfileModel> response) {

                ProfileModel profileModel = response.body();
                if (profileModel != null && profileModel.getCode() == 200) {

                    ProfileModel.Profile profile = profileModel.getData();
                    if (profile != null) {

                        String ename = profile.getFull_name();
                        String emobile = "+91 " + profile.getMobile();
                        String eemail = profile.getEmail();
                        String evehicle = ":  "+profile.getVehicle_type();
                        String eadress = ":  "+profile.getUser_address();
                        String edl_no = ":  "+profile.getDl_no();
//                        String edl_expire = ":  "+profile.getDl_expiry();
                        String elice_no = ":  "+profile.getHazard_lice_no();
                        String elice_expiry = profile.getHazard_lice_expiry();

//                        TextView name,mobile,email,vehicle,adress,dl_no,dl_expire,lice_no,lice_expir

                        Log.e("fsdfsdfsfdf","00000:-  " + profile.getDl_expiry());
                        try {

                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            Date inputDate = inputFormat.parse(profile.getDl_expiry());

                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

                            if (inputDate != null) {
                                String outputDateStr = ":  " + outputFormat.format(inputDate);
                                dl_expire.setText(outputDateStr);
                            }

                        } catch (ParseException e) {
                            String dlExpiry = ":  "+profile.getDl_expiry();
                            dl_expire.setText(dlExpiry);
                        }
                        try {

                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            Date inputDate = inputFormat.parse(profile.getHazard_lice_expiry());

                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

                            if (inputDate != null) {
                                String outputDateStr = outputFormat.format(inputDate);

                                String licExpiry = ":  " + outputDateStr;
                                lice_expiry.setText(licExpiry);
                            }

                        } catch (ParseException e) {
                            String eliceExpiry = ":  "+elice_expiry;
                            lice_expiry.setText(eliceExpiry);
                        }
                        name.setText(ename);

                        mobile.setText(emobile);
                        email.setText(eemail);
                        vehicle.setText(evehicle);
                        adress.setText(eadress);
                        dl_no.setText(edl_no);
                        lice_no.setText(elice_no);
//                        lice_expiry.setText(elice_expiry);

                        String userImageUrl = profile.getUser_image();
                        if(!TextUtils.isEmpty(userImageUrl)) {
                            Glide.with(requireActivity()).
                                load(Uri.parse("https://dfcgroup.in/crmapi/public/profiles/"+profile.getUser_image()))
                                .into(profile_image);
                        }

                    }

                } else {
                    Toast.makeText(requireActivity(), "Mobile Number is Not Registered", Toast.LENGTH_SHORT).show();
                }
                // hide progress dialog
                hideProgressDialog();
            }

            @Override
            public void onFailure(@NonNull Call<ProfileModel> call,
                @NonNull Throwable t) {
                // hide progress dialog
                hideProgressDialog();
            }
        });
    }

}