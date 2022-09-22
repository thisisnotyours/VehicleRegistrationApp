package com.thisisnotyours.vehicleregistrationapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.thisisnotyours.vehicleregistrationapp.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    String log = "log_";
    String lifeCycle_ = "lifeCycle_";
    private Context mContext;
    private RecyclerView recycler;
    public static TextView search_car, register_car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(lifeCycle_, "onCreate");

        //처음에 나오는 차량조회 화면
        Fragment fragment = null;
        fragment = new Car_Search_Fragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_change, fragment);
        transaction.commit();

        register_car = (TextView) findViewById(R.id.register_car);
        search_car = (TextView) findViewById(R.id.search_car);
        register_car.setOnClickListener(this);
        search_car.setOnClickListener(this);
    }//onCreate..

    //me: 뒤로가기 두른 클릭 시 앱 완전종료 해야함....!!
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(lifeCycle_, "onResume");
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;

        switch (v.getId()) {
            case R.id.search_car: //차량조회
                fragment = new Car_Search_Fragment();
                search_car.setTextColor(getResources().getColor(R.color.blue));
                search_car.setBackgroundResource(R.drawable.btn_gradi_white_line);
                register_car.setTextColor(getResources().getColor(R.color.light_grey));
                register_car.setBackgroundResource(R.drawable.btn_gradi_white);
                break;
            case R.id.register_car: //차량등록
                fragment = new Car_Registration_Fragment();
                register_car.setTextColor(getResources().getColor(R.color.blue));
                register_car.setBackgroundResource(R.drawable.btn_gradi_white_line);
                search_car.setTextColor(getResources().getColor(R.color.light_grey));
                search_car.setBackgroundResource(R.drawable.btn_gradi_white);
                break;
        }

        if (fragment != null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.frame_change, fragment);
            transaction.commit();
        }
    }



}