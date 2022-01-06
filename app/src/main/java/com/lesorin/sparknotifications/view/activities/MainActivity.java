package com.lesorin.sparknotifications.view.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.lesorin.sparknotifications.R;
import com.lesorin.sparknotifications.view.services.AppScanningService;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        startService(new Intent(this, AppScanningService.class));
    }
}