package com.dfc.agsolutions.Activity;

import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dfc.agsolutions.Model.ProfileModel;
import com.dfc.agsolutions.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile_fragment, container, false);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
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

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        currantHistory();

        return view;

    }

    public void currantHistory() {
        dialog.show();

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
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        Api loginservice = retrofit.create(Api.class);
        Call<ProfileModel> call = loginservice.get_profile();
        call.enqueue(new Callback<ProfileModel>() {
            @Override
            public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode() == 200) {

                    ProfileModel apiResponse = response.body();

                    ProfileModel.Profile profile = apiResponse.getData();
                    if (profile != null) {
                        String ename = profile.getFull_name();
                        String emobile = profile.getMobile();
                        String eemail = profile.getEmail();
                        String evehicle = profile.getVehicle_type();
                        String eadress = profile.getUser_address();
                        String edl_no = profile.getDl_no();
                        String edl_expire = profile.getDl_expiry();
                        String elice_no = profile.getHazard_lice_no();
                        String elice_expiry = profile.getHazard_lice_expiry();

//                        TextView name,mobile,email,vehicle,adress,dl_no,dl_expire,lice_no,lice_expir

                        Log.e("fsdfsdfsfdf","00000:-  " + profile.getDl_expiry());
                        try {

                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date inputDate = inputFormat.parse(profile.getDl_expiry());

                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");

                            String outputDateStr = outputFormat.format(inputDate);

                            dl_expire.setText(":  "+outputDateStr);

                        } catch (ParseException e) {
                            dl_expire.setText(":  "+profile.getDl_expiry());
                        }
                        try {

                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date inputDate = inputFormat.parse(profile.getHazard_lice_expiry());

                            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");

                            String outputDateStr = outputFormat.format(inputDate);

                            lice_expiry.setText(":  "+outputDateStr);

                        } catch (ParseException e) {
                            lice_expiry.setText(":  "+elice_expiry);
                        }
                        name.setText(ename);
                        mobile.setText("+91 "+emobile);
                        email.setText(eemail);
                        vehicle.setText(":  "+evehicle);
                        adress.setText(":  "+eadress);
                        dl_no.setText(":  "+edl_no);
                        lice_no.setText(":  "+elice_no);
//                        lice_expiry.setText(":  "+elice_expiry);

                        if(profile.getUser_image().equals(null)) {
//                            Glide.with(getActivity()).load(Uri.parse("https://test.dfclogistics.online/storage/app/public/profiles/no_profile.png")).into(profile_image);
                            Picasso.get().load(Uri.parse("https://test.dfclogistics.online/storage/app/public/profiles/no_profile.png")).error(R.drawable.no_profile).into(profile_image);
                        }else{
//                            Glide.with(getActivity()).load(Uri.parse("https://test.dfclogistics.online/storage/app/public/profiles/"+profile.getUser_image())).into(profile_image);
                            Picasso.get().load(Uri.parse("https://test.dfclogistics.online/storage/app/public/profiles/" +profile.getUser_image())).error(R.drawable.no_profile).into(profile_image);
                        }

                    }

                } else {
                    Toast.makeText(getActivity(), "Mobile Number is Not Registered", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<ProfileModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
            }
        });
    }
}