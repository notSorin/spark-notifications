package com.lesorin.sparknotifications.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.lesorin.sparknotifications.R;
import com.lesorin.sparknotifications.view.adapters.AppAdapter;
import com.lesorin.sparknotifications.view.services.AppScanningService;

public class AppsActivity extends AppCompatActivity
{
    private AppAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apps_layout);

		if(getSupportActionBar() != null)
		{
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

        mAdapter = new AppAdapter(this);
		ListView listView = findViewById(R.id.AppsList);

		listView.setAdapter(mAdapter);
        startService(new Intent(this, AppScanningService.class));
	}

    @Override
    protected void onDestroy()
	{
        super.onDestroy();
        mAdapter.tearDown();
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == android.R.id.home)
		{
			finish();

			return true;
		}

		return false;
	}
}