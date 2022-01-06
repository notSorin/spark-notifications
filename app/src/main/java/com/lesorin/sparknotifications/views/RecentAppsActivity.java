package com.lesorin.sparknotifications.views;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.lesorin.sparknotifications.R;
import com.lesorin.sparknotifications.adapters.RecentAppsAdapter;

public class RecentAppsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recent_apps_layout);

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ListView listView = findViewById(R.id.RecentAppsList);

        listView.setAdapter(new RecentAppsAdapter(this));
        listView.setEmptyView(findViewById(R.id.NoRecentAppsText));
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