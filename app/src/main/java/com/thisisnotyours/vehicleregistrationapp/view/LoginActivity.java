package com.thisisnotyours.vehicleregistrationapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thisisnotyours.vehicleregistrationapp.R;
import com.thisisnotyours.vehicleregistrationapp.handler.BackPressedKeyHandler;
import com.thisisnotyours.vehicleregistrationapp.manager.PreferenceManager;
import com.thisisnotyours.vehicleregistrationapp.retrofit.RetrofitAPI;
import com.thisisnotyours.vehicleregistrationapp.retrofit.RetrofitHelper;
import com.thisisnotyours.vehicleregistrationapp.vo.CarInfoListData;

import java.util.EventListener;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private BackPressedKeyHandler backPressedKeyHandler = new BackPressedKeyHandler(this);
    private Context mContext;
    private String log="log_", savedId="", savedPw="", savedName="", savedAuto="false";
    private EditText id_et, pw_et;
    private LinearLayout refresh_layout;
    private TextView tvAppVersion;
    private CheckBox checkBox;
    private Button loginBtn;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private boolean autoClicked = false;
    private PreferenceManager preferenceManager = new PreferenceManager();
    private boolean idKeyboardOn = false, pwKeyboardOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;

        id_et = findViewById(R.id.et_login_id); //아이디
        pw_et = findViewById(R.id.et_login_pw); //비밀번호
        checkBox = findViewById(R.id.checkbox); //자동로그인 체크박스
        loginBtn = findViewById(R.id.btn_login);
        tvAppVersion = findViewById(R.id.tv_app_version);  //앱버전
        String version = getVersionInfo(getApplicationContext());
        tvAppVersion.setText("앱 버전(v"+version+")");

        loginBtn.setOnClickListener(this);

        refresh_layout = findViewById(R.id.refresh_layout);
        refresh_layout.setOnClickListener(this);

        //저장된 로그인정보 있는지 확인
        savedAuto = PreferenceManager.getString(mContext, "auto");
        savedId = PreferenceManager.getString(mContext, "id");
        savedPw = PreferenceManager.getString(mContext, "pw");
        savedName = PreferenceManager.getString(mContext, "name");
        Log.d(log+"saved_info", savedId+", "+savedPw+", "+savedName);


        if (savedAuto.equals("false")) {
            Log.d(log+"login","got no login information");
            //저장된 로그인정보 없으면 - do nothing
            checkBox.setChecked(false);
            id_et.requestFocus();
            bringKeyboard(1650);
        }else if (savedAuto.equals("true")) {
            Log.d(log+"login","login info existed. auto login");
            //있으면 로그인버튼 자동클릭
            id_et.setText(savedId);
            pw_et.setText(savedPw);
            checkBox.setChecked(true);
            loginBtn.performClick();
        }


        //로그인 editText
        id_et.setOnEditorActionListener((textView, i, keyEvent) -> {
            boolean handled = false;
            if (i == EditorInfo.IME_ACTION_DONE) {
                handled = true;
                id_et.clearFocus();
                pw_et.requestFocus();
            }
            return handled;
        });
        //비밀번호 editText
        pw_et.setOnEditorActionListener((textView, i, keyEvent) -> {
            boolean handled = false;
            if (i == EditorInfo.IME_ACTION_DONE) {
                handled = true;
                pw_et.clearFocus();
                downKeyboard(pw_et);
            }
            return handled;
        });


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

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

    }//onCreate..

    public String getVersionInfo(Context context) {
        String version = null;
        try {
            PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
            version = i.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    //앱 비정상종료 감지 handler
    class ExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
            throwable.printStackTrace();
            Log.d(log+"on_exception","비정상 종료");
        }
    }

    //뒤로가기 버튼
    @Override
    public void onBackPressed() {
        backPressedKeyHandler.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:  //[로그인]버튼
                getLoginInfoData();   //로그인 정보
                break;
            case R.id.refresh_layout:  //로그인 아이디, 비번 새로고침
                id_et.setText("");
                pw_et.setText("");
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(log+"onStart","onStart");
        if (id_et.hasFocus() == true) {
            Log.d(log+"which_editText","ID");
            downKeyboardForced(id_et);
        }else if (pw_et.hasFocus() == true) {
            Log.d(log+"which_editText","PW");
            downKeyboardForced(pw_et);
        }
    }


    public void bringKeyboard(long speed) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        upKeyboard(id_et);  //키보드 올리기
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, speed);
            }
        });
        thread.start();
    }

    public void upKeyboard(EditText editText) {
        if (editText != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText,InputMethodManager.SHOW_FORCED);
        }
    }

    public void downKeyboard(EditText editText) {
        if (editText != null) {
            InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    public void downKeyboardForced(EditText editText) {
        if (editText != null) {
            InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    //로그인 정보 서버 fetching
    private void getLoginInfoData() {
        if (id_et.getText().toString().replace(" ","").equals("") || pw_et.getText().toString().replace(" ","").equals("")) {
            Toast.makeText(mContext, "아이디와 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
        }else {
            Log.d(log+"loginEdittExt_3", id_et.getText().toString()+", "+pw_et.getText().toString());
            Retrofit retrofit = RetrofitHelper.getRetrofitInstance();
            RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
            Call<CarInfoListData> call = retrofitAPI.getLoginData(id_et.getText().toString().replace(" ",""), pw_et.getText().toString().replace(" ",""));
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
                                    PreferenceManager.setString(mContext, "auto", "true");
                                }else {
                                    PreferenceManager.setString(mContext, "auto", "false");
                                }
                                PreferenceManager.setString(mContext, "id", str.getUserInfoVOS().get(0).getId());
                                PreferenceManager.setString(mContext, "pw", str.getUserInfoVOS().get(0).getPw());
                                PreferenceManager.setString(mContext, "name", str.getUserInfoVOS().get(0).getName());
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
///////////////////////////////
                @Override
                public void onFailure(Call<CarInfoListData> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    Log.d(log+"onFailure", call.toString()+",  "+t.toString()+",  "+t.getMessage());
                }
            });
        }

    }



}