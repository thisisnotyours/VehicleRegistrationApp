package com.thisisnotyours.vehicleregistrationapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.thisisnotyours.vehicleregistrationapp.R;
import com.thisisnotyours.vehicleregistrationapp.handler.BackPressedKeyHandler;
import com.thisisnotyours.vehicleregistrationapp.handler.IOnBackPressed;
import com.thisisnotyours.vehicleregistrationapp.manager.PreferenceManager;

import java.sql.Time;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    String log = "log_";
    private Context mContext;
    private RecyclerView recycler;
    public static TextView search_car, register_car;
    private BackPressedKeyHandler backPressedKeyHandler = new BackPressedKeyHandler(this);
    private String fragType = "", loginId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(log+"saved_info_lifeCycle_", "onCreate_main");

        register_car = (TextView) findViewById(R.id.register_car);
        search_car = (TextView) findViewById(R.id.search_car);
        register_car.setOnClickListener(this);
        search_car.setOnClickListener(this);

//        String savedId = PreferenceManager.getString(mContext, "id");
//        String savedPw = PreferenceManager.getString(mContext, "pw");
//        String savedName = PreferenceManager.getString(mContext, "name");
//        Log.d(log+"saved_info", savedId+", "+savedPw+", "+savedName);

        Intent i = getIntent();
        loginId = i.getStringExtra("login_id");
//        loginId = "다시다시";
        Log.d(log+"loginId_main",loginId);

        //처음에 나오는 차량조회 화면
        Fragment fragment = null;
        fragment = new Car_Search_Fragment();
        FragmentManager manager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("login_id", loginId);
        FragmentTransaction transaction = manager.beginTransaction();
        fragment.setArguments(bundle);
        transaction.replace(R.id.frame_change, fragment);
        transaction.commit();

        search_car.setTextColor(getResources().getColor(R.color.blue));
        search_car.setBackgroundResource(R.drawable.btn_gradi_white_line);
        register_car.setTextColor(getResources().getColor(R.color.light_grey));
        register_car.setBackgroundResource(R.drawable.btn_gradi_white);

    }//onCreate..


    //me: 뒤로가기 클릭 시 앱 완전종료 해야함....!!
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_change);
        if (fragment != null) {
            ((IOnBackPressed) fragment).onBackPressed();
            backPressedKeyHandler.onBackPressed();
        }else {
//            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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