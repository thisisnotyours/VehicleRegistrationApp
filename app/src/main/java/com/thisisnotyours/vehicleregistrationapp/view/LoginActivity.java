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
import com.thisisnotyours.vehicleregistrationapp.retrofit.RetrofitAPI;
import com.thisisnotyours.vehicleregistrationapp.retrofit.RetrofitHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private String log = "log_";
    private EditText loginIdEdit, logInPwEdit;
    private CheckBox checkBox;
    private Button loginBtn;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;

        loginIdEdit = findViewById(R.id.et_login_id); //아이디
        logInPwEdit = findViewById(R.id.et_login_pw); //비밀번호
        checkBox = findViewById(R.id.checkbox); //자동로그인 체크박스
        loginBtn = findViewById(R.id.btn_login);

        loginBtn.setOnClickListener(this);



        //저장된 로그인정보 있는지 확인
        pref = getSharedPreferences("auto_login", Activity.MODE_PRIVATE);
        String autoId = pref.getString("id","");
        String autoPw = pref.getString("pw","");
        Log.d(log+"autoInfo_find", autoId+", "+autoPw);

        if (autoId.equals("") && autoPw.equals("")) {

        }else {
            loginIdEdit.setText(autoId);
            logInPwEdit.setText(autoPw);
            loginBtn.performClick();
        }


        //자동로그인 체크박스
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (loginIdEdit.getText().toString().equals("") && logInPwEdit.getText().toString().equals("")) {
                    Toast.makeText(mContext, "아이디와 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    checkBox.setChecked(false);
                }else {
                    if (b == true) {
                        pref = getSharedPreferences("auto_login", Activity.MODE_PRIVATE);
                        editor = pref.edit();
                        editor.putString("id", loginIdEdit.getText().toString());
                        editor.putString("pw", logInPwEdit.getText().toString());
                        editor.commit();
                        Log.d(log+"autoInfo_save", pref.getString("id","")+", "+pref.getString("pw",""));
                    }else {
                        pref = getSharedPreferences("auto_login", Activity.MODE_PRIVATE);
                        editor = pref.edit();
                        editor.putString("id", "");
                        editor.putString("pw", "");
                        editor.commit();
                        Log.d(log+"autoInfo_save", pref.getString("id","")+", "+pref.getString("pw",""));
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:  //[로그인]버튼

                checkLoginInfoData();

                break;
        }
    }


    //로그인정보 데이터 서버 fetching
    private void checkLoginInfoData() {
        if (loginIdEdit.getText().toString().equals("") || logInPwEdit.getText().toString().equals("")) {
            Toast.makeText(mContext, "아이디와 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
        }else {
            Retrofit retrofit = RetrofitHelper.getRetrofitInstance();
            RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
            Call<String> call = retrofitAPI.getLoginData(loginIdEdit.getText().toString(), logInPwEdit.getText().toString());

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.d(log+"response", response.body());
                    Log.d(log+"response", response.toString());

                    if (response.isSuccessful()) {
                        String str = response.body();
                        Log.d(log+"response_str", str.toString());

                        if (response.body().equals("Y")) {
                            Intent i = new Intent(mContext, MainActivity.class);
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
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "서버와 연결이 원할하지 않습니다", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }//checkLoginInfoData..



}