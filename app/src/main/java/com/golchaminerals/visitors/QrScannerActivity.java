package com.golchaminerals.visitors;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;


public class QrScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    ZXingScannerView zXingScannerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zXingScannerView =new ZXingScannerView(this);
        zXingScannerView.setAspectTolerance(0.5f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermission()==false) {
                requestPermissions();
            }
            else {
                setContentView(zXingScannerView);
            }

        }


    }
    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(QrScannerActivity.this, CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,new String[ ]{CAMERA},100);

    }
    @Override
    public void onResume() {
        super.onResume();
        zXingScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        zXingScannerView.startCamera();// Start camera on resume
        zXingScannerView.setAutoFocus(true);
        zXingScannerView.setAutoFocus(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        String result =rawResult.getText();
        Intent intent = new Intent(this,IdcardResultActivity.class);
        intent.putExtra("scanresult",result);
        startActivity(intent);
        finish();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode ==100 )
            if(grantResults[0]!=PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"permission abort!",Toast.LENGTH_SHORT ).show();
                onBackPressed();
            }
        else if(grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                setContentView(zXingScannerView);
                Toast.makeText(this,"permission granted",Toast.LENGTH_SHORT ).show();
            }
    }
}
