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
import com.thisisnotyours.vehicleregistrationapp.manager.PreferenceManager;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    String log = "log_";
    private Context mContext;
    private RecyclerView recycler;
    public static TextView search_car, register_car, logout, appVersion, regNameText;
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
        logout = (TextView) findViewById(R.id.tv_logout);
        appVersion = (TextView) findViewById(R.id.tv_app_version);
        regNameText = (TextView) findViewById(R.id.tv_reg_name);
        if (!PreferenceManager.getString(mContext,"name").equals("") || PreferenceManager.getString(mContext,"name") != null) {
            regNameText.setText(PreferenceManager.getString(mContext,"name"));
        }else {
            regNameText.setText("");
        }

        appVersion.setText("(v"+LoginActivity.getVersionInfo(mContext)+")");

        register_car.setOnClickListener(this);
        search_car.setOnClickListener(this);
        logout.setOnClickListener(this);

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
            if (CarPageGubun.type.equals("조회") || CarPageGubun.type.equals("등록")) {
                register_car.setText("차량등록");
            }else if (CarPageGubun.type.equals("수정")) {
                register_car.setText("차량수정");
            }
        }else {
            //do nothing
            //this is for default
        }
//        return;
    }


    //me: 뒤로가기 클릭 시 앱 완전종료 해야함....!!
    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_change);
        if (fragment != null) {
            if (CarPageGubun.type.equals("등록") || CarPageGubun.type.equals("수정")) {
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
            case R.id.tv_logout:
                setCancelDialog("로그아웃");
                break;
            case R.id.search_car: //[차량조회] 탭버튼

                if (!CarPageGubun.type.equals("")) {
                    //"등록" 상태에서 조회누를 때
                    if (CarPageGubun.type.equals("등록") || CarPageGubun.type.equals("수정")) {
                        //데이터의 개수를 확인하고 - null 아니면 do nothing
                        //alertdialog
                        setCancelDialog(CarPageGubun.type);
                    }else {
                        //"조회" 상태에서 조회누를 때
                        //do nothing
                    }
                }else {
                    //처음 시작할 때 ->  CarPageGubun.type 에 값이 없을 때
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
            case R.id.register_car: //[차량등록] 탭버튼
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

    private void setCancelDialog(String gubun) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        if (gubun.equals("등록") || gubun.equals("수정")) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(gubun+"을 취소하시겠습니까?");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //차량조회 화면으로 이동
                    Fragment fragment_1 = null;
                    changeFragment(fragment_1);
                    Toast.makeText(mContext, gubun+" 취소", Toast.LENGTH_SHORT).show();
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
        }else if (gubun.equals("로그아웃")) {
            builder.setTitle("로그아웃을 하시겠습니까?");
            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //preference 에서 로그인정보 초기화
                    PreferenceManager.setString(mContext, "auto","false");
                    PreferenceManager.setString(mContext, "id", "");
                    PreferenceManager.setString(mContext, "pw","");
                    //로그인화면으로 이동하며 clear task
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //do nothing
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }else {
            //do nothing
        }


    }

    private void changeFragment(Fragment fragment) { //차량조회//화면으로 이동
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