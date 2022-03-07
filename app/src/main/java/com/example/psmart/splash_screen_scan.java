package com.example.psmart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class splash_screen_scan extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen_scan);

        Handler handler=new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            Intent intent =new Intent(splash_screen_scan.this,ScanFaceActivity.class);
            finish();
            startActivity(intent);
        },2500);
    }
}