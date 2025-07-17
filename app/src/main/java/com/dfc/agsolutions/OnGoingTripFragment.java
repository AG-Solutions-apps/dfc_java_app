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

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dfc.agsolutions.Activity.Api;
import com.dfc.agsolutions.Model.OngoingTruckTypeModel;

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
 * Use the {@link OnGoingTripFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnGoingTripFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OnGoingTripFragment() {
        // Required empty public constructor
    }
    RecyclerView rly_shope;
    LinearLayout nodata;
    ProgressDialog dialog;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    Activity activity;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OnGoingTripFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OnGoingTripFragment newInstance(String param1, String param2) {
        OnGoingTripFragment fragment = new OnGoingTripFragment();
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
    public void updateContent(String selectedItem) {
//        get_trip(selectedItem);
//        if (textView != null) {
//            textView.setText("Selected Item: " + selectedItem);
//        }
    }

    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_on_going_trip, container, false);

        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_on_going_trip, container, false);


        swipeRefreshLayout = inflatedView.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                get_trip(mParam1,activity);
            }
        });

        activity = getActivity();
//        return inflater.inflate(R.layout.fragment_ideal, container, false);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ed = sp.edit();

//        spinnerBranches = findViewById(R.id.spinnerBranches);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
//        branchNames.clear();

        rly_shope = inflatedView.findViewById(R.id.rly_shope);
        nodata =inflatedView. findViewById(R.id.nodata);

        Log.e("branchname","mParam1:-   " +mParam1 );
        get_trip(mParam1,activity);
        return inflatedView;
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
                    .header("Authorization", "Bearer " + sp.getString("token",""))
                    .method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
//        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(activity.getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        Api loginservice = retrofit.create(Api.class);
        Call<OngoingTruckTypeModel> call = loginservice.get_vhiclelistongoing(selectedBranch,"1");
        call.enqueue(new Callback<OngoingTruckTypeModel>() {
            @Override
            public void onResponse(Call<OngoingTruckTypeModel> call, Response<OngoingTruckTypeModel> response) {
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
            public void onFailure(Call<OngoingTruckTypeModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public class Home_Today_list_Adapter extends RecyclerView.Adapter<Home_Today_list_Adapter.Holder> {
        private Activity context;

        ArrayList<OngoingTruckTypeModel> arrayListTopic;

        public Home_Today_list_Adapter(Activity context, ArrayList<OngoingTruckTypeModel> arrayListTopic) {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itam_ongoingtrip, parent, false);
            return new Home_Today_list_Adapter.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final Home_Today_list_Adapter.Holder holder, @SuppressLint("RecyclerView") final int position) {


            holder.status.setText("Status:- " + arrayListTopic.get(position).getTrip_status());
            holder.carname.setText("" + arrayListTopic.get(position).getTrip_vehicle());
            holder.loacation.setText("Destination : " + arrayListTopic.get(position).getTrip_agency());
//            holder.date.setText("Date : " + arrayListTopic.get(position).getTrip_date());
            holder.driver.setText("Driver : " + arrayListTopic.get(position).getTrip_driver());
            holder.distance.setText("Distance : " + arrayListTopic.get(position).getTrip_km() + " Km");

            try {
                String date = arrayListTopic.get(position).getTrip_date();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                // Parse the given date string
                Date givenDate = sdf.parse(date);

                SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String dateformate = outputDateFormat.format(givenDate);
                holder.date.setText("Date : " + dateformate);
            } catch (ParseException e) {
                holder.date.setText("Date : " + arrayListTopic.get(position).getTrip_date());
//                throw new RuntimeException(e);
            }


//            System.out.println("Last Trip Date: " + lastTripDateStr);
//            System.out.println("New Date (" + daysBeforeLastTrip + " days before last trip): " + formattedNewDate);



        }

        class Holder extends RecyclerView.ViewHolder {

            TextView status,loacation,date,driver,distance,carname;
//            LinearLayout click;

            public Holder(@NonNull View itemView) {
                super(itemView);


                status = itemView.findViewById(R.id.status);
                loacation = itemView.findViewById(R.id.loacation);
                date = itemView.findViewById(R.id.date);
                driver = itemView.findViewById(R.id.driver);
                distance = itemView.findViewById(R.id.distance);
                carname = itemView.findViewById(R.id.carname);

            }
        }


    }

}