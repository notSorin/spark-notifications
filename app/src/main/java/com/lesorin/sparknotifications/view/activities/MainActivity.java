package com.lesorin.sparknotifications.view.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.lesorin.sparknotifications.MainApplication;
import com.lesorin.sparknotifications.R;
import com.lesorin.sparknotifications.presenter.Contract;
import com.lesorin.sparknotifications.view.services.AppScanningService;

public class MainActivity extends AppCompatActivity implements Contract.View
{
    private Contract.PresenterView _presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        startService(new Intent(this, AppScanningService.class));
        ((MainApplication)getApplication()).activityChanged(this);
    }

    @Override
    public void setPresenter(Contract.PresenterView presenter)
    {
        _presenter = presenter;
    }
}