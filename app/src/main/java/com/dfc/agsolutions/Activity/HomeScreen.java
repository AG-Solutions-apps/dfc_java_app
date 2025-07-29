package com.dfc.agsolutions.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.dfc.agsolutions.Model.Branch;
import com.dfc.agsolutions.Model.DeletModel;
import com.dfc.agsolutions.Model.ResponseArrayModel;
import com.dfc.agsolutions.Model.ResponseTodoCount;
import com.dfc.agsolutions.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeScreen extends Fragment {

    TextView branchname, branchnameadmin, username;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    BottomSheetDialog bottomSheetDialog;
    public String user_type;
    ImageView creattrip;
    LinearLayout mainview;
    LottieAnimationView comingsun;
    ImageView logout;
    TextView totaltodo;
    LinearLayout vhivlelivk;

    ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_home_screen, container, false);

        username = view.findViewById(R.id.username);
        totaltodo = view.findViewById(R.id.totaltodo);

        vhivlelivk = view.findViewById(R.id.vhivlelivk);

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home_screen);
        sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        ed = sp.edit();
        branchname = view.findViewById(R.id.branchname);
        branchnameadmin = view.findViewById(R.id.branchnameadmin);
        creattrip = view.findViewById(R.id.creattrip);
        mainview = view.findViewById(R.id.mainview);
        comingsun = view.findViewById(R.id.comingsun);

        String branchName = sp.getString("userBranch", "");
        branchname.setText(branchName);

        String branchAdmin = sp.getString("userBranch", "");
        branchnameadmin.setText(branchAdmin);

        logout = view.findViewById(R.id.logout);
        Log.e("TAG", "get_branch: +++ " + sp.getString("token", ""));
        username.setText(sp.getString("fullName", ""));
        user_type = sp.getString("userType", "");

        Log.e("user_types", "user_type:-- " + user_type);

        if (user_type.equals("1")) {
            mainview.setVisibility(View.GONE);
            comingsun.setVisibility(View.VISIBLE);
            branchnameadmin.setVisibility(View.GONE);
            creattrip.setVisibility(View.GONE);
            branchname.setVisibility(View.VISIBLE);
        } else if (user_type.equals("2")) {
            mainview.setVisibility(View.VISIBLE);
            branchnameadmin.setVisibility(View.VISIBLE);
            branchname.setVisibility(View.GONE);
            creattrip.setVisibility(View.GONE);
            comingsun.setVisibility(View.GONE);
            get_branch();
        } else {
            mainview.setVisibility(View.VISIBLE);
            comingsun.setVisibility(View.GONE);
            branchnameadmin.setVisibility(View.GONE);
            branchname.setVisibility(View.VISIBLE);
            creattrip.setVisibility(View.VISIBLE);
        }

        get_todotask();

        branchnameadmin.setOnClickListener(v -> bottomSheetDialog.show());

        logout.setOnClickListener(v -> logOutUser());
