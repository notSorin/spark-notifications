package com.lesorin.sparknotifications;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lesorin.sparknotifications.services.AppScanningService;

public class ScreenNotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        startService(new Intent(this, AppScanningService.class));
    }
}
