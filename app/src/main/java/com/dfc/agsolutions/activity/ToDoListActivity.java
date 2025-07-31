package com.dfc.agsolutions.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dfc.agsolutions.model.TodoListDataModel;
import com.dfc.agsolutions.R;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.Objects;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ToDoListActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ImageView back;
    ViewPager mViewPager;

    int count = 0;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        dialog.show();
        tabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewpager);
        back = findViewById(R.id.back);

        back.setOnClickListener(v -> finish());

        previousHistory();
        previousHistory1();
        new Handler().postDelayed(this::setData,1000);

    }

    SharedPreferences sp;
    SharedPreferences.Editor ed;

    private void setData() {

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        TextView tabLabel;
        TextView totalToDo;

        int[] navLabels = {R.string.pending_task, R.string.complete_task

        };
        for (int i = 0; i < tabLayout.getTabCount(); i++) {

            View tabView = LayoutInflater.from(this).inflate(R.layout.custom_tablayout,
                    new LinearLayout(this),
                    false);

            tabLabel = tabView.findViewById(R.id.text1);
            totalToDo = tabView.findViewById(R.id.tv_total_todo);
            if(i==0) {
                tabLabel.setText(navLabels[i]);
                totalToDo.setText(String.valueOf(count));
            }else {
                tabLabel.setText(navLabels[i]);
                totalToDo.setText(String.valueOf(pos));
            }

            Objects.requireNonNull(tabLayout.getTabAt(i)).setCustomView(tabView);
            dialog.dismiss();
        }

    }

    public static class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 1:
                    return CompleteTask.newInstance(position + 1);
                case 0:
                default:
                    return PendingTask.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        public int getItemPosition(@NonNull Object item) {
            return POSITION_NONE;
        }
    }

    int pos;

    public void previousHistory() {

        OkHttpClient.Builder httpClient = createHttpClient();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer " +
                            sp.getString("token", ""))
                    .method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.common_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        Api loginService = retrofit.create(Api.class);

        Call<TodoListDataModel> call = loginService.getToDoList(sp.getString("userBranch", ""),
                String.valueOf(1));

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TodoListDataModel> call,
                                   @NonNull Response<TodoListDataModel> response) {

                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {
//
                    ArrayList<TodoListDataModel> branches = response.body().getData();

                    Log.e("Response---------", "onResponse: " + response.body().getData().size());

                    count = branches.size();

                    Log.e("response..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(ToDoListActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TodoListDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("TodoListDataModel", "" + t);
            }

        });
    }

    private OkHttpClient.Builder createHttpClient() {
        return new OkHttpClient.Builder();
    }

    public void previousHistory1() {

        OkHttpClient.Builder httpClient = createHttpClient();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer " + sp.getString("token", ""))
                    .method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.common_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        Api loginService = retrofit.create(Api.class);

        Call<TodoListDataModel> call = loginService.getToDoList(sp.getString("userBranch", ""),
                String.valueOf(2));

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TodoListDataModel> call,
                                   @NonNull Response<TodoListDataModel> response) {
                Log.e("response..", "" + response);

                assert response.body() != null;
                if (response.body().getCode().equalsIgnoreCase("200")) {
//
                    ArrayList<TodoListDataModel> branches = response.body().getData();

                    Log.e("Response---------", "onResponse: " + response.body().getData().size());

                    pos = branches.size();

                    Log.e("response..", "branches:-  " + branches.size());

                } else {
                    Toast.makeText(ToDoListActivity.this,
                            "Network Error!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TodoListDataModel> call,
                                  @NonNull Throwable t) {
                Log.e("TodoListDataModel", " " + t);
            }

        });
    }

}