//        branchname.setText("" + sp.getString("user_type",""));

        view.findViewById(R.id.vhivlelivk).setOnClickListener(v -> {

            if (user_type.equals("1")) {
                startActivity(new Intent(getActivity(), TripActivity.class));

            } else if (user_type.equals("2")) {
                startActivity(new Intent(getActivity(), AdminTripActivity.class));

            } else {

                startActivity(new Intent(getActivity(), AdminTripActivity.class));

            }
        });

        view.findViewById(R.id.payment).setOnClickListener(v ->
            startActivity(new Intent(getActivity(), PaymentActivity.class).putExtra("header", "Payment")));

        view.findViewById(R.id.allvhicle).setOnClickListener(v ->
            startActivity(new Intent(getActivity(), Allvhicale_list_activity.class).putExtra("header", "Payment")));

        view.findViewById(R.id.expence).setOnClickListener(v ->
            startActivity(new Intent(getActivity(), ExpensesActicity.class).putExtra("header", "Expenses")));

        view.findViewById(R.id.driver).setOnClickListener(v ->
            startActivity(new Intent(getActivity(), DriverListActivity.class).putExtra("header", "Driver")));

        view.findViewById(R.id.service).setOnClickListener(v ->
            startActivity(new Intent(getActivity(), VehicleServiceActivity.class).putExtra("header", "Service")));

        view.findViewById(R.id.todolis).setOnClickListener(v ->
            startActivity(new Intent(getActivity(), ToDoListActivity.class).putExtra("header", "To Do list")));

        view.findViewById(R.id.creattrip).setOnClickListener(v ->
            startActivity(new Intent(getActivity(), CreatTrip.class).putExtra("header", "To Do list")));

        return view;
    }

    public void get_branch() {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
//        dialog.show();

        Log.e("dialog", "dialog1: ");

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
        Call<ResponseArrayModel> call = loginservice.get_branch();
        call.enqueue(new Callback<ResponseArrayModel>() {
            @Override
            public void onResponse(@NonNull Call<ResponseArrayModel> call,
                @NonNull Response<ResponseArrayModel> response) {

//                Log.e("responce..", "" + response.toString());

                ResponseArrayModel responseArrayModel = response.body();
                if (responseArrayModel != null && responseArrayModel.getCode().equalsIgnoreCase("200")) {

                    List<Branch> branches = responseArrayModel.getData();
                    countryDialog(branches);


//                    setupSpinner(branchNames);
                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(getActivity(), "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                Log.e("dismiss", "dismiss3: ");


            }

            @Override
            public void onFailure(@NonNull Call<ResponseArrayModel> call,
                @NonNull Throwable t) {
//                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
                Log.e("dismiss", "dismiss3: ");
            }
        });
    }

    public void get_todotask() {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();
        Log.e("dialog", "dialog2: ");

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
        Call<ResponseTodoCount> call = loginservice.get_gettodocount(sp.getString("userBranch", ""));
        call.enqueue(new Callback<ResponseTodoCount>() {
            @Override
            public void onResponse(@NonNull Call<ResponseTodoCount> call,
                @NonNull Response<ResponseTodoCount> response) {
//                Log.e("responce..", "" + response.toString());

                ResponseTodoCount responseTodoCount = response.body();
                if (responseTodoCount != null && responseTodoCount.getCode().equalsIgnoreCase("200")) {


                    totaltodo.setText(responseTodoCount.getData());
//
//                    List<Branch> branches = response.body().getData();
//                    countryDialog(branches);


//                    setupSpinner(branchNames);
//                    Log.e("responce..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(getActivity(), "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                Log.e("dismiss", "dismiss1: ");

            }

            @Override
            public void onFailure(@NonNull Call<ResponseTodoCount> call,
                @NonNull Throwable t) {
//                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
                Log.e("dismiss", "dismiss11: ");
            }
        });
    }

    private void countryDialog(List<Branch> branches) {
//        vhivlelivk.
        try {
            if (isAdded()) { // Check if the fragment is added to the activity
                bottomSheetDialog = new BottomSheetDialog(requireActivity(), R.style.SheetDialog);
                bottomSheetDialog.setContentView(R.layout.branch_bottom_sheet_dialog);

                RecyclerView rvCountry = bottomSheetDialog.findViewById(R.id.rvCountry);

                if (getActivity() != null & rvCountry != null) { // Check if getActivity() returns a non-null value
                    rvCountry.setLayoutManager(new LinearLayoutManager(getActivity()));
                    rvCountry.setHasFixedSize(true);

                    Home_Today_list_Adapter home_today_list_adapter = new Home_Today_list_Adapter(getActivity(), branches);
                    rvCountry.setAdapter(home_today_list_adapter);
                }
            } else {
                // Handle the case when the fragment is not attached to the activity
                // You might want to log an error or handle it in another way
            }
        } catch (java.lang.IllegalStateException e) {
            e.printStackTrace();
            Log.e("TAG", "countryDialog: "+e.getMessage() );
        }

//        try {
//            bottomSheetDialog = new BottomSheetDialog(requireActivity(), R.style.SheetDialog);
//            bottomSheetDialog.setContentView(R.layout.branch_bottom_sheet_dialog);
//
//            RecyclerView rvCountry = bottomSheetDialog.findViewById(R.id.rvCountry);
//
//
//            rvCountry.setLayoutManager(new LinearLayoutManager(getActivity()));
//            rvCountry.setHasFixedSize(true);
//            Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
//
//
//            Home_Today_list_Adapter home_today_list_adapter = new Home_Today_list_Adapter(getActivity(), branches);
//            rvCountry.setAdapter(home_today_list_adapter);
//        }
//        catch (java.lang.IllegalStateException e) {
//            Toast.makeText(requireActivity(), "e", Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        }

    }

//        LinearLayout copy = bottomSheetDialog.findViewById(R.id.copyLinearLayout);




    private void logOutUser() {

//        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity(), R.style.SheetDialog);
//        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
//        bottomSheetDialog.show();

//        TextView logout = bottomSheetDialog.findViewById(R.id.logout);
//        if (logout != null) {
//            logout.setOnClickListener(v -> {
                ed.clear();
                ed.commit();
                startActivity(new Intent(getActivity(), ActivityCheckMobileNumber.class));
                requireActivity().finish();
//            });
//        }

//        TextView delete = bottomSheetDialog.findViewById(R.id.tvDeleteMyAccountAction);
//        if (delete != null) {
//            delete.setOnClickListener(v -> deleteUserAccount());
//        }

    }

    public void deleteUserAccount() {
//        ProgressDialog  dialog = new ProgressDialog(activity);
//        dialog.setMessage("Loading...");
//        dialog.setCancelable(false);
        dialog.show();
        Log.e("dialog", "dialog2: " );
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
            .baseUrl(getString(R.string.commn_url))
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build();
        Api loginservice = retrofit.create(Api.class);
        Call<DeletModel> call = loginservice.get_deleteaccount();
        call.enqueue(new Callback<DeletModel>() {
            @Override
            public void onResponse(@NonNull Call<DeletModel> call,
                @NonNull Response<DeletModel> response) {
//                Log.e("responce..", "" + response.toString());

                DeletModel deletModel = response.body();
                if (deletModel != null && deletModel.getCode().equalsIgnoreCase("200")) {
                    ed.clear();
                    ed.commit();
                    Toast.makeText(getActivity(), "Your Account is Deleted!!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(getActivity(), ActivityIntroScreen.class));
                    requireActivity().finish();

                } else {
                    Toast.makeText(getActivity(), "Network Error!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                Log.e("dismiss", "dismiss2: " );


            }

            @Override
            public void onFailure(@NonNull Call<DeletModel> call, @NonNull Throwable t) {
                Log.e("sdfsd", "" + t.toString());
                dialog.dismiss();
                Log.e("dismiss", "dismiss22: " );

            }
        });
    }

//    @Override
//    public void onBackPressed() {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle(R.string.app_name);
//        builder.setIcon(R.mipmap.ic_launcher);
//        builder.setMessage("Do you want to exit?")
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        getActivity().finishAffinity();
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//        AlertDialog alert = builder.create();
//        alert.show();
//
//    }

    public class Home_Today_list_Adapter extends RecyclerView.Adapter<Home_Today_list_Adapter.Holder> {
        private Activity context;

        List<Branch> arrayListTopic;

        public Home_Today_list_Adapter(Activity context, List<Branch> arrayListTopic) {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itam_branch, parent, false);
            return new Home_Today_list_Adapter.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final Home_Today_list_Adapter.Holder holder, @SuppressLint("RecyclerView") final int position) {

            String branchName = arrayListTopic.get(position).getBranchName();
            holder.status.setText(branchName);

            holder.click.setOnClickListener(v -> {
                ed.putString("userBranch", arrayListTopic.get(position).getBranchName());
                ed.commit();
                String branchUserName = sp.getString("userBranch", "");
                branchnameadmin.setText(branchUserName);
                bottomSheetDialog.dismiss();
            });

        }

        class Holder extends RecyclerView.ViewHolder {

            TextView status;
            LinearLayout click;

            public Holder(@NonNull View itemView) {
                super(itemView);
                status = itemView.findViewById(R.id.status);
                click = itemView.findViewById(R.id.click);

            }
        }

    }

}