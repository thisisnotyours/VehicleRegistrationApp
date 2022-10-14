package com.thisisnotyours.vehicleregistrationapp.manager;

import android.view.MotionEvent;
import android.view.View;

import com.thisisnotyours.vehicleregistrationapp.R;

public class MyTouchListener implements View.OnTouchListener{
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (view.getId()) {
            //차량조회 화면 views..
            case R.id.btn_search:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.setBackgroundResource(R.drawable.btn_search_box_clicked);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.setBackgroundResource(R.drawable.btn_search_box);
                }
                break;
            case R.id.btn_reset:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.setBackgroundResource(R.drawable.btn_reset_box_clicked);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.setBackgroundResource(R.drawable.btn_reset_box);
                }
                break;
            case R.id.btn_next:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.setBackgroundResource(R.drawable.btn_search_box_clicked);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.setBackgroundResource(R.drawable.btn_search_box);
                }
                break;
            case R.id.btn_back:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.setBackgroundResource(R.drawable.btn_reset_box_clicked);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.setBackgroundResource(R.drawable.btn_reset_box);
                }
                break;


                //차량 등록화면 views..
            case R.id.btn_register:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.setBackgroundResource(R.drawable.btn_search_box_clicked);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.setBackgroundResource(R.drawable.btn_ok_box);
                }
                break;
            case R.id.btn_register_cancel:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.setBackgroundResource(R.drawable.btn_reset_box_clicked);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    view.setBackgroundResource(R.drawable.btn_cancel_box);
                }
                break;
        }

        return false;
    }
}
