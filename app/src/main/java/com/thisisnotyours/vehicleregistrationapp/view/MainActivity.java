package com.thisisnotyours.vehicleregistrationapp.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.thisisnotyours.vehicleregistrationapp.R;
import com.thisisnotyours.vehicleregistrationapp.handler.BackPressedKeyHandler;
import com.thisisnotyours.vehicleregistrationapp.handler.IOnBackPressed;
import com.thisisnotyours.vehicleregistrationapp.item.CarPageGubun;
import com.thisisnotyours.vehicleregistrationapp.manager.PreferenceManager;

import java.util.zip.Inflater;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    String log = "log_";
    private Context mContext;
    private RecyclerView recycler;
    public static TextView search_car, register_car, logout, appVersion, regNameText;
    private RelativeLayout menu_logout_layout;
    private TextView menu_tv_reg_name, menu_tv_reg_manager, menu_tv_logout;
    private BackPressedKeyHandler backPressedKeyHandler = new BackPressedKeyHandler(this);
    private String fragType="", loginId="";
    public String barcodeResult="";
    private static String type="";
    private TextView menuBtn;
    private DrawerLayout drawer;
    private boolean isDrawerOpen=true, isOutSideClicked=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(log+"onCreate", "main");

        mContext = this;

        findAllViewIds();

        if (!PreferenceManager.getString(mContext,"name").equals("") || PreferenceManager.getString(mContext,"name") != null) {
            regNameText.setText(PreferenceManager.getString(mContext,"name"));
            menu_tv_reg_name.setText(PreferenceManager.getString(mContext,"name"));
            if (PreferenceManager.getString(mContext, "name").equals("?????????") ||
                PreferenceManager.getString(mContext, "name").equals("?????????") ||
                PreferenceManager.getString(mContext, "name").equals("?????????") ||
                PreferenceManager.getString(mContext, "name").equals("?????????") ||
                PreferenceManager.getString(mContext, "name").equals("?????????")) {
                menu_tv_reg_manager.setText("?????????");
            }else {
                menu_tv_reg_manager.setText("??????");
                menu_tv_reg_manager.setTextColor(getResources().getColor(R.color.blue));
                menu_tv_reg_manager.setBackgroundResource(R.drawable.layout_line_light_blue);
            }
        }else {
            regNameText.setText("");
            menu_tv_reg_name.setText("");
        }

        appVersion.setText("?????????  (v"+LoginActivity.getVersionInfo(mContext)+")");

        Intent i = getIntent();
        loginId = i.getStringExtra("login_id");
        Log.d(log+"loginId_main",loginId);

        //????????? ????????? ???????????? ??????
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


    private void findAllViewIds() {
        register_car = (TextView) findViewById(R.id.register_car);
        search_car = (TextView) findViewById(R.id.search_car);
        logout = (TextView) findViewById(R.id.tv_logout);
        appVersion = (TextView) findViewById(R.id.tv_app_version);
        regNameText = (TextView) findViewById(R.id.tv_reg_name);
        menu_tv_reg_name = (TextView) findViewById(R.id.menu_tv_reg_name);
        menu_tv_reg_manager = (TextView) findViewById(R.id.menu_tv_reg_manager);
        menu_tv_logout = (TextView) findViewById(R.id.menu_tv_logout);
        menu_logout_layout = (RelativeLayout) findViewById(R.id.menu_logout_layout);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        menuBtn = (TextView) findViewById(R.id.tv_menu_btn);

        menuBtn.setOnClickListener(this);
        menu_logout_layout.setOnClickListener(this);
        register_car.setOnClickListener(this);
        search_car.setOnClickListener(this);
        logout.setOnClickListener(this);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                isDrawerOpen = true;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }


    public void tabClickCheck() {
        if (!CarPageGubun.type.equals("")) {
            if (CarPageGubun.type.equals("??????") || CarPageGubun.type.equals("??????")) {
                register_car.setText("????????????");
            }else if (CarPageGubun.type.equals("??????")) {
                register_car.setText("????????????");
            }
        }
    }


    //me: ???????????? ?????? ??? ??? ???????????? ?????????....!!
    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_change);
        if (fragment != null) {
            if (CarPageGubun.type.equals("??????") || CarPageGubun.type.equals("??????")) {
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
            case R.id.tv_menu_btn:
                if (isDrawerOpen == true) {
                    drawer.openDrawer(Gravity.LEFT);
                    //????????? ?????? ?????? ????????? ??????
                    //set drawerLayout (clickable = true) in xml file
                    isDrawerOpen = false;
                }else {
                    drawer.closeDrawer(Gravity.LEFT);
                    isDrawerOpen = true;
                }
                break;
            case R.id.menu_logout_layout:
                logout.performClick();
                break;
            case R.id.tv_logout:
                setCancelDialog("????????????");
                break;
            case R.id.search_car: //[????????????] ?????????

                if (!CarPageGubun.type.equals("")) {
                    //"??????" ???????????? ???????????? ???
                    if (CarPageGubun.type.equals("??????") || CarPageGubun.type.equals("??????")) {
                        //???????????? ????????? ???????????? - null ????????? do nothing
                        //alertdialog
                        setCancelDialog(CarPageGubun.type);
                    }else {
                        //"??????" ???????????? ???????????? ???
                        //do nothing
                    }
                }else {
                    //?????? ????????? ??? ->  CarPageGubun.type ??? ?????? ?????? ???
                    //do nothing
                    //this is for default
                    CarPageGubun.type = "??????";
                    fragment = new Car_Search_Fragment();
                    search_car.setTextColor(getResources().getColor(R.color.blue));
                    search_car.setBackgroundResource(R.drawable.btn_gradi_white_line);
                    register_car.setTextColor(getResources().getColor(R.color.light_grey));
                    register_car.setBackgroundResource(R.drawable.btn_gradi_white);
                }
                break;
            case R.id.register_car: //[????????????] ?????????
                CarPageGubun.type = "??????";
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

    //??????/?????? ?????? ???????????????
    private void setCancelDialog(String gubun) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        if (gubun.equals("??????") || gubun.equals("??????")) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(gubun+"??? ?????????????????????????");
            builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //???????????? ???????????? ??????
                    Fragment fragment_1 = null;
                    changeFragment(fragment_1);
                    Toast.makeText(mContext, gubun+" ??????", Toast.LENGTH_SHORT).show();
                }
            }).setNegativeButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //do nothing
                    //????????????
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }else if (gubun.equals("????????????")) {
            builder.setTitle("??????????????? ???????????????????");
            builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //preference ?????? ??????????????? ?????????
                    PreferenceManager.setString(mContext, "auto","false");
                    PreferenceManager.setString(mContext, "id", "");
                    PreferenceManager.setString(mContext, "pw","");
                    //????????????????????? ???????????? clear task
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }).setNegativeButton("?????????", new DialogInterface.OnClickListener() {
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

    //??????????????? ???????????? ??????
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