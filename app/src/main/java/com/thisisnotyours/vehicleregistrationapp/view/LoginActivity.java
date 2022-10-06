package com.thisisnotyours.vehicleregistrationapp.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.thisisnotyours.vehicleregistrationapp.R;
import com.thisisnotyours.vehicleregistrationapp.manager.PreferenceManager;
import com.thisisnotyours.vehicleregistrationapp.retrofit.RetrofitAPI;
import com.thisisnotyours.vehicleregistrationapp.retrofit.RetrofitHelper;
import com.thisisnotyours.vehicleregistrationapp.vo.CarInfoListData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private String log="log_", savedId="", savedPw="", savedName="";
    private EditText id_et, pw_et;
    private CheckBox checkBox;
    private Button loginBtn;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private boolean autoClicked = false;
    private PreferenceManager preferenceManager = new PreferenceManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;

        id_et = findViewById(R.id.et_login_id); //아이디
        pw_et = findViewById(R.id.et_login_pw); //비밀번호
        checkBox = findViewById(R.id.checkbox); //자동로그인 체크박스
        loginBtn = findViewById(R.id.btn_login);

        loginBtn.setOnClickListener(this);


        //저장된 로그인정보 있는지 확인
        savedId = PreferenceManager.getString(mContext, "id");
        savedPw = PreferenceManager.getString(mContext, "pw");
        savedName = PreferenceManager.getString(mContext, "name");
        Log.d(log+"saved_info", savedId+", "+savedPw+", "+savedName);


        if (savedId.equals("") && savedPw.equals("")) {
            //저장된 로그인정보 없으면 - do nothing
            checkBox.setChecked(false);
        }else {
            //있으면 로그인버튼 자동클릭
            id_et.setText(savedId);
            pw_et.setText(savedPw);
            checkBox.setChecked(true);
            loginBtn.performClick();
        }



        //자동로그인 체크박스
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b == true) {
                    autoClicked = true; //자동로그인 선택
                }else {
                    autoClicked = false; //자동로그인 선택취소
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:  //[로그인]버튼
                getLoginInfoData();   //로그인 정보
                break;
        }
    }


    //로그인 정보 서버 fetching
    private void getLoginInfoData() {
        if (id_et.getText().toString().equals("") || pw_et.getText().toString().equals("")) {
            Toast.makeText(mContext, "아이디와 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();

        }else {
            Retrofit retrofit = RetrofitHelper.getRetrofitInstance();
            RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
            Call<CarInfoListData> call = retrofitAPI.getLoginData(id_et.getText().toString(),pw_et.getText().toString());

            call.enqueue(new Callback<CarInfoListData>() {
                @Override
                public void onResponse(Call<CarInfoListData> call, Response<CarInfoListData> response) {
                    Log.d(log+"response", response.body().toString());
                    Log.d(log+"response", response.toString());

                    if (response.isSuccessful()) {
                        try {
                            CarInfoListData str = response.body();
                            Log.d(log+"response_str", str.toString());

                            if (response.body().getUserInfoVOS().get(0).getUse_yn().equals("Y")) {
                                if (autoClicked == true) {
                                    PreferenceManager.setString(mContext, "id", str.getUserInfoVOS().get(0).getId());
                                    PreferenceManager.setString(mContext, "pw", str.getUserInfoVOS().get(0).getPw());
                                    PreferenceManager.setString(mContext, "name", str.getUserInfoVOS().get(0).getName());
                                }else {
                                    PreferenceManager.setString(mContext, "id", "");
                                    PreferenceManager.setString(mContext, "pw", "");
                                    PreferenceManager.setString(mContext, "name", "");
                                }
                                Intent i = new Intent(mContext, MainActivity.class);
                                i.putExtra("login_id", str.getUserInfoVOS().get(0).getId());
                                startActivity(i);

                            }else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                builder.setTitle("로그인 정보가 일치하지 않습니다.\n\n다시 입력해주세요.");
                                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(Call<CarInfoListData> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "다시 시도해주세요", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }



}