package com.dfc.agsolutions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dfc.agsolutions.activity.Api;
import com.dfc.agsolutions.model.TruckTypeModel;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link IdealFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IdealFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public IdealFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IdealFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IdealFragment newInstance(String param1, String param2) {
        IdealFragment fragment = new IdealFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    RecyclerView rly_shope;
    LinearLayout nodata;
    ProgressDialog dialog;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    Activity activity;

    //    View inflater;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View inflatedView = inflater.inflate(R.layout.fragment_idle, container, false);
        activity = getActivity();
//        return inflater.inflate(R.layout.fragment_ideal, container, false);

        swipeRefreshLayout = inflatedView.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                get_trip(mParam1, activity);
            }
        });

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ed = sp.edit();

//        spinnerBranches = findViewById(R.id.spinnerBranches);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
//        branchNames.clear();

        rly_shope = inflatedView.findViewById(R.id.rv_shop);
        nodata = inflatedView.findViewById(R.id.lav_no_data);

        Log.e("branchname", "mParam1:-   " + mParam1);
        get_trip(mParam1, activity);
        return inflatedView;
    }

    public void updateContent(String selectedItem, Activity activity) {
        get_trip(selectedItem, activity);
//        if (textView != null) {
//            textView.setText("Selected Item: " + selectedItem);
//        }
    }

    public void get_trip(String selectedBranch, Activity activity) {
//        ProgressDialog  dialog = new ProgressDialog(activity);
//        dialog.setMessage("Loading...");
//        dialog.setCancelable(false);
        dialog.show();
//
//        sp = PreferenceManager.getDefaultSharedPreferences(activity);
//        ed = sp.edit();

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
                .baseUrl(activity.getString(R.string.commn_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        Api loginservice = retrofit.create(Api.class);
        Call<TruckTypeModel> call = loginservice.get_vhiclelist(selectedBranch, "2");
        call.enqueue(new Callback<TruckTypeModel>() {
            @Override
            public void onResponse(Call<TruckTypeModel> call, Response<TruckTypeModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {

                    if (response.body().getData().size() == 0) {
                        nodata.setVisibility(View.VISIBLE);
                        rly_shope.setVisibility(View.GONE);
                    } else {
                        nodata.setVisibility(View.GONE);
                        rly_shope.setVisibility(View.VISIBLE);
                        Home_Today_list_Adapter home_today_list_adapter = new Home_Today_list_Adapter(getActivity(), response.body().getData());
                        rly_shope.setAdapter(home_today_list_adapter);
                        rly_shope.setItemAnimator(new DefaultItemAnimator());
                        rly_shope.setHasFixedSize(true);
                    }

                } else {
                    Toast.makeText(getActivity(), "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<TruckTypeModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    public class Home_Today_list_Adapter extends RecyclerView.Adapter<Home_Today_list_Adapter.Holder> {
        private Activity context;

        ArrayList<TruckTypeModel> arrayListTopic;

        public Home_Today_list_Adapter(Activity context, ArrayList<TruckTypeModel> arrayListTopic) {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itam_ideal_vhical, parent, false);
            return new Home_Today_list_Adapter.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final Home_Today_list_Adapter.Holder holder, @SuppressLint("RecyclerView") final int position) {


            holder.status.setText("Status:- " + arrayListTopic.get(position).getVehicle_status());
            holder.v_number.setText("" + arrayListTopic.get(position).getReg_no());
//            String lastTripDateStr = arrayListTopic.get(position).getTrip_date();
//Log.e("branchname","lastTripDateStr:- " +lastTripDateStr );

            try {
                if (arrayListTopic.get(position).getTrip_date().equals("")) {
                    holder.tripdate.setText("-" + " / " + "0" + "days");

                } else {

                    long daysDifference;
                    try {
                        String givenDateString = arrayListTopic.get(position).getTrip_date();

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                        // Parse the given date string
                        Date givenDate = sdf.parse(givenDateString);

                        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        String dateformate = outputDateFormat.format(givenDate);

                        // Get the current date
                        Date currentDate = new Date();

                        // Calculate the difference in milliseconds
                        long timeDifference = currentDate.getTime() - givenDate.getTime();

                        // Convert milliseconds to days
                        daysDifference = timeDifference / (24 * 60 * 60 * 1000);

                        holder.tripdate.setText(dateformate + " / " + daysDifference + " days");

                        System.out.println("Days difference between " + givenDateString + " and today: " + daysDifference + " days");
                    } catch (ParseException e) {
                        //                e.printStackTrace();
                        holder.tripdate.setText(arrayListTopic.get(position).getTrip_date() + " / " + "0" + "days");

                    }
                    //            System.out.println("Last Trip Date: " + lastTripDateStr);
                    //            System.out.println("New Date (" + daysBeforeLastTrip + " days before last trip): " + formattedNewDate);

                }
            } catch (Exception e) {
//                throw new RuntimeException(e);
            }


        }

        class Holder extends RecyclerView.ViewHolder {

            TextView status, v_number, tripdate;
//            LinearLayout click;

            public Holder(@NonNull View itemView) {
                super(itemView);


                status = itemView.findViewById(R.id.status);
                v_number = itemView.findViewById(R.id.v_number);
                tripdate = itemView.findViewById(R.id.tv_trip_date);

            }
        }


    }


}