package com.thisisnotyours.vehicleregistrationapp.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.thisisnotyours.vehicleregistrationapp.R;
import com.thisisnotyours.vehicleregistrationapp.handler.BackPressedKeyHandler;
import com.thisisnotyours.vehicleregistrationapp.handler.IOnBackPressed;
import com.thisisnotyours.vehicleregistrationapp.item.CarPageGubun;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    String log = "log_";
    private Context mContext;
    private RecyclerView recycler;
    public static TextView search_car, register_car;
    private BackPressedKeyHandler backPressedKeyHandler = new BackPressedKeyHandler(this);
    private String fragType = "", loginId="";
    public String barcodeResult="";
    private static String type="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(log+"onCreate", "main");

        mContext = this;

        register_car = (TextView) findViewById(R.id.register_car);
        search_car = (TextView) findViewById(R.id.search_car);
        register_car.setOnClickListener(this);
        search_car.setOnClickListener(this);

        Intent i = getIntent();
        loginId = i.getStringExtra("login_id");
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


    public void tabClickCheck() {
        if (!CarPageGubun.type.equals("")) {
            if (CarPageGubun.type.equals("등록")) {
                //데이터의 개수를 확인하고 - null 아니면 do nothing
//                if ("if ketdatas not null") {
//                    search_car.setClickable(false);
//                }
                Toast.makeText(mContext, "???", Toast.LENGTH_SHORT).show();
                search_car.setClickable(false);
            }
        }else {
            //do nothing
            //this is for default
        }
    }


    //me: 뒤로가기 클릭 시 앱 완전종료 해야함....!!
    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_change);
        if (fragment != null) {
            if (CarPageGubun.type.equals("등록")) {
                ((IOnBackPressed) fragment).onBackPressed();
            }else {
                Log.d(log+"back","111");
                backPressedKeyHandler.onBackPressed();
            }
        }else {
            Log.d(log+"back","222");
            backPressedKeyHandler.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            barcodeResult = scanResult.getContents();
            Log.d(log+"getBarcode=", requestCode+", "+resultCode+", "+data);
            Log.d(log+"getBarcodeResult=", barcodeResult.toString());
            getBarcodeResult();
        }
    }

    public String getBarcodeResult() {
        Log.d(log+"barcodeResult", barcodeResult);
        Car_Registration_Fragment frg = new Car_Registration_Fragment();
        frg.etUnitSn = findViewById(R.id.et_unit_sn);
        frg.etUnitSn.setText(barcodeResult);

        return barcodeResult;
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
                if (!CarPageGubun.type.equals("")) {
                    if (CarPageGubun.type.equals("등록")) {
                        //데이터의 개수를 확인하고 - null 아니면 do nothing
                        //alertdialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("등록을 취소하시겠습니까?");
                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //차량조회 화면으로 이동
                                Fragment fragment_1 = null;
                                changeFragment(fragment_1);
                                Toast.makeText(mContext, "등록 취소", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //do nothing
                                //이동안함
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }else {
                        //"조회" 상태에서 조회누를 때
                        //do nothing
                    }
                }else {
                    //do nothing
                    //this is for default
                    CarPageGubun.type = "조회";
                    fragment = new Car_Search_Fragment();
                    search_car.setTextColor(getResources().getColor(R.color.blue));
                    search_car.setBackgroundResource(R.drawable.btn_gradi_white_line);
                    register_car.setTextColor(getResources().getColor(R.color.light_grey));
                    register_car.setBackgroundResource(R.drawable.btn_gradi_white);
                }
                break;
            case R.id.register_car: //차량등록
                CarPageGubun.type = "등록";
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

    private void changeFragment(Fragment fragment) {
        fragment = null;
        fragment = new Car_Search_Fragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_change, fragment);
        transaction.commit();
        MainActivity.search_car.setTextColor(getResources().getColor(R.color.blue));
        MainActivity.search_car.setBackgroundResource(R.drawable.btn_gradi_white_line);
        MainActivity.register_car.setTextColor(getResources().getColor(R.color.light_grey));
        MainActivity.register_car.setBackgroundResource(R.drawable.btn_gradi_white);
    }



}