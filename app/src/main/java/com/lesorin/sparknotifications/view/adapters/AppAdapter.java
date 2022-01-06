package com.lesorin.sparknotifications.view.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.SwitchCompat;
import com.lesorin.sparknotifications.R;
import com.lesorin.sparknotifications.presenter.App;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

//TODO try to make it so the adapter doesn't need to implement RealmChangeListener.
public class AppAdapter extends BaseAdapter implements RealmChangeListener<RealmResults<App>>
{
    private Context mContext;
	private LayoutInflater mInflater;
    private Realm mRealm;
	private RealmResults<App> mApps;

	public AppAdapter(Context context)
	{
        mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRealm = Realm.getDefaultInstance();

        getApps();
	}

    public void tearDown()
	{
        mRealm.close();
    }

    private void getApps()
	{
        mApps = mRealm.where(App.class).findAll().sort("name");

        mApps.addChangeListener(this); //TODO is this call really necessary?
        notifyDataSetChanged();
    }

    @Override
    public void onChange(RealmResults<App> element)
	{
        getApps();
    }

	@Override
	public int getCount()
	{
		return mApps.size();
	}

	@Override
	public String getItem(int position)
	{
		return mApps.get(position).getName();
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
			convertView = mInflater.inflate(R.layout.app_layout, null);
			holder = new ViewHolder();
			holder.icon = convertView.findViewById(R.id.AppIcon);
			holder.name = convertView.findViewById(R.id.AppName);
			holder.selected = convertView.findViewById(R.id.AppEnabled);

			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)convertView.getTag();
		}

		App app = mApps.get(position);

		if(app != null)
		{
			Drawable appIcon = app.getIcon(mContext.getPackageManager());

			if(appIcon != null)
			{
				holder.icon.setImageDrawable(appIcon);
			}

			holder.name.setText(app.getName());
			holder.selected.setOnCheckedChangeListener((buttonView, isChecked) ->
			{
				mRealm.beginTransaction();
				app.setEnabled(isChecked);
				mRealm.commitTransaction();
			});

			holder.selected.setChecked(app.getEnabled());
		}
		
		return convertView;
	}

    private class ViewHolder
	{
		ImageView icon;
		TextView name;
		SwitchCompat selected;
	}
}