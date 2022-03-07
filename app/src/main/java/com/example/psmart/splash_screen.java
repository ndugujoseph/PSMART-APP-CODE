package com.example.psmart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class splash_screen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        Handler handler=new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            Intent intent =new Intent(splash_screen.this,NewFaceActivity.class);
            finish();
            startActivity(intent);
        },2500);
    }

    public static class SendPostReqAsyncTask {
    }
}