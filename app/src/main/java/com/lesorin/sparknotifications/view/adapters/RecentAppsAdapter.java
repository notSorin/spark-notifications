package com.lesorin.sparknotifications.view.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lesorin.sparknotifications.BuildConfig;
import com.lesorin.sparknotifications.R;
import com.lesorin.sparknotifications.presenter.RecentApp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RecentAppsAdapter extends BaseAdapter
{
    private Context _context;
    private LayoutInflater _inflater;
    private List<? extends RecentApp> _appsList;
    private SimpleDateFormat _dateFormat;

    public RecentAppsAdapter(Context context)
    {
        _context = context;
        _inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        _appsList = new ArrayList<>();
    }

    @Override
    public int getCount()
    {
        return Math.min(_appsList.size(), 50);
    }

    @Override
    public String getItem(int position)
    {
        return _appsList.get(position).getPackageName();
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ViewHolder holder;

        if(convertView == null)
        {
            convertView = _inflater.inflate(R.layout.recent_app_layout, null);
            holder = new ViewHolder();
            holder.icon = convertView.findViewById(R.id.AppIcon);
            holder.name = convertView.findViewById(R.id.AppName);
            holder.notificationTime = convertView.findViewById(R.id.NotificationTime);

            convertView.setTag(holder);
            convertView.setClickable(true);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        final RecentApp app = _appsList.get(position);

        if(app.isInstalled())
        {
            holder.icon.setImageDrawable(app.getIcon());

            if(app.getPackageName().equals(BuildConfig.APPLICATION_ID))
            {
                holder.name.setText(R.string.DevicePickedUp);
            }
            else
            {
                holder.name.setText(app.getName());
            }

        }
        else
        {
            holder.icon.setImageResource(R.drawable.default_app_icon);
            holder.name.setText(R.string.UninstalledApp);
        }

        String relativeTime = DateUtils.getRelativeTimeSpanString(app.getTimestamp(), System.currentTimeMillis(), 0).toString();
        String formattedDate = _dateFormat.format(app.getTimestamp());
        String notificationTime = String.format(_context.getString(R.string.NotificationTime), relativeTime, formattedDate);

        holder.notificationTime.setText(notificationTime);

        return convertView;
    }

    public void setApps(List<? extends RecentApp> appsList)
    {
        _appsList = appsList;

        notifyDataSetChanged();
    }

    static class ViewHolder
    {
        ImageView icon;
        TextView name;
        TextView notificationTime;
    }
}