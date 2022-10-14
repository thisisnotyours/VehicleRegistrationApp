package com.thisisnotyours.vehicleregistrationapp.handler;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class BackPressedKeyHandler {

    private long backKeyPressedTime = 0;
    private Activity mActivity;
    private Toast toast;

    public BackPressedKeyHandler(Activity mActivity) {
        this.mActivity = mActivity;
    }


    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {

            mActivity.moveTaskToBack(true);

            if (Build.VERSION.SDK_INT >= 21) {
                mActivity.finishAndRemoveTask();
                android.os.Process.killProcess(android.os.Process.myPid());
            }else {
                mActivity.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }

            toast.cancel();
        }

    }

    public boolean showGuide() {
        toast = Toast.makeText(mActivity, "뒤로가기 버튼을 한번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
        return true;
    }

}
