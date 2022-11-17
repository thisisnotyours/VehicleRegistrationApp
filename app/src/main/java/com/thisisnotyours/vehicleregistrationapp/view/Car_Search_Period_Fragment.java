package com.thisisnotyours.vehicleregistrationapp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.thisisnotyours.vehicleregistrationapp.R;
import com.thisisnotyours.vehicleregistrationapp.handler.IOnBackPressed;


public class Car_Search_Period_Fragment extends Fragment implements IOnBackPressed, View.OnClickListener {

    //defind view id's..
    private void findViewIds(View v) {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_car_search_period, container, false);

        findViewIds(rootView);

        return rootView;

    }//onCreateView


    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View view) {

    }//onClick


}
