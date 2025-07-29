package com.dfc.agsolutions.activity;

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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dfc.agsolutions.model.TodoListDataModel;
import com.dfc.agsolutions.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

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
    private SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    List<String> pend = new ArrayList<>();
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
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        back = (ImageView) findViewById(R.id.back);

        back.setOnClickListener(v -> finish());

        previousHistory();
        previousHistory1();
        new Handler().postDelayed(() -> setData(),1000);

    }

    SharedPreferences sp;
    SharedPreferences.Editor ed;

    private void setData() {

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        TextView tab_label = null;
        TextView totaltodo = null;
        int[] navLabels = {R.string.pendingtask, R.string.completetask
        };
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            RelativeLayout tab2 = (RelativeLayout) LayoutInflater.from(ToDoListActivity.this).inflate(R.layout.custom_tablayout, (ViewGroup) null);
            tab_label = (TextView) tab2.findViewById(R.id.text1);
            totaltodo = (TextView) tab2.findViewById(R.id.tv_total_todo);
            if(i==0) {
                tab_label.setText(navLabels[i]);
                totaltodo.setText(String.valueOf(count));
            }else {
                tab_label.setText(navLabels[i]);
                totaltodo.setText(String.valueOf(pos));
            }

            tabLayout.getTabAt(i).setCustomView(tab2);
            dialog.dismiss();
        }

    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return PendingTask.newInstance(position + 1);
                case 1:
                    return CompleteTask.newInstance(position + 1);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {

            }
            return null;
        }

        public int getItemPosition(Object item) {
            return POSITION_NONE;
        }
    }

    int pos;

    public void previousHistory() {
//        dialog.show();
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
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
        Call<TodoListDataModel> call = loginservice.get_TodoList(sp.getString("userBranch", ""), String.valueOf(1));
        call.enqueue(new Callback<TodoListDataModel>() {
            @Override
            public void onResponse(Call<TodoListDataModel> call, Response<TodoListDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {
//
                    ArrayList<TodoListDataModel> branches = response.body().getData();

                    Log.e("Respone---------", "onResponse: " + response.body().getData().size());

//                    for (TodoListDataModel branch : branches) {
//                        count+=1;
//                    }
                    count = branches.size();
//                    previousHistory1();
                    Log.e("responce..", "branches1111111111111111111111111:-  " + branches.size());

                } else {
                    Toast.makeText(ToDoListActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TodoListDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
            }
        });
    }
    public void previousHistory1() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
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
        Call<TodoListDataModel> call = loginservice.get_TodoList(sp.getString("userBranch", ""), String.valueOf(2));
        call.enqueue(new Callback<TodoListDataModel>() {
            @Override
            public void onResponse(Call<TodoListDataModel> call, Response<TodoListDataModel> response) {
                Log.e("responce..", "" + response.toString());

                if (response.body().getCode().equalsIgnoreCase("200")) {
//
                    ArrayList<TodoListDataModel> branches = response.body().getData();

                    Log.e("Respone---------", "onResponse: " + response.body().getData().size());

//                    for (TodoListDataModel branch : branches) {
//                        count+=1;
//                    }
                    pos = branches.size();
//                    setData();
                    Log.e("responce..", "branches1111111111111111111111111:-  " + branches.size());

                } else {
                    Toast.makeText(ToDoListActivity.this, "Network Error!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TodoListDataModel> call, Throwable t) {
                Log.e("sdfsd", "" + t.toString());
            }
        });
    }

}