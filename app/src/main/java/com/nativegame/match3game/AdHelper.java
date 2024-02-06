package com.nativegame.match3game;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.adhelper.AdInitializer;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdHelper extends AppCompatActivity {

    private AdInitializer adInitializer;

    private String geolocation;

    private String adId;

    public class DelayedBackButtonHandler {

        private boolean mIsBackPressed = false;
        private Handler mHandler = new Handler();

        public void onBackPressedWithDelay(int delaySeconds) {
            mIsBackPressed = false;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIsBackPressed = true;
                }
            }, delaySeconds * 1000);
        }

        public boolean isBackPressed() {
            return mIsBackPressed;
        }
    }


    private DelayedBackButtonHandler mBackButtonHandler = new DelayedBackButtonHandler();
    private boolean mIsBackButtonEnabled = false;
    private Handler mHandler = new Handler();

    private LocalTime activityStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activityStart = LocalTime.now();
        }
        mIsBackButtonEnabled = false;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIsBackButtonEnabled = true;
            }
        }, 30000);

        setContentView(R.layout.activity_ad_helper);


        adInitializer = new AdInitializer(AdHelper.this, "your app token", R.id.webView);
        adInitializer.loadPreferences(getPreferences(MODE_PRIVATE));


        if (adId == null) {
            adInitializer.getAdIdFromDevice();
//            ExecutorService executor = Executors.newSingleThreadExecutor();
//            executor.execute(() -> {
//                //Background work here
//                adInitializer.getAdIdFromDevice();
//                this.adId = adInitializer.getAdId();
//            });
        }

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        //    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        //}

        adInitializer.showAd();
    }

    @Override
    public void onBackPressed() {
        if (mIsBackButtonEnabled || mBackButtonHandler.isBackPressed()) {
            finish();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Toast.makeText(this, "Осталось до конца рекламы: " + ChronoUnit.SECONDS.between(LocalTime.now(), activityStart.plusSeconds(30)) , Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (geolocation == null) {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> {
                        //Background work here
                        adInitializer.getGeolocationFromDevice();
                        this.geolocation = adInitializer.getGeolocation();
                    });
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        adInitializer.savePreferences(getPreferences(MODE_PRIVATE));
    }
}
