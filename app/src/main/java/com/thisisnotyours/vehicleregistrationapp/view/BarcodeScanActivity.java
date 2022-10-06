package com.thisisnotyours.vehicleregistrationapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.thisisnotyours.vehicleregistrationapp.R;

public class BarcodeScanActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener{

    private DecoratedBarcodeView barcodeScannerView;
    private CaptureManager capture;
    private ImageButton flashLightBtn;
    private Boolean switchFlashStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);

        barcodeScannerView = (DecoratedBarcodeView) findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setTorchListener(this);
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();

        flashLightBtn = (ImageButton) findViewById(R.id.iv_flash_light);

        switchFlashStatus = true;

        if (!hasFlash()) {
            flashLightBtn.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        capture.onSaveInstanceState(outState);
    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.iv_flash_light:  //[플래쉬] 버튼
//                if (true) {
//                    switchFlashStatus = true;
//                }else {
//                    switchFlashStatus = false;
//                }
//                switchFlashLight(view);
//                break;
//        }
//    }

    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void switchFlashLight(View view) {
        if (switchFlashStatus == true) {
            barcodeScannerView.setTorchOn();
        }else {
            barcodeScannerView.setTorchOff();
        }
    }

    @Override
    public void onTorchOn() {
        flashLightBtn.setImageResource(R.drawable.ic_baseline_flash_on_24);
        switchFlashStatus = false;
    }

    @Override
    public void onTorchOff() {
        flashLightBtn.setImageResource(R.drawable.ic_baseline_flash_off_24);
        switchFlashStatus = true;
    }
}