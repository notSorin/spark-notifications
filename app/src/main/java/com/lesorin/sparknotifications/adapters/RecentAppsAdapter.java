package com.lesorin.sparknotifications.adapters;

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
import com.lesorin.sparknotifications.helpers.AppHelper;
import com.lesorin.sparknotifications.presenter.RecentApp;
import java.text.SimpleDateFormat;
import io.realm.RealmResults;

public class RecentAppsAdapter extends BaseAdapter
{
    private Context mContext;
    private LayoutInflater mInflater;
    private RealmResults<RecentApp> mApps;

    public RecentAppsAdapter(Context context)
    {
        mContext = context;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mApps = AppHelper.getRecentNotifyingApps();
    }

    @Override
    public int getCount()
    {
        return Math.min(mApps.size(), 50);
    }

    @Override
    public String getItem(int position)
    {
        return mApps.get(position).getPackageName();
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
            convertView = mInflater.inflate(R.layout.recent_app_layout, null);
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

        final RecentApp app = mApps.get(position);

        RecentApp.fetchInformation(app, mContext);

        if(app.isInstalled())
        {
            holder.icon.setImageDrawable(app.getIcon());

            if(app.getPackageName().equals(BuildConfig.APPLICATION_ID))
            {
                holder.name.setText(R.string.picked_up);
            }
            else
            {
                holder.name.setText(app.getName());
            }

        }
        else
        {
            holder.icon.setImageResource(R.mipmap.ic_launcher);
            holder.name.setText(R.string.uninstalled_app);
        }

        String relativeTime = DateUtils.getRelativeTimeSpanString(app.getTimestamp(), System.currentTimeMillis(), 0).toString();
        String formattedDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(app.getTimestamp());

        holder.notificationTime.setText(relativeTime + " (" + formattedDate + ")"); //TODO fix after switching to MVP.

        return convertView;
    }

    static class ViewHolder
    {
        ImageView icon;
        TextView name;
        TextView notificationTime;
    }
